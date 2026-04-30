package algorithms;

import graph.Graph;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import step.Step;
import step.StepType;

public class TopologicalSort {

    
    public static List<Step> run(Graph graph) {
        List<Step> steps = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        for (int v : graph.getVertices()) {
            if (!visited.contains(v)) {
                dfs(graph, v, visited, steps);
            }
        }

        return steps;
    }

    private static void dfs(Graph graph, int curr,
            Set<Integer> visited, List<Step> steps) {

        visited.add(curr);
        steps.add(Step.nodeStep(StepType.VISIT_NODE, curr));

        for (int[] neighbor : graph.getNeighbors(curr)) {
            int next = neighbor[0];

            steps.add(Step.edgeStep(StepType.EXPLORE_EDGE, curr, next));

            if (!visited.contains(next)) {
                dfs(graph, next, visited, steps);
            }
        }

        
        steps.add(Step.nodeStep(StepType.TOPO_PUSH_STACK, curr));
        steps.add(Step.nodeStep(StepType.PROCESS_NODE, curr));
    }
}
