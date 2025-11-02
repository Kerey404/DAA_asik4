import graph.core.Graph;
import graph.core.Metrics;
import graph.dagsp.DAGShortestPath;
import graph.topo.TopologicalSorter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DAGShortestPathTest {

    @Test
    public void testShortestPath() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 2);
        g.addEdge(1, 2, 2);
        g.addEdge(0, 2, 5);

        TopologicalSorter topo = new TopologicalSorter(new Metrics());
        List<Integer> order = topo.topoSort(g);

        DAGShortestPath dsp = new DAGShortestPath(new Metrics());
        var res = dsp.shortestPaths(g, 0, order);

        assertEquals(0.0, res.dist[0]);
        assertEquals(2.0, res.dist[1]);
        assertEquals(4.0, res.dist[2]);
    }

    @Test
    public void testLongestPath() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);

        TopologicalSorter topo = new TopologicalSorter(new Metrics());
        List<Integer> order = topo.topoSort(g);

        DAGShortestPath dsp = new DAGShortestPath(new Metrics());
        var res = dsp.longestPath(g, order);

        assertEquals(3.0, res.dist[2]);
    }
}
