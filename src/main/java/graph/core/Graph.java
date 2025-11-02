package graph.core;

import java.util.*;
/**
 * Basic directed/undirected weighted graph class.
 * Supports adjacency list representation and condensation building.
 */

public class Graph {
    public static class Edge {
        public final int u, v;
        public final double w;
        public Edge(int u, int v, double w) { this.u = u; this.v = v; this.w = w; }
        @Override public String toString() { return String.format("(%d->%d, w=%.3f)", u, v, w); }
    }

    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adj;
    private final List<Edge> edges;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        this.edges = new ArrayList<>();
    }

    public int n() { return n; }
    public boolean directed() { return directed; }
    public List<List<Edge>> adj() { return adj; }
    public List<Edge> edges() { return edges; }

    public void addEdge(int u, int v, double w) {
        Edge e = new Edge(u, v, w);
        adj.get(u).add(e);
        edges.add(e);
        if (!directed) adj.get(v).add(new Edge(v, u, w));
    }


    public static Graph buildCondensation(Graph g, int[] compId, int compCount, boolean useMax) {
        Graph dag = new Graph(compCount, true);
        Map<Long, Double> wmap = new HashMap<>();
        for (Edge e : g.edges()) {
            int a = compId[e.u], b = compId[e.v];
            if (a == b) continue;
            long key = (((long) a) << 32) | (b & 0xffffffffL);
            if (useMax) {
                double cur = wmap.getOrDefault(key, Double.NEGATIVE_INFINITY);
                wmap.put(key, Math.max(cur, e.w));
            } else {
                double cur = wmap.getOrDefault(key, Double.POSITIVE_INFINITY);
                wmap.put(key, Math.min(cur, e.w));
            }
        }
        for (Map.Entry<Long, Double> ent : wmap.entrySet()) {
            int a = (int) (ent.getKey() >>> 32);
            int b = (int) (ent.getKey().longValue() & 0xffffffffL);
            dag.addEdge(a, b, ent.getValue());
        }
        return dag;
    }
}
