package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphVisualData {

    private final Map<Integer, VisualNode> nodeMap;
    private final List<VisualEdge> edges;

    public GraphVisualData() {
        this.nodeMap = new HashMap<>();
        this.edges = new ArrayList<>();
    }

    public void addNode(int id, double x, double y) {
        nodeMap.put(id, new VisualNode(id, x, y));
    }

    public VisualNode getNode(int id) {
        return nodeMap.get(id);
    }

    public Map<Integer, VisualNode> getAllNodes() {
        return nodeMap;
    }

    public void addEdge(int u, int v, int weight) {
        edges.add(new VisualEdge(u, v, weight));
    }

    public void addEdge(int u, int v) {
        edges.add(new VisualEdge(u, v));
    }

    public List<VisualEdge> getAllEdges() {
        return edges;
    }

    public void buildFromGraph(Graph graph, double centerX, double centerY, double radius) {
        nodeMap.clear();
        edges.clear();

        List<Integer> vertices = new ArrayList<>(graph.getVertices());
        int n = vertices.size();

        if (n <= 12) {

            for (int i = 0; i < n; i++) {
                double angle = 2 * Math.PI * i / n - Math.PI / 2;
                double x = centerX + radius * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);
                addNode(vertices.get(i), x, y);
            }
        } else if (n <= 20) {

            int innerCount = Math.max(2, n / 3);
            int outerCount = n - innerCount;

            double innerR = radius * 0.38;
            double outerR = radius * 0.92;

            int idx = 0;

            for (int i = 0; i < innerCount && idx < n; i++, idx++) {
                double angle = 2 * Math.PI * i / innerCount - Math.PI / 2;
                addNode(vertices.get(idx),
                        centerX + innerR * Math.cos(angle),
                        centerY + innerR * Math.sin(angle));
            }

            double outerOffset = Math.PI / outerCount;
            for (int i = 0; i < outerCount && idx < n; i++, idx++) {
                double angle = 2 * Math.PI * i / outerCount - Math.PI / 2 + outerOffset;
                addNode(vertices.get(idx),
                        centerX + outerR * Math.cos(angle),
                        centerY + outerR * Math.sin(angle));
            }
        } else {

            int innerCount = Math.max(1, n / 6);
            int middleCount = Math.max(1, n / 3);
            int outerCount = n - innerCount - middleCount;

            double innerR = radius * 0.30;
            double middleR = radius * 0.62;
            double outerR = radius * 0.95;

            int idx = 0;

            for (int i = 0; i < innerCount && idx < n; i++, idx++) {
                double angle = 2 * Math.PI * i / innerCount - Math.PI / 2;
                addNode(vertices.get(idx),
                        centerX + innerR * Math.cos(angle),
                        centerY + innerR * Math.sin(angle));
            }

            for (int i = 0; i < middleCount && idx < n; i++, idx++) {
                double angle = 2 * Math.PI * i / middleCount - Math.PI / 2;
                addNode(vertices.get(idx),
                        centerX + middleR * Math.cos(angle),
                        centerY + middleR * Math.sin(angle));
            }

            for (int i = 0; i < outerCount && idx < n; i++, idx++) {
                double angle = 2 * Math.PI * i / outerCount - Math.PI / 2;
                addNode(vertices.get(idx),
                        centerX + outerR * Math.cos(angle),
                        centerY + outerR * Math.sin(angle));
            }
        }

        for (int v : vertices) {
            for (int[] neighbor : graph.getNeighbors(v)) {
                int dest = neighbor[0];
                int weight = neighbor[1];

                if (graph.isDirected()) {
                    addEdge(v, dest, weight);
                } else {
                    if (v < dest) {
                        addEdge(v, dest, weight);
                    }
                }
            }
        }
    }

    public void print() {
        System.out.println("Nodes:");
        for (VisualNode node : nodeMap.values()) {
            System.out.println("  " + node);
        }
        System.out.println("Edges:");
        for (VisualEdge edge : edges) {
            System.out.println("  " + edge);
        }
    }
}
