import graph.core.Graph;
import graph.core.Metrics;
import graph.dagsp.DAGShortestPath;
import graph.topo.TopologicalSorter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DAGShortestPathTest {
    @Test
    public void testSPandLP() {
        Graph dag = new Graph(5, true);
        dag.addEdge(0,1,2); dag.addEdge(0,2,1); dag.addEdge(2,3,2); dag.addEdge(1,3,2); dag.addEdge(3,4,3);
        var topo = new TopologicalSorter(new Metrics()).topoSort(dag);
        var sp = new DAGShortestPath(new Metrics()).shortestPaths(dag, 0, topo);
        assertEquals(0.0, sp.dist[0]);
        assertEquals(1.0, sp.dist[2]);
        assertEquals(3.0, sp.dist[3]);
        assertEquals(6.0, sp.dist[4]);
        var lp = new DAGShortestPath(new Metrics()).longestPath(dag, topo);
        double max = Double.NEGATIVE_INFINITY;
        for (double d : lp.dist) if (d > max) max = d;
        assertEquals(7.0, max, 1e-9);
    }
}
