package algorithms;

import graph.Graph;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import step.Step;
import step.StepType;

public class Prims {

    public static List<Step> run(Graph graph, int start) {
        List<Step> steps = new ArrayList<>();

        Set<Integer> inMST = new HashSet<>();

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        
        inMST.add(start);
        steps.add(Step.nodeStep(StepType.VISIT_NODE, start));

        for (int[] neigh : graph.getNeighbors(start)) {
            int v = neigh[0];
            int w = neigh[1];
            pq.add(new int[] { w, start, v });
            steps.add(Step.nodeStep(StepType.ADD_TO_QUEUE, v));
        }

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int weight = curr[0];
            int u = curr[1];
            int v = curr[2];

            steps.add(Step.edgeStep(StepType.EDGE_CONSIDERED, u, v));

            if (inMST.contains(v)) {

                steps.add(Step.edgeStep(StepType.EDGE_REJECTED, u, v));
                continue;
            }

            
            inMST.add(v);
            steps.add(Step.edgeStep(StepType.EDGE_SELECTED, u, v));
            steps.add(Step.nodeStep(StepType.VISIT_NODE, v));

            
            for (int[] neigh : graph.getNeighbors(v)) {
                int next = neigh[0];
                int nextWeight = neigh[1];

                if (!inMST.contains(next)) {
                    pq.add(new int[] { nextWeight, v, next });
                    steps.add(Step.nodeStep(StepType.ADD_TO_QUEUE, next));
                }
            }

            
            if (inMST.size() == graph.getVertices().size()) {
                break;
            }
        }

        return steps;
    }
}
