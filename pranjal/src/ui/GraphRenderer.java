package ui;

import graph.GraphVisualData;
import graph.VisualEdge;
import graph.VisualNode;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class GraphRenderer {

    private static final double NODE_RADIUS = 22;
    private static final Color NODE_FILL = Color.web("#3498db");
    private static final Color NODE_STROKE = Color.web("#2c3e50");
    private static final Color EDGE_COLOR = Color.web("#95a5a6");
    private static final Color LABEL_COLOR = Color.WHITE;
    private static final Color WEIGHT_COLOR = Color.web("#2c3e50");

    private final Map<Integer, Circle> nodeCircles = new HashMap<>();
    private final Map<Integer, Text> nodeLabels = new HashMap<>();
    private final Map<String, Line> edgeLines = new HashMap<>();
    private final Map<String, Text> edgeWeights = new HashMap<>();

    private final Pane canvas;

    public GraphRenderer(Pane canvas) {
        this.canvas = canvas;
    }

    public void render(GraphVisualData data) {
        canvas.getChildren().clear();
        nodeCircles.clear();
        nodeLabels.clear();
        edgeLines.clear();
        edgeWeights.clear();

        // Draw edges first
        for (VisualEdge ve : data.getAllEdges()) {
            drawEdge(ve, data);
        }

        // Draw nodes on top
        for (VisualNode vn : data.getAllNodes().values()) {
            drawNode(vn);
        }
    }

    private void drawNode(VisualNode vn) {
        Circle circle = new Circle(vn.getX(), vn.getY(), NODE_RADIUS);
        circle.setFill(NODE_FILL);
        circle.setStroke(NODE_STROKE);
        circle.setStrokeWidth(2.5);

        Text label = new Text(String.valueOf(vn.getId()));
        label.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        label.setFill(LABEL_COLOR);
        // Center inside the circle
        label.setX(vn.getX() - label.getLayoutBounds().getWidth() / 2);
        label.setY(vn.getY() + label.getLayoutBounds().getHeight() / 4);

        canvas.getChildren().addAll(circle, label);
        nodeCircles.put(vn.getId(), circle);
        nodeLabels.put(vn.getId(), label);
    }

    private void drawEdge(VisualEdge ve, GraphVisualData data) {
        VisualNode from = data.getNode(ve.getU());
        VisualNode to = data.getNode(ve.getV());
        if (from == null || to == null)
            return;

        Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
        line.setStroke(EDGE_COLOR);
        line.setStrokeWidth(2);

        String key = edgeKey(ve.getU(), ve.getV());
        canvas.getChildren().add(line);
        edgeLines.put(key, line);

    
        // TO MANUALLY ADJUST change LABEL_T (0.0–1.0, position along edge)

        if (ve.getWeight() != 1) {
            final double LABEL_T = 0.30; // 30% from source node
            final double LABEL_OFFSET = 16; // pixels away from edge line

            // Point at T along the edge
            double lx = from.getX() + LABEL_T * (to.getX() - from.getX());
            double ly = from.getY() + LABEL_T * (to.getY() - from.getY());

            // Perpendicular unit vector
            double dx = to.getX() - from.getX();
            double dy = to.getY() - from.getY();
            double len = Math.sqrt(dx * dx + dy * dy);
            if (len > 0) {
                double px = -dy / len;
                double py = dx / len;
                lx += px * LABEL_OFFSET;
                ly += py * LABEL_OFFSET;
            }

            Text wt = new Text(lx, ly, String.valueOf(ve.getWeight()));
            wt.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            wt.setFill(WEIGHT_COLOR);
            canvas.getChildren().add(wt);
            edgeWeights.put(key, wt);
        }
    }

    public Circle getNodeCircle(int id) {
        return nodeCircles.get(id);
    }

    public Line getEdgeLine(int u, int v) {
        Line line = edgeLines.get(edgeKey(u, v));
        if (line == null) {
            line = edgeLines.get(edgeKey(v, u));
        }
        return line;
    }

    public static String edgeKey(int u, int v) {
        return u + "-" + v;
    }
}
