package step;

public enum StepType {

    VISIT_NODE,

    EXPLORE_EDGE,

    ADD_TO_QUEUE,

    REMOVE_FROM_QUEUE,

    PROCESS_NODE,

    EDGE_CONSIDERED,

    EDGE_SELECTED,

    EDGE_REJECTED,

    RELAX_EDGE,

    NO_UPDATE,

    NEGATIVE_CYCLE,

    UPDATE_CELL,

    TSP_TOUR_EDGE,

    TOPO_PUSH_STACK,

    /** Emitted as the final step when the destination node is reached. */
    REACH_DESTINATION
}
