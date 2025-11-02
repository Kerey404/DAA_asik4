import graph.core.Graph;
import graph.core.Metrics;
import graph.scc.SCCFinder;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SCCFinderTest {

    @Test
    public void testSimpleCycle() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);

        SCCFinder scc = new SCCFinder(new Metrics());
        List<List<Integer>> comps = scc.findSCCs(g);

        assertEquals(1, scc.getComponentCount(), "All vertices form one SCC");
        assertEquals(3, comps.get(0).size());
    }

    @Test
    public void testDisconnectedGraph() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1);
        // node 2 isolated
        SCCFinder scc = new SCCFinder(new Metrics());
        scc.findSCCs(g);
        assertEquals(3, scc.getComponentCount());
    }
}
