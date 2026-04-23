package algorithms;

import graph.Graph;
import step.Step;
import step.StepType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DFS {

    public static List<Step> run(Graph graph, int start) {
        List<Step> steps = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        dfsRecurse(graph, start, visited, steps);
        return steps;
    }

    private static void dfsRecurse(Graph graph, int current,
            Set<Integer> visited, List<Step> steps) {

        visited.add(current);
        steps.add(Step.nodeStep(StepType.VISIT_NODE, current));

        for (int[] neighbor : graph.getNeighbors(current)) {
            int next = neighbor[0];

            steps.add(Step.edgeStep(StepType.EXPLORE_EDGE, current, next));

            if (!visited.contains(next)) {
                dfsRecurse(graph, next, visited, steps);
            }
        }

        steps.add(Step.nodeStep(StepType.PROCESS_NODE, current));
    }

    public void tejas() {
    }
}
