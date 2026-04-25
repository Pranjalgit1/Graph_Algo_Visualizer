package algorithms;

import graph.Graph;
import step.Step;
import step.StepType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra {

    /** Run Dijkstra from start. Pass dest = -1 to settle all reachable nodes. */
    public static List<Step> run(Graph graph, int start) {
        return run(graph, start, -1);
    }

    public static List<Step> run(Graph graph, int start, int dest) {
        List<Step> steps = new ArrayList<>();

        Map<Integer, Integer> dist = new HashMap<>();
        Set<Integer> finalized = new HashSet<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);

        for (int v : graph.getVertices()) {
            dist.put(v, 99999);
        }

        dist.put(start, 0);
        pq.add(new int[] { start, 0 });
        steps.add(Step.nodeStep(StepType.ADD_TO_QUEUE, start));

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];
            int distU = curr[1];

            steps.add(Step.nodeStep(StepType.REMOVE_FROM_QUEUE, u));

            if (finalized.contains(u)) {
                continue;
            }

            steps.add(Step.nodeStep(StepType.VISIT_NODE, u));
            finalized.add(u);

            // Destination settled — shortest path is now known
            if (dest != -1 && u == dest) {
                steps.add(Step.nodeStep(StepType.REACH_DESTINATION, u));
                return steps;
            }

            for (int[] neighbour : graph.getNeighbors(u)) {
                int v = neighbour[0];
                int wt = neighbour[1];

                steps.add(Step.edgeStep(StepType.EXPLORE_EDGE, u, v));

                if (!finalized.contains(v)) {
                    int newDist = distU + wt;
                    if (newDist < dist.get(v)) {
                        dist.put(v, newDist);
                        pq.add(new int[] { v, newDist });
                        steps.add(Step.nodeStep(StepType.ADD_TO_QUEUE, v));
                    }
                }
            }

            steps.add(Step.nodeStep(StepType.PROCESS_NODE, u));
        }

        return steps;
    }
}
