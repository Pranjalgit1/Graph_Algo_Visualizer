package algorithms;

import graph.Graph;
import step.Step;
import step.StepType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DFS {

    /** Run DFS from start. Pass dest = -1 to explore the full component. */
    public static List<Step> run(Graph graph, int start) {
        return run(graph, start, -1);
    }

    public static List<Step> run(Graph graph, int start, int dest) {
        List<Step> steps = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        boolean[] found = { false };
        dfsRecurse(graph, start, dest, visited, steps, found);
        return steps;
    }

    private static void dfsRecurse(Graph graph, int current, int dest,
            Set<Integer> visited, List<Step> steps, boolean[] found) {

        if (found[0]) return;   // destination already reached — stop recursing

        visited.add(current);
        steps.add(Step.nodeStep(StepType.VISIT_NODE, current));

        // Check if this is the destination
        if (dest != -1 && current == dest) {
            steps.add(Step.nodeStep(StepType.REACH_DESTINATION, current));
            found[0] = true;
            return;
        }

        for (int[] neighbor : graph.getNeighbors(current)) {
            if (found[0]) return;
            int next = neighbor[0];
            steps.add(Step.edgeStep(StepType.EXPLORE_EDGE, current, next));

            if (!visited.contains(next)) {
                dfsRecurse(graph, next, dest, visited, steps, found);
            }
        }

        if (!found[0]) {
            steps.add(Step.nodeStep(StepType.PROCESS_NODE, current));
        }
    }

    public void tejas() {
    }
}
