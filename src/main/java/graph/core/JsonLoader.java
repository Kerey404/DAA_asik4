package graph.core;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;


public class JsonLoader {
    public static class InputGraph {
        public boolean directed = true;
        public int n;
        public Edge[] edges;
        public Integer source;
        public String weight_model;
        public static class Edge { public int u, v; public double w; }
    }

    public static class Loaded {
        public final Graph g;
        public final Integer source;
        public final String weightModel;
        public Loaded(Graph g, Integer s, String wm) { this.g = g; this.source = s; this.weightModel = wm; }
    }

    public static Loaded load(String path) throws IOException {
        String json = Files.readString(Path.of(path));
        Gson gson = new Gson();
        InputGraph ig = gson.fromJson(json, InputGraph.class);
        if (ig == null) throw new IOException("Invalid JSON or empty file: " + path);
        Graph g = new Graph(ig.n, ig.directed);
        if (ig.edges != null) {
            for (InputGraph.Edge e : ig.edges) g.addEdge(e.u, e.v, e.w);
        }
        return new Loaded(g, ig.source, ig.weight_model);
    }
}
