package graph.core;

public class Metrics {
    public long timeNs = 0L;
    // SCC
    public long dfsCalls = 0L;
    public long edgesVisited = 0L;
    // Topo (Kahn)
    public long pushes = 0L;
    public long pops = 0L;
    // DAG-SP
    public long relaxations = 0L;

    public void reset() {
        timeNs = dfsCalls = edgesVisited = pushes = pops = relaxations = 0L;
    }
}
