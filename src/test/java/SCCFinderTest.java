import graph.core.Graph;
import graph.core.Metrics;
import graph.scc.SCCFinder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class SCCFinderTest {
    @Test
    public void testSimpleCycle() {
        Graph g = new Graph(4, true);
        g.addEdge(0,1,1); g.addEdge(1,2,1); g.addEdge(2,0,1); g.addEdge(2,3,1);
        Metrics m = new Metrics();
        SCCFinder f = new SCCFinder(m);
        var comps = f.findSCCs(g);
        assertEquals(2, comps.size());
        boolean has3 = comps.stream().anyMatch(c -> c.size() == 3);
        boolean has1 = comps.stream().anyMatch(c -> c.size() == 1);
        assertTrue(has3 && has1);
    }
}
