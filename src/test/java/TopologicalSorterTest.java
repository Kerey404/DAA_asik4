import graph.core.Graph;
import graph.core.Metrics;
import graph.topo.TopologicalSorter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TopologicalSorterTest {
    @Test
    public void testSimpleDAG() {
        Graph dag = new Graph(4, true);
        dag.addEdge(0,1,1); dag.addEdge(1,2,1); dag.addEdge(0,3,1);
        TopologicalSorter t = new TopologicalSorter(new Metrics());
        var order = t.topoSort(dag);
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(1) < order.indexOf(2));
    }
}
