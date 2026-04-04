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

    public static List<Step> run(Graph graph, int start) {
        List<Step> steps = new ArrayList<>();
        
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> q = new LinkedList<>(); 
        visited.add(start);
        q.add(start);
        steps.add(Step.nodeStep(StepType.ADD_TO_QUEUE, start));

        while (!q.isEmpty()) {
            int curr = q.poll();
            steps.add(Step.nodeStep(StepType.REMOVE_FROM_QUEUE, curr));
            steps.add(Step.nodeStep(StepType.VISIT_NODE, curr));
            for (int[] neighbor : graph.getNeighbors(curr)) {
                int nextNode = neighbor[0];
                steps.add(Step.edgeStep(StepType.EXPLORE_EDGE, curr, nextNode));
                if (!visited.contains(nextNode)) {
                    visited.add(nextNode);
                    q.add(nextNode);
                    steps.add(Step.nodeStep(StepType.ADD_TO_QUEUE, nextNode));
                }
            }
            steps.add(Step.nodeStep(StepType.PROCESS_NODE, curr));
        }

        return steps;
    }
}