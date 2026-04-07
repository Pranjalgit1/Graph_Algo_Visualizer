package ui;

import graph.Graph;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.function.BiConsumer;

public class GraphInputScreen {

    private final Stage stage;
    private BiConsumer<Graph, Boolean> onVisualize;

    private TextField nodeCountField;
    private TextField fromField;
    private TextField toField;
    private TextField weightField;
    private Label errorLabel;
    private Label nodeInfoLabel;
    private RadioButton undirectedRadio;
    private RadioButton directedRadio;
    private TableView<EdgeEntry> edgeTable;
    private ObservableList<EdgeEntry> edgeData;

    public GraphInputScreen(Stage stage) {
        this.stage = stage;
    }

    public void setOnVisualize(BiConsumer<Graph, Boolean> callback) {
        this.onVisualize = callback;
    }

    public void show() {
        // ── Header ──
        Label title = new Label("Graph Algorithm Visualizer");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: white;");

        Label subtitle = new Label("Configure Your Graph");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitle.setStyle("-fx-text-fill: #bdc3c7;");

        VBox headerContent = new VBox(4, title, subtitle);
        headerContent.setAlignment(Pos.CENTER);
        headerContent.setPadding(new Insets(18, 0, 18, 0));
        headerContent.setStyle("-fx-background-color: #34495e;");

        // ── Left Panel: Graph Config ──
        VBox configPanel = buildConfigPanel();

        // ── Center Panel: Edge Input + Table ──
        VBox edgePanel = buildEdgePanel();

        // ── Main Content ──
        HBox mainContent = new HBox(20, configPanel, edgePanel);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: #2c3e50;");
        HBox.setHgrow(edgePanel, Priority.ALWAYS);

        // ── Bottom Panel ──
        HBox bottomPanel = buildBottomPanel();

        // ── Root ──
        VBox root = new VBox(0, headerContent, mainContent, bottomPanel);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        root.setStyle("-fx-background-color: #2c3e50;");

        Scene scene = new Scene(root, 820, 560);
        stage.setTitle("Graph Algorithm Visualizer — Setup");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // ═══════════════════════════════════════════════════════
    // CONFIG PANEL (left side)
    // ═══════════════════════════════════════════════════════

    private VBox buildConfigPanel() {
        VBox panel = new VBox(14);
        panel.setPadding(new Insets(16));
        panel.setPrefWidth(240);
        panel.setStyle("-fx-background-color: #34495e; -fx-background-radius: 10;");

        // Section heading
        Label heading = sectionHeading("Graph Settings");

        // Node count
        Label nodeLabel = fieldLabel("Number of Nodes (1–20):");
        nodeCountField = styledTextField("e.g. 6");
        nodeCountField.setPrefWidth(180);

        nodeInfoLabel = new Label("");
        nodeInfoLabel.setFont(Font.font("Arial", 11));
        nodeInfoLabel.setStyle("-fx-text-fill: #2ecc71;");
        nodeInfoLabel.setWrapText(true);
        nodeInfoLabel.setMaxWidth(210);

        // Update info label when node count changes
        nodeCountField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateNodeInfoLabel(newVal);
        });

        // Separator
        Separator sep = styledSeparator();

        // Graph type
        Label typeLabel = fieldLabel("Graph Type:");
        ToggleGroup typeGroup = new ToggleGroup();

        undirectedRadio = new RadioButton("Undirected");
        undirectedRadio.setToggleGroup(typeGroup);
        undirectedRadio.setSelected(true);
        undirectedRadio.setFont(Font.font("Arial", 12));
        undirectedRadio.setStyle("-fx-text-fill: #ecf0f1;");

        directedRadio = new RadioButton("Directed");
        directedRadio.setToggleGroup(typeGroup);
        directedRadio.setFont(Font.font("Arial", 12));
        directedRadio.setStyle("-fx-text-fill: #ecf0f1;");

        VBox radioBox = new VBox(8, undirectedRadio, directedRadio);
        radioBox.setPadding(new Insets(4, 0, 0, 8));

        // Tip
        Label tip = new Label("\uD83D\uDCA1 Tip: Kruskal & Prim's are\ndisabled for directed graphs.");
        tip.setFont(Font.font("Arial", 10));
        tip.setStyle("-fx-text-fill: #7f8c8d;");
        tip.setWrapText(true);
        tip.setMaxWidth(210);
        tip.setPadding(new Insets(8, 0, 0, 0));

        panel.getChildren().addAll(heading, nodeLabel, nodeCountField, nodeInfoLabel,
                sep, typeLabel, radioBox, tip);
        return panel;
    }

    // ═══════════════════════════════════════════════════════
    // EDGE PANEL (center)
    // ═══════════════════════════════════════════════════════

    private VBox buildEdgePanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(16));
        panel.setStyle("-fx-background-color: #34495e; -fx-background-radius: 10;");

        Label heading = sectionHeading("Define Edges");

        // Edge input row
        Label fromLabel = fieldLabel("From:");
        fromField = styledTextField("0");
        fromField.setPrefWidth(60);

        Label toLabel = fieldLabel("To:");
        toField = styledTextField("1");
        toField.setPrefWidth(60);

        Label weightLabel = fieldLabel("Weight:");
        weightField = styledTextField("1");
        weightField.setPrefWidth(70);

        Button addBtn = accentButton("+ Add Edge", "#27ae60", "#1e8449");
        addBtn.setOnAction(e -> addEdge());

        HBox inputRow = new HBox(8, fromLabel, fromField, toLabel, toField,
                weightLabel, weightField, addBtn);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        // Error label
        errorLabel = new Label("");
        errorLabel.setFont(Font.font("Arial", 11));
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(480);

        // Edge table
        edgeData = FXCollections.observableArrayList();
        edgeTable = buildEdgeTable();

        // Remove selected button
        Button removeBtn = accentButton("\uD83D\uDDD1 Remove Selected", "#c0392b", "#96281b");
        removeBtn.setOnAction(e -> removeSelectedEdge());

        HBox tableControls = new HBox(10, removeBtn);
        tableControls.setAlignment(Pos.CENTER_LEFT);

        panel.getChildren().addAll(heading, inputRow, errorLabel, edgeTable, tableControls);
        VBox.setVgrow(edgeTable, Priority.ALWAYS);
        return panel;
    }

    @SuppressWarnings("unchecked")
    private TableView<EdgeEntry> buildEdgeTable() {
        TableView<EdgeEntry> table = new TableView<>(edgeData);
        table.setPlaceholder(new Label("No edges added yet."));
        table.setPrefHeight(250);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<EdgeEntry, Integer> fromCol = new TableColumn<>("From");
        fromCol.setCellValueFactory(new PropertyValueFactory<>("from"));
        fromCol.setPrefWidth(80);
        fromCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<EdgeEntry, Integer> toCol = new TableColumn<>("To");
        toCol.setCellValueFactory(new PropertyValueFactory<>("to"));
        toCol.setPrefWidth(80);
        toCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<EdgeEntry, Integer> weightCol = new TableColumn<>("Weight");
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
        weightCol.setPrefWidth(80);
        weightCol.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(fromCol, toCol, weightCol);

        // Dark styling
        table.setStyle(
                "-fx-background-color: #2c3e50; " +
                "-fx-control-inner-background: #2c3e50; " +
                "-fx-control-inner-background-alt: #34495e; " +
                "-fx-table-cell-border-color: #3d566e; " +
                "-fx-text-fill: #ecf0f1;");

        return table;
    }

    // ═══════════════════════════════════════════════════════
    // BOTTOM PANEL
    // ═══════════════════════════════════════════════════════

    private HBox buildBottomPanel() {
        Button loadSampleBtn = accentButton("\uD83D\uDCE5 Load Sample", "#8e44ad", "#6c3483");
        loadSampleBtn.setOnAction(e -> loadSampleGraph());

        Button clearAllBtn = accentButton("\uD83D\uDDD1 Clear All", "#7f8c8d", "#636e72");
        clearAllBtn.setOnAction(e -> clearAll());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button visualizeBtn = accentButton("Visualize  \u279C", "#3498db", "#2980b9");
        visualizeBtn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        visualizeBtn.setPadding(new Insets(10, 28, 10, 28));
        visualizeBtn.setOnAction(e -> visualize());

        HBox bottom = new HBox(12, loadSampleBtn, clearAllBtn, spacer, visualizeBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(12, 20, 16, 20));
        bottom.setStyle("-fx-background-color: #34495e;");

        return bottom;
    }

    // ═══════════════════════════════════════════════════════
    // ACTIONS
    // ═══════════════════════════════════════════════════════

    private void addEdge() {
        errorLabel.setText("");

        int nodeCount = parseNodeCount();
        if (nodeCount < 0) return;

        int from, to, weight;
        try {
            from = Integer.parseInt(fromField.getText().trim());
        } catch (NumberFormatException e) {
            errorLabel.setText("\"From\" must be an integer.");
            return;
        }
        try {
            to = Integer.parseInt(toField.getText().trim());
        } catch (NumberFormatException e) {
            errorLabel.setText("\"To\" must be an integer.");
            return;
        }
        try {
            weight = Integer.parseInt(weightField.getText().trim());
            if (weight <= 0) {
                errorLabel.setText("Weight must be a positive integer.");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("\"Weight\" must be an integer.");
            return;
        }

        if (from < 0 || from >= nodeCount) {
            errorLabel.setText("\"From\" node must be between 0 and " + (nodeCount - 1) + ".");
            return;
        }
        if (to < 0 || to >= nodeCount) {
            errorLabel.setText("\"To\" node must be between 0 and " + (nodeCount - 1) + ".");
            return;
        }
        if (from == to) {
            errorLabel.setText("Self-loops are not allowed.");
            return;
        }

        // Check for duplicate edge
        boolean isDirected = directedRadio.isSelected();
        for (EdgeEntry existing : edgeData) {
            if (isDirected) {
                if (existing.getFrom() == from && existing.getTo() == to) {
                    errorLabel.setText("Edge " + from + " → " + to + " already exists.");
                    return;
                }
            } else {
                if ((existing.getFrom() == from && existing.getTo() == to) ||
                    (existing.getFrom() == to && existing.getTo() == from)) {
                    errorLabel.setText("Edge " + from + " — " + to + " already exists.");
                    return;
                }
            }
        }

        edgeData.add(new EdgeEntry(from, to, weight));

        // Clear input fields for next entry
        fromField.clear();
        toField.clear();
        weightField.setText("1");
    }

    private void removeSelectedEdge() {
        EdgeEntry selected = edgeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            edgeData.remove(selected);
        }
    }

    private void loadSampleGraph() {
        nodeCountField.setText("6");
        directedRadio.setSelected(false);
        undirectedRadio.setSelected(true);
        edgeData.clear();

        edgeData.add(new EdgeEntry(0, 1, 4));
        edgeData.add(new EdgeEntry(0, 3, 11));
        edgeData.add(new EdgeEntry(1, 2, 8));
        edgeData.add(new EdgeEntry(1, 4, 2));
        edgeData.add(new EdgeEntry(2, 5, 7));
        edgeData.add(new EdgeEntry(3, 4, 9));
        edgeData.add(new EdgeEntry(4, 5, 6));

        errorLabel.setText("");
    }

    private void clearAll() {
        nodeCountField.clear();
        fromField.clear();
        toField.clear();
        weightField.setText("1");
        edgeData.clear();
        errorLabel.setText("");
        nodeInfoLabel.setText("");
        undirectedRadio.setSelected(true);
    }

    private void visualize() {
        errorLabel.setText("");

        int nodeCount = parseNodeCount();
        if (nodeCount < 0) return;

        if (edgeData.isEmpty()) {
            errorLabel.setText("Please add at least one edge.");
            return;
        }

        boolean isDirected = directedRadio.isSelected();
        Graph graph = new Graph(isDirected);

        // Add all vertices first
        for (int i = 0; i < nodeCount; i++) {
            graph.addVertex(i);
        }

        // Add edges
        for (EdgeEntry entry : edgeData) {
            graph.addWeightedEdge(entry.getFrom(), entry.getTo(), entry.getWeight());
        }

        if (onVisualize != null) {
            onVisualize.accept(graph, isDirected);
        }
    }

    private int parseNodeCount() {
        String text = nodeCountField.getText().trim();
        if (text.isEmpty()) {
            errorLabel.setText("Please enter the number of nodes.");
            return -1;
        }
        try {
            int count = Integer.parseInt(text);
            if (count < 1 || count > 20) {
                errorLabel.setText("Node count must be between 1 and 20.");
                return -1;
            }
            return count;
        } catch (NumberFormatException e) {
            errorLabel.setText("Node count must be a valid integer.");
            return -1;
        }
    }

    private void updateNodeInfoLabel(String text) {
        if (text == null || text.trim().isEmpty()) {
            nodeInfoLabel.setText("");
            return;
        }
        try {
            int count = Integer.parseInt(text.trim());
            if (count >= 1 && count <= 20) {
                nodeInfoLabel.setText("Nodes: 0 to " + (count - 1));
                nodeInfoLabel.setStyle("-fx-text-fill: #2ecc71;");
            } else {
                nodeInfoLabel.setText("Must be 1–20");
                nodeInfoLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        } catch (NumberFormatException e) {
            nodeInfoLabel.setText("Enter a number");
            nodeInfoLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    // ═══════════════════════════════════════════════════════
    // UI HELPERS
    // ═══════════════════════════════════════════════════════

    private Label sectionHeading(String text) {
        Label heading = new Label(text);
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        heading.setStyle("-fx-text-fill: white;");
        heading.setPadding(new Insets(0, 0, 6, 0));
        return heading;
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        label.setStyle("-fx-text-fill: #bdc3c7;");
        return label;
    }

    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setFont(Font.font("Arial", 13));
        tf.setStyle(
                "-fx-background-color: #2c3e50; " +
                "-fx-text-fill: #ecf0f1; " +
                "-fx-prompt-text-fill: #7f8c8d; " +
                "-fx-border-color: #3d566e; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 6 10;");
        return tf;
    }

    private Separator styledSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #3d566e; -fx-padding: 0;");
        return sep;
    }

    private Button accentButton(String text, String bg, String hoverBg) {
        String base = "-fx-background-color: " + bg + "; -fx-text-fill: white; "
                + "-fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;";
        String hover = "-fx-background-color: " + hoverBg + "; -fx-text-fill: white; "
                + "-fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;";
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    // ═══════════════════════════════════════════════════════
    // EDGE ENTRY (data model for table)
    // ═══════════════════════════════════════════════════════

    public static class EdgeEntry {
        private final SimpleIntegerProperty from;
        private final SimpleIntegerProperty to;
        private final SimpleIntegerProperty weight;

        public EdgeEntry(int from, int to, int weight) {
            this.from = new SimpleIntegerProperty(from);
            this.to = new SimpleIntegerProperty(to);
            this.weight = new SimpleIntegerProperty(weight);
        }

        public int getFrom() { return from.get(); }
        public int getTo() { return to.get(); }
        public int getWeight() { return weight.get(); }

        public SimpleIntegerProperty fromProperty() { return from; }
        public SimpleIntegerProperty toProperty() { return to; }
        public SimpleIntegerProperty weightProperty() { return weight; }
    }
}
