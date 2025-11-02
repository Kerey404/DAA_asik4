package graph.dagsp;

import graph.core.Graph;
import graph.core.Metrics;

import java.util.*;


public class DAGShortestPath {
    private final Metrics metrics;

    public DAGShortestPath(Metrics metrics) { this.metrics = metrics; }

    public static class Result {
        public final double[] dist;
        public final int[] parent;
        public Result(double[] dist, int[] parent) { this.dist = dist; this.parent = parent; }
    }


    public Result shortestPaths(Graph dag, int source, List<Integer> topo) {
        long t0 = System.nanoTime();
        int n = dag.n();
        double INF = 1e100;
        double[] dist = new double[n];
        Arrays.fill(dist, INF);
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        dist[source] = 0.0;

        // process vertices in topo order
        for (int u : topo) {
            if (dist[u] >= INF / 2) continue;
            for (Graph.Edge e : dag.adj().get(u)) {
                if (dist[e.v] > dist[u] + e.w) {
                    dist[e.v] = dist[u] + e.w;
                    parent[e.v] = u;
                    metrics.relaxations++;
                }
            }
        }
        metrics.timeNs = System.nanoTime() - t0;
        return new Result(dist, parent);
    }


    public Result longestPath(Graph dag, List<Integer> topo) {
        long t0 = System.nanoTime();
        int n = dag.n();
        double NEG = -1e100;
        double[] dp = new double[n];
        Arrays.fill(dp, NEG);
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        for (int v : topo) {
            if (dp[v] == NEG) dp[v] = 0.0; // new chain start
            for (Graph.Edge e : dag.adj().get(v)) {
                if (dp[e.v] < dp[v] + e.w) {
                    dp[e.v] = dp[v] + e.w;
                    parent[e.v] = v;
                    metrics.relaxations++;
                }
            }
        }
        metrics.timeNs = System.nanoTime() - t0;
        return new Result(dp, parent);
    }

    public static List<Integer> reconstruct(int target, int[] parent) {
        List<Integer> path = new ArrayList<>();
        for (int v = target; v != -1; v = parent[v]) path.add(v);
        Collections.reverse(path);
        return path;
    }
}
