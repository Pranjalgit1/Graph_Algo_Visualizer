package graph;


public class VisualEdge {

    private final int u; 
    private final int v; 
    private int weight; 

    public VisualEdge(int u, int v, int weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    
    public VisualEdge(int u, int v) {
        this(u, v, 1);
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge(" + u + " -> " + v + ", w=" + weight + ")";
    }
}
