package graph;

import graph.core.*;
import graph.scc.SCCFinder;
import graph.topo.TopologicalSorter;
import graph.dagsp.DAGShortestPath;
import java.util.*;


public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java -jar assignment4.jar <data.json>");
            return;
        }

        String path = args[0];
        JsonLoader.Loaded loaded = JsonLoader.load(path);
        Graph g = loaded.g;
        Integer src = loaded.source;
        String wm = loaded.weightModel;

        System.out.printf("Loaded: n=%d, edges=%d, directed=%b, weight_model=%s, source=%s%n",
                g.n(), g.edges().size(), g.directed(), wm, src);

        // 1) SCC (Tarjan)
        Metrics sccM = new Metrics();
        SCCFinder sccFinder = new SCCFinder(sccM);
        var comps = sccFinder.findSCCs(g);
        int[] compId = sccFinder.getComponentIds();
        int compCount = sccFinder.getComponentCount();

        System.out.println("SCC count = " + compCount);
        for (int i = 0; i < comps.size(); i++) {
            System.out.printf("  SCC#%d (size=%d): %s%n", i, comps.get(i).size(), comps.get(i));
        }
        System.out.printf("SCC metrics: dfsCalls=%d, edgesVisited=%d, timeMs=%.3f%n",
                sccM.dfsCalls, sccM.edgesVisited, sccM.timeNs / 1e6);

        // 2) Build condensation DAG
        Graph cond = Graph.buildCondensation(g, compId, compCount);

        // 3) Topological order on condensation DAG
        Metrics topoM = new Metrics();
        TopologicalSorter topoSorter = new TopologicalSorter(topoM);
        var compOrder = topoSorter.topoSort(cond);
        System.out.println("Topological order of components: " + compOrder);
        System.out.printf("Topo metrics: pushes=%d, pops=%d, timeMs=%.3f%n",
                topoM.pushes, topoM.pops, topoM.timeNs / 1e6);

        // Derived order of original tasks (expand each component in topological order)
        List<Integer> derived = new ArrayList<>();
        for (int cid : compOrder) {
            List<Integer> members = new ArrayList<>();
            for (int v = 0; v < g.n(); v++) if (compId[v] == cid) members.add(v);
            Collections.sort(members);
            derived.addAll(members);
        }
        System.out.println("Derived task order after SCC compression: " + derived);

        // 4) DAG shortest & longest paths on condensation DAG
        Metrics spM = new Metrics();
        DAGShortestPath dsp = new DAGShortestPath(spM);

        if (src != null) {
            int sComp = compId[src];
            var spRes = dsp.shortestPaths(cond, sComp, compOrder);
            System.out.println("Shortest distances from component(" + sComp + "):");
            System.out.println(Arrays.toString(spRes.dist));
            System.out.printf("SP metrics: relaxations=%d, timeMs=%.3f%n",
                    spM.relaxations, spM.timeNs / 1e6);
        } else {
            System.out.println("No source provided; skipping shortest paths.");
        }

        // Longest (critical) path on condensation DAG
        Metrics lpM = new Metrics();
        DAGShortestPath dlp = new DAGShortestPath(lpM);
        var lpRes = dlp.longestPath(cond, compOrder);
        // find best node
        int best = 0;
        for (int i = 1; i < cond.n(); i++) if (lpRes.dist[i] > lpRes.dist[best]) best = i;
        var critPath = DAGShortestPath.reconstruct(best, lpRes.parent);
        System.out.printf("Critical path (components): %s, length=%.3f%n", critPath, lpRes.dist[best]);
        System.out.printf("LP metrics: relaxations=%d, timeMs=%.3f%n", lpM.relaxations, lpM.timeNs / 1e6);
    }
}
