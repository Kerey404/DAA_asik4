package graph;

import graph.core.*;
import graph.scc.SCCFinder;
import graph.topo.TopologicalSorter;
import graph.dagsp.DAGShortestPath;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.*;

public class Main {
    private static void log(PrintWriter out, String msg) {
        System.out.println(msg);
        out.println(msg);
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Usage: java -jar assignment4.jar <data.json>");
            System.exit(1);
        }

        String inputPathStr = args[0];
        Path inputPath = Paths.get(inputPathStr);
        String inputName = inputPath.getFileName().toString().replaceFirst("\\.json$", "");
        Path outputDir = Paths.get("output");

        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            System.err.println("Can't create output dir: " + e.getMessage());
            System.exit(2);
        }

        Path outFile = outputDir.resolve("results_" + inputName + ".txt");
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(outFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
            JsonLoader.Loaded loaded;
            try { loaded = JsonLoader.load(inputPathStr); } catch (IOException ioe) { log(out, "Failed to load input: " + ioe.getMessage()); return; }

            Graph g = loaded.g;
            Integer src = loaded.source;
            String wm = loaded.weightModel;
            log(out, String.format("Loaded: %s (n=%d, edges=%d, directed=%b, weight_model=%s, source=%s)",
                    inputName, g.n(), g.edges().size(), g.directed(), wm, src));

            // SCC
            Metrics sccMetrics = new Metrics();
            SCCFinder scc = new SCCFinder(sccMetrics);
            List<List<Integer>> comps = scc.findSCCs(g);
            int[] compId = scc.getComponentIds();
            int compCount = scc.getComponentCount();
            log(out, "");
            log(out, "=== SCC ===");
            log(out, "SCC count = " + compCount);
            for (int i = 0; i < comps.size(); i++) {
                List<Integer> c = new ArrayList<>(comps.get(i));
                Collections.sort(c);
                log(out, String.format("  SCC#%d (size=%d): %s", i, c.size(), c));
            }
            log(out, String.format("SCC metrics: dfsCalls=%d, edgesVisited=%d, timeMs=%.3f",
                    sccMetrics.dfsCalls, sccMetrics.edgesVisited, sccMetrics.timeNs / 1e6));

            // condensation DAG (min weights for shortest)
            Graph condMin = Graph.buildCondensation(g, compId, compCount, false);
            // condensation DAG (max weights for longest)
            Graph condMax = Graph.buildCondensation(g, compId, compCount, true);

            log(out, "");
            log(out, "=== Condensation (min-weights for SP) ===");
            log(out, String.format("components=%d, edges=%d", condMin.n(), condMin.edges().size()));
            for (int u = 0; u < condMin.n(); u++) {
                List<String> outs = new ArrayList<>();
                for (Graph.Edge e : condMin.adj().get(u)) outs.add(String.format("%d(w=%.3f)", e.v, e.w));
                log(out, String.format("  %d -> %s", u, outs));
            }

            // topological order on condensation (condMin and condMax share structure)
            Metrics topoMetrics = new Metrics();
            TopologicalSorter topo = new TopologicalSorter(topoMetrics);
            List<Integer> compOrder = topo.topoSort(condMin);

            log(out, "");
            log(out, "=== Topological order of components ===");
            log(out, compOrder.toString());
            log(out, String.format("Topo metrics: pushes=%d, pops=%d, timeMs=%.3f",
                    topoMetrics.pushes, topoMetrics.pops, topoMetrics.timeNs / 1e6));

            // derived order of original tasks:
            List<Integer> derived = new ArrayList<>();
            for (int cid : compOrder) {
                List<Integer> members = new ArrayList<>();
                for (int v = 0; v < g.n(); v++) if (compId[v] == cid) members.add(v);
                Collections.sort(members);
                derived.addAll(members);
            }
            log(out, "");
            log(out, "Derived tasks order (expand SCCs in topo order):");
            log(out, derived.toString());

            // shortest paths on condMin
            log(out, "");
            log(out, "=== Shortest paths on condensation (single-source) ===");
            if (src != null) {
                int sComp = compId[src];
                DAGShortestPath dspSP = new DAGShortestPath(new Metrics());
                var spRes = dspSP.shortestPaths(condMin, sComp, compOrder);
                log(out, String.format("Source original vertex=%d -> component=%d", src, sComp));
                for (int i = 0; i < spRes.dist.length; i++) {
                    String s = Double.isInfinite(spRes.dist[i]) ? "INF" : String.format("%.3f", spRes.dist[i]);
                    log(out, String.format(" comp %d : %s", i, s));
                }
                // reconstruct a shortest path example to farthest reachable
                int any = -1;
                double maxd = Double.NEGATIVE_INFINITY;
                for (int i = 0; i < spRes.dist.length; i++) {
                    if (!Double.isInfinite(spRes.dist[i]) && spRes.dist[i] > maxd) { maxd = spRes.dist[i]; any = i; }
                }
                if (any != -1 && any != sComp) {
                    List<Integer> path = DAGShortestPath.reconstructShortestPath(any, spRes.parent);
                    log(out, String.format("Example shortest path to comp %d: %s  (length=%.3f)", any, path, spRes.dist[any]));
                } else {
                    log(out, "No reachable other components from source.");
                }
                log(out, String.format("SP metrics: relaxations=%d, timeMs=%.3f",
                        dspSP.getMetrics().relaxations, dspSP.getMetrics().timeNs / 1e6));
            } else {
                log(out, "No source specified in JSON input; skipping shortest paths.");
            }

            // longest path on condMax (critical path)
            log(out, "");
            log(out, "=== Longest (critical) path on condensation ===");
            DAGShortestPath dspLP = new DAGShortestPath(new Metrics());
            var lpRes = dspLP.longestPath(condMax, compOrder);
            // find best sink
            int best = 0;
            for (int i = 1; i < lpRes.dist.length; i++) if (lpRes.dist[i] > lpRes.dist[best]) best = i;
            double bestLen = lpRes.dist[best];
            List<Integer> crit = DAGShortestPath.reconstructPath(best, lpRes.parent);
            log(out, String.format("Critical path (components): %s, length=%.3f", crit, bestLen));
            log(out, String.format("LP metrics: relaxations=%d, timeMs=%.3f",
                    dspLP.getMetrics().relaxations, dspLP.getMetrics().timeNs / 1e6));

            // final metrics summary
            log(out, "");
            log(out, "=== Final Metrics Summary ===");
            log(out, String.format("SCC: dfsCalls=%d, edgesVisited=%d, timeMs=%.3f",
                    sccMetrics.dfsCalls, sccMetrics.edgesVisited, sccMetrics.timeNs / 1e6));
            log(out, String.format("Topo: pushes=%d, pops=%d, timeMs=%.3f",
                    topoMetrics.pushes, topoMetrics.pops, topoMetrics.timeNs / 1e6));
            log(out, String.format("SP: relaxations=%d, timeMs=%.3f",
                    (src!=null?new DAGShortestPath(new Metrics()).getMetrics().relaxations:0), 0.0));
            log(out, String.format("LP: relaxations=%d, timeMs=%.3f",
                    dspLP.getMetrics().relaxations, dspLP.getMetrics().timeNs / 1e6));

            log(out, "");
            log(out, "Results saved to: " + outFile.toAbsolutePath().toString());
            out.flush();

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
            System.exit(3);
        } catch (Exception ex) {
            System.err.println("Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(4);
        }
    }
}
