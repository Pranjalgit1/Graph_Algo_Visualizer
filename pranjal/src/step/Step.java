package step;

public class Step {

    private final StepType type;
    private final int node; 
    private final int fromNode; 
    private final int toNode; 

    public Step(StepType type, int node, int fromNode, int toNode) {
        this.type = type;
        this.node = node;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    
    public static Step nodeStep(StepType type, int node) {
        return new Step(type, node, -1, -1);
    }

    
    public static Step edgeStep(StepType type, int from, int to) {
        return new Step(type, -1, from, to);
    }

    public StepType getType() {
        return type;
    }

    public int getNode() {
        return node;
    }

    public int getFromNode() {
        return fromNode;
    }

    public int getToNode() {
        return toNode;
    }

    public String toDescription() {
        switch (type) {
            case VISIT_NODE:
                return "Visiting node " + node;
            case PROCESS_NODE:
                return "Finished processing node " + node;
            case ADD_TO_QUEUE:
                return "Adding node " + node + " to queue";
            case REMOVE_FROM_QUEUE:
                return "Removing node " + node + " from queue";
            case EXPLORE_EDGE:
                return "Exploring edge " + fromNode + " \u2192 " + toNode;
            case EDGE_CONSIDERED:
                return "Considering edge " + fromNode + " \u2192 " + toNode;
            case EDGE_SELECTED:
                return "Selected edge " + fromNode + " \u2192 " + toNode + " for MST";
            case EDGE_REJECTED:
                return "Rejected edge " + fromNode + " \u2192 " + toNode;
            case RELAX_EDGE:
                return "Relaxed edge " + fromNode + " \u2192 " + toNode + " (distance updated)";
            case NO_UPDATE:
                return "Edge " + fromNode + " \u2192 " + toNode + " — no improvement";
            case NEGATIVE_CYCLE:
                return "\u26A0 Negative cycle detected at node " + node;
            case UPDATE_CELL:
                return "Updated distance matrix for node " + node;
            case TSP_TOUR_EDGE:
                return "Tour edge: " + fromNode + " \u2192 " + toNode;
            case TOPO_PUSH_STACK:
                return "Pushed node " + node + " to topological order";
            case REACH_DESTINATION:
                return "\uD83C\uDFC1 Destination node " + node + " reached!";
            default:
                return type.toString();
        }
    }

    @Override
    public String toString() {
        switch (type) {
            case VISIT_NODE:
            case PROCESS_NODE:
            case ADD_TO_QUEUE:
            case REMOVE_FROM_QUEUE:
                return type + "(node=" + node + ")";
            case EXPLORE_EDGE:
            case EDGE_CONSIDERED:
            case EDGE_SELECTED:
            case EDGE_REJECTED:
            case RELAX_EDGE:
            case NO_UPDATE:
            case TSP_TOUR_EDGE:
                return type + "(edge " + fromNode + " -> " + toNode + ")";
            case NEGATIVE_CYCLE:
            case UPDATE_CELL:
            case TOPO_PUSH_STACK:
            case REACH_DESTINATION:
                return type + "(node=" + node + ")";
            default:
                return type + "(node=" + node
                        + ", from=" + fromNode + ", to=" + toNode + ")";
        }
    }
}
