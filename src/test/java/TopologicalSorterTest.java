import graph.core.Graph;
import graph.core.Metrics;
import graph.topo.TopologicalSorter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TopologicalSorterTest {

    @Test
    public void testSimpleDAG() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(0, 3, 1);

        TopologicalSorter sorter = new TopologicalSorter(new Metrics());
        List<Integer> order = sorter.topoSort(g);

        // Ensure topological order is valid: 0 before 1, 1 before 2, 0 before 3
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(1) < order.indexOf(2));
        assertTrue(order.indexOf(0) < order.indexOf(3));
    }

    @Test
    public void testSingleNode() {
        Graph g = new Graph(1, true);
        TopologicalSorter sorter = new TopologicalSorter(new Metrics());
        List<Integer> order = sorter.topoSort(g);
        assertEquals(List.of(0), order);
    }
}
