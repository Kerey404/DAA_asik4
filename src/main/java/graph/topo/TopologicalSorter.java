package graph.topo;

import graph.core.Graph;
import graph.core.Metrics;

import java.util.*;
/**
 * Implements Kahn's algorithm for topological sorting of a DAG.
 *
 * <p>Returns a valid topological order of all vertices.
 * Also tracks operation metrics (pushes, pops, time).
 */


public class TopologicalSorter {
    private final Metrics metrics;

    public TopologicalSorter(Metrics metrics) { this.metrics = metrics; }

    public List<Integer> topoSort(Graph dag) {
        long t0 = System.nanoTime();
        int n = dag.n();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) for (Graph.Edge e : dag.adj().get(u)) indeg[e.v]++;

        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) { q.add(i); metrics.pushes++; }

        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.remove(); metrics.pops++;
            order.add(u);
            for (Graph.Edge e : dag.adj().get(u)) {
                if (--indeg[e.v] == 0) { q.add(e.v); metrics.pushes++; }
            }
        }
        metrics.timeNs = System.nanoTime() - t0;
        if (order.size() != n) throw new IllegalStateException("Graph is not a DAG (cycle exists).");
        return order;
    }
    public Metrics getMetrics() { return metrics; }
}
