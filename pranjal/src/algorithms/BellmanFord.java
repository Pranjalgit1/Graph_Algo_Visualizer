package algorithms;

import graph.Graph;
import step.Step;
import step.StepType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BellmanFord {

    public static List<Step> run(Graph graph, int start) {
        List<Step> steps = new ArrayList<>();

        Map<Integer, Integer> dist = new HashMap<>();
        List<Integer> vertices = new ArrayList<>(graph.getVertices());
        int V = vertices.size();

        for (int v : vertices) {
            dist.put(v, 99999);
        }
        dist.put(start, 0);
        steps.add(Step.nodeStep(StepType.VISIT_NODE, start));

        List<int[]> allEdges = new ArrayList<>();
        for (int u : vertices) {
            for (int[] neighbor : graph.getNeighbors(u)) {
                allEdges.add(new int[] { u, neighbor[0], neighbor[1] });
            }
        }

        for (int pass = 0; pass < V - 1; pass++) {
            for (int[] edge : allEdges) {
                int u = edge[0];
                int v = edge[1];
                int w = edge[2];

                steps.add(Step.edgeStep(StepType.EXPLORE_EDGE, u, v));

                if (dist.get(u) != 99999 && dist.get(u) + w < dist.get(v)) {
                    dist.put(v, dist.get(u) + w);
                    steps.add(Step.edgeStep(StepType.RELAX_EDGE, u, v));
                    steps.add(Step.nodeStep(StepType.VISIT_NODE, v));
                } else {
                    steps.add(Step.edgeStep(StepType.NO_UPDATE, u, v));
                }
            }

            steps.add(Step.nodeStep(StepType.PROCESS_NODE, pass < V ? vertices.get(pass) : start));
        }

        boolean hasNegativeCycle = false;
        for (int[] edge : allEdges) {
            int u = edge[0];
            int v = edge[1];
            int w = edge[2];

            if (dist.get(u) != 99999 && dist.get(u) + w < dist.get(v)) {
                steps.add(Step.nodeStep(StepType.NEGATIVE_CYCLE, v));
                hasNegativeCycle = true;
            }
        }

        if (!hasNegativeCycle) {
            for (int v : vertices) {
                steps.add(Step.nodeStep(StepType.PROCESS_NODE, v));
            }
        }

        return steps;
    }
}
