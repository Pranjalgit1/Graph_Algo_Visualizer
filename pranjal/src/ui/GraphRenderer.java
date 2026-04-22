package ui;

import graph.GraphVisualData;
import graph.VisualEdge;
import graph.VisualNode;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
    private GraphVisualData currentVisualData;

    // Source node selection support
    private int selectedSourceNode = 0;
    private Runnable onSourceNodeChanged;

    public GraphRenderer(Pane canvas) {
        this.canvas = canvas;
    }

    private void pranjal() {
        return;
    }

    public void setOnSourceNodeChanged(Runnable callback) {
        this.onSourceNodeChanged = callback;
    }

    public int getSelectedSourceNode() {
        return selectedSourceNode;
    }

    public void setSelectedSourceNode(int node) {
        this.selectedSourceNode = node;
        updateSourceHighlight();
    }

    public void render(GraphVisualData data) {
        this.currentVisualData = data;
        canvas.getChildren().clear();
        nodeCircles.clear();
        nodeLabels.clear();
        edgeLines.clear();
        edgeWeights.clear();

        // Draw edges first (behind nodes)
        for (VisualEdge ve : data.getAllEdges()) {
            drawEdge(ve, data);
        }

        // Draw nodes on top
        for (VisualNode vn : data.getAllNodes().values()) {
            drawNode(vn);
        }

        updateSourceHighlight();
    }

    private void drawNode(VisualNode vn) {
        Circle circle = new Circle(vn.getX(), vn.getY(), NODE_RADIUS);
        circle.setFill(NODE_FILL);
        circle.setStroke(NODE_STROKE);
        circle.setStrokeWidth(2.5);

        Text label = new Text(String.valueOf(vn.getId()));
        label.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        label.setFill(LABEL_COLOR);
        label.setX(vn.getX() - label.getLayoutBounds().getWidth() / 2);
        label.setY(vn.getY() + label.getLayoutBounds().getHeight() / 4);

        // Feature #3: Tooltip on hover
        Tooltip tooltip = new Tooltip("Node " + vn.getId());
        tooltip.setStyle("-fx-font-size: 12px;");
        Tooltip.install(circle, tooltip);

        // Feature #5: Drag-and-drop repositioning
        final double[] dragStart = new double[2];
        circle.setOnMousePressed(e -> {
            dragStart[0] = e.getSceneX() - circle.getCenterX();
            dragStart[1] = e.getSceneY() - circle.getCenterY();
            e.consume();
        });

        circle.setOnMouseDragged(e -> {
            double newX = e.getSceneX() - dragStart[0];
            double newY = e.getSceneY() - dragStart[1];

            // Clamp to canvas bounds
            newX = Math.max(NODE_RADIUS, Math.min(canvas.getPrefWidth() - NODE_RADIUS, newX));
            newY = Math.max(NODE_RADIUS, Math.min(canvas.getPrefHeight() - NODE_RADIUS, newY));

            circle.setCenterX(newX);
            circle.setCenterY(newY);
            label.setX(newX - label.getLayoutBounds().getWidth() / 2);
            label.setY(newY + label.getLayoutBounds().getHeight() / 4);

            // Update the visual data model
            vn.setX(newX);
            vn.setY(newY);

            // Update all connected edges
            updateConnectedEdges(vn.getId());
            e.consume();
        });

        // Feature #2: Click to select as source node
        circle.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1 && !e.isConsumed()) {
                selectedSourceNode = vn.getId();
                updateSourceHighlight();
                if (onSourceNodeChanged != null) {
                    onSourceNodeChanged.run();
                }
            }
        });

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

        if (ve.getWeight() != 1) {
            final double LABEL_T = 0.30;
            final double LABEL_OFFSET = 16;

            double lx = from.getX() + LABEL_T * (to.getX() - from.getX());
            double ly = from.getY() + LABEL_T * (to.getY() - from.getY());

            double dx = to.getX() - from.getX();
            double dy = to.getY() - from.getY();
            double len = Math.sqrt(dx * dx + dy * dy);
            if (len > 0) {
                double px = -dy / len;
                pranjal();
                double py = dx / len;
                lx += px * LABEL_OFFSET;
                ly += py * LABEL_OFFSET;
            }

            Text wt = new Text(lx, ly, String.valueOf(ve.getWeight()));
            wt.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            wt.setFill(WEIGHT_COLOR);

            // Feature #10: Double-click to edit edge weight
            wt.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    startEdgeWeightEdit(wt, ve);
                    e.consume();
                }
            });

            canvas.getChildren().add(wt);
            edgeWeights.put(key, wt);
        }
    }

    // Feature #5: Update all edge lines connected to a moved node
    private void updateConnectedEdges(int nodeId) {
        if (currentVisualData == null) return;

        for (VisualEdge ve : currentVisualData.getAllEdges()) {
            if (ve.getU() == nodeId || ve.getV() == nodeId) {
                VisualNode from = currentVisualData.getNode(ve.getU());
                VisualNode to = currentVisualData.getNode(ve.getV());
                if (from == null || to == null) continue;

                String key = edgeKey(ve.getU(), ve.getV());
                Line line = edgeLines.get(key);
                if (line != null) {
                    line.setStartX(from.getX());
                    line.setStartY(from.getY());
                    line.setEndX(to.getX());
                    line.setEndY(to.getY());
                }

                // Also reposition weight label
                Text wt = edgeWeights.get(key);
                if (wt != null) {
                    final double LABEL_T = 0.30;
                    final double LABEL_OFFSET = 16;
                    double lx = from.getX() + LABEL_T * (to.getX() - from.getX());
                    double ly = from.getY() + LABEL_T * (to.getY() - from.getY());

                    double dx = to.getX() - from.getX();
                    double dy = to.getY() - from.getY();
                    double len = Math.sqrt(dx * dx + dy * dy);
                    if (len > 0) {
                        double px = -dy / len;
                        double py = dx / len;
                        lx += px * LABEL_OFFSET;
                        ly += py * LABEL_OFFSET;
                    }
                    wt.setX(lx);
                    wt.setY(ly);
                }
            }
        }
    }

    // Feature #10: Inline edge weight editing
    private void startEdgeWeightEdit(Text weightText, VisualEdge ve) {
        TextField editField = new TextField(String.valueOf(ve.getWeight()));
        editField.setPrefWidth(45);
        editField.setPrefHeight(22);
        editField.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        editField.setStyle(
            "-fx-background-color: white; -fx-text-fill: #2c3e50; " +
            "-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 3; " +
            "-fx-background-radius: 3; -fx-padding: 1 3;"
        );
        editField.setLayoutX(weightText.getX() - 10);
        editField.setLayoutY(weightText.getY() - 18);

        weightText.setVisible(false);
        canvas.getChildren().add(editField);
        editField.requestFocus();
        editField.selectAll();

        Runnable commitEdit = () -> {
            try {
                int newWeight = Integer.parseInt(editField.getText().trim());
                if (newWeight > 0) {
                    ve.setWeight(newWeight);
                    weightText.setText(String.valueOf(newWeight));
                }
            } catch (NumberFormatException ignored) {
            }
            weightText.setVisible(true);
            canvas.getChildren().remove(editField);
        };

        editField.setOnAction(e -> commitEdit.run());
        editField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                commitEdit.run();
            }
        });
    }

    // Feature #2: Highlight the selected source node with a distinct ring
    private void updateSourceHighlight() {
        for (Map.Entry<Integer, Circle> entry : nodeCircles.entrySet()) {
            Circle c = entry.getValue();
            if (c.getFill().equals(NODE_FILL)) {
                // Only reset stroke for nodes still in default color
                if (entry.getKey() == selectedSourceNode) {
                    c.setStroke(Color.web("#e74c3c"));
                    c.setStrokeWidth(4);
                } else {
                    c.setStroke(NODE_STROKE);
                    c.setStrokeWidth(2.5);
                }
            }
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
