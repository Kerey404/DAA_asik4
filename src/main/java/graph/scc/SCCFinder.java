package graph.scc;

import graph.core.Graph;
import graph.core.Metrics;

import java.util.*;


public class SCCFinder {
    private final Metrics metrics;
    private int time;
    private int[] disc, low, compId;
    private boolean[] inStack;
    private Deque<Integer> stack;
    private int compCount;

    public SCCFinder(Metrics metrics) { this.metrics = metrics; }


    public List<List<Integer>> findSCCs(Graph g) {
        long t0 = System.nanoTime();
        int n = g.n();
        disc = new int[n]; Arrays.fill(disc, -1);
        low = new int[n];
        inStack = new boolean[n];
        stack = new ArrayDeque<>();
        compId = new int[n]; Arrays.fill(compId, -1);
        compCount = 0; time = 0;

        for (int v = 0; v < n; v++) {
            if (disc[v] == -1) dfs(g, v);
        }
        metrics.timeNs = System.nanoTime() - t0;

        List<List<Integer>> comps = new ArrayList<>();
        for (int i = 0; i < compCount; i++) comps.add(new ArrayList<>());
        for (int v = 0; v < n; v++) comps.get(compId[v]).add(v);
        return comps;
    }

    private void dfs(Graph g, int u) {
        metrics.dfsCalls++;
        disc[u] = low[u] = time++;
        stack.push(u); inStack[u] = true;
        for (Graph.Edge e : g.adj().get(u)) {
            metrics.edgesVisited++;
            int v = e.v;
            if (disc[v] == -1) {
                dfs(g, v);
                low[u] = Math.min(low[u], low[v]);
            } else if (inStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
        if (low[u] == disc[u]) {
            while (true) {
                int v = stack.pop();
                inStack[v] = false;
                compId[v] = compCount;
                if (v == u) break;
            }
            compCount++;
        }
    }

    public int[] getComponentIds() { return compId; }
    public int getComponentCount() { return compCount; }
}
