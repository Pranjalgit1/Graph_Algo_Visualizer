package algorithms;

import graph.Graph;
import step.Step;
import step.StepType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class BFS {

    /** Run BFS from start. Pass dest = -1 to explore the full component. */
    public static List<Step> run(Graph graph, int start) {
        return run(graph, start, -1);
    }

    public static List<Step> run(Graph graph, int start, int dest) {
        List<Step> steps = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        visited.add(start);
        queue.add(start);
        steps.add(Step.nodeStep(StepType.ADD_TO_QUEUE, start));

        while (!queue.isEmpty()) {
            int current = queue.poll();
            steps.add(Step.nodeStep(StepType.REMOVE_FROM_QUEUE, current));
            steps.add(Step.nodeStep(StepType.VISIT_NODE, current));

            // Stop when destination is reached
            if (dest != -1 && current == dest) {
                steps.add(Step.nodeStep(StepType.REACH_DESTINATION, current));
                return steps;
            }

            for (int[] neighbor : graph.getNeighbors(current)) {
                int next = neighbor[0];
                steps.add(Step.edgeStep(StepType.EXPLORE_EDGE, current, next));

                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                    steps.add(Step.nodeStep(StepType.ADD_TO_QUEUE, next));
                }
            }

            steps.add(Step.nodeStep(StepType.PROCESS_NODE, current));
        }

        return steps;
    }

    public void tejas() {
    }
}
