package graph.core;

import java.util.*;


public class Graph {
    public static class Edge {
        public final int u, v;
        public final double w;
        public Edge(int u, int v, double w) { this.u = u; this.v = v; this.w = w; }
    }

    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adj;
    private final List<Edge> edges;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>();
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

    public static Graph buildCondensation(Graph g, int[] compId, int compCount) {
        Graph dag = new Graph(compCount, true);
        // track min weight for edge between components
        Map<Long, Double> minW = new HashMap<>();
        for (Edge e : g.edges()) {
            int a = compId[e.u], b = compId[e.v];
            if (a != b) {
                long key = (((long) a) << 32) | (b & 0xffffffffL);
                minW.put(key, Math.min(minW.getOrDefault(key, Double.POSITIVE_INFINITY), e.w));
            }
        }
        for (Map.Entry<Long, Double> ent : minW.entrySet()) {
            int a = (int) (ent.getKey() >>> 32);
            int b = (int) (ent.getKey().longValue() & 0xffffffffL);
            dag.addEdge(a, b, ent.getValue());
        }
        return dag;
    }
}
