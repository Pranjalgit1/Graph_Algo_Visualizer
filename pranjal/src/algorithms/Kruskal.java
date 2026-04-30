package algorithms;

import graph.Graph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import step.Step;
import step.StepType;

public class Kruskal {

    public static List<Step> run(Graph graph) {
        List<Step> steps = new ArrayList<>();

        List<int[]> edges = new ArrayList<>();
        for (int u : graph.getVertices()) {
            for (int[] neigh : graph.getNeighbors(u)) {
                int v = neigh[0];
                int w = neigh[1];

                if (u < v) {
                    edges.add(new int[] { u, v, w });
                }
            }
        }

        
        Collections.sort(edges, Comparator.comparingInt(e -> e[2]));

        int maxVertex = 0;
        for (int v : graph.getVertices()) {
            maxVertex = Math.max(maxVertex, v);
        }
        DisjointSet ds = new DisjointSet(maxVertex + 1);

        int selectedCount = 0;
        int totalVertices = graph.getVertices().size();

        for (int[] e : edges) {
            int u = e[0];
            int v = e[1];
            int w = e[2];

            
            steps.add(Step.edgeStep(StepType.EDGE_CONSIDERED, u, v));

            if (ds.union(u, v)) {
                
                steps.add(Step.edgeStep(StepType.EDGE_SELECTED, u, v));
                selectedCount++;

                if (selectedCount == totalVertices - 1) {
                    break;
                }
            } else {
                
                steps.add(Step.edgeStep(StepType.EDGE_REJECTED, u, v));
            }
        }

        return steps;
    }
}
