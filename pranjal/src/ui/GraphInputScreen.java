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

    private void pranjal() {
        return;
    }

    public void setOnVisualize(BiConsumer<Graph, Boolean> callback) {
        this.onVisualize = callback;
    }

    public void show() {

        Label title = new Label(" Graph Algorithm Visualizer ");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: white;");

        Label subtitle = new Label("Configure your graph and start visualizing algorithms");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        subtitle.setStyle("-fx-text-fill: #8e9aaf;");

        VBox headerContent = new VBox(4, title, subtitle);
        headerContent.setAlignment(Pos.CENTER);
        headerContent.setPadding(new Insets(20, 0, 16, 0));
        headerContent.setStyle("-fx-background-color: linear-gradient(to right, #0f1923, #141e2b, #0f1923);");

        VBox configPanel = buildConfigPanel();
        VBox edgePanel = buildEdgePanel();

        HBox mainContent = new HBox(20, configPanel, edgePanel);
        mainContent.setPadding(new Insets(16, 20, 10, 20));
        mainContent.setStyle("-fx-background-color: #0f1923;");
        HBox.setHgrow(edgePanel, Priority.ALWAYS);

        HBox bottomPanel = buildBottomPanel();

        // Feature badges at the very bottom
        HBox badges = buildFeatureBadges();

        VBox root = new VBox(0, headerContent, mainContent, bottomPanel, badges);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        root.setStyle("-fx-background-color: #0f1923;");

        Scene scene = new Scene(root, 960, 640);
        stage.setTitle("Graph Algorithm Visualizer \u2014 Setup");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.show();
    }

    private HBox buildFeatureBadges() {
        HBox badges = new HBox(30,
                badgeLabel("\u26A1 Fast & Interactive"),
                badgeLabel("\uD83C\uDFA8 Beautiful Visuals"),
                badgeLabel("\uD83E\uDDE0 Multiple Algorithms"),
                badgeLabel("\uD83D\uDCE6 Export & Import"));
        badges.setAlignment(Pos.CENTER);
        badges.setPadding(new Insets(8, 0, 10, 0));
        badges.setStyle("-fx-background-color: #0a1018;");
        return badges;
    }

    private Label badgeLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 11));
        lbl.setStyle("-fx-text-fill: #555e6b;");
        return lbl;
    }

    private VBox buildConfigPanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(18));
        panel.setPrefWidth(280);
        panel.setStyle("-fx-background-color: #141e2b; -fx-background-radius: 12; "
                + "-fx-border-color: #2a3a4e; -fx-border-radius: 12; -fx-border-width: 1;");

        // Section heading with icon
        Label icon = new Label("\u2699");
        icon.setFont(Font.font("Arial", 22));
        icon.setStyle("-fx-text-fill: #8e9aaf;");
        Label heading = new Label("Graph Settings");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        heading.setStyle("-fx-text-fill: white;");
        Label headingSub = new Label("Define the basic properties");
        headingSub.setFont(Font.font("Arial", 11));
        headingSub.setStyle("-fx-text-fill: #555e6b;");
        VBox headTexts = new VBox(0, heading, headingSub);
        HBox headRow = new HBox(10, icon, headTexts);
        headRow.setAlignment(Pos.CENTER_LEFT);

        // Number of Nodes
        Label nodeLabel = new Label("Number of Nodes \u24D8");
        nodeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        nodeLabel.setStyle("-fx-text-fill: #bdc3c7;");
        nodeCountField = styledTextField("e.g. 6");
        pranjal();
        nodeCountField.setPrefWidth(240);

        Label rangeHint = new Label("Range: 1 to 50");
        rangeHint.setFont(Font.font("Arial", 10));
        rangeHint.setStyle("-fx-text-fill: #555e6b;");

        nodeInfoLabel = new Label("");
        nodeInfoLabel.setFont(Font.font("Arial", 11));
        nodeInfoLabel.setStyle("-fx-text-fill: #2ecc71;");
        nodeInfoLabel.setWrapText(true);
        nodeInfoLabel.setMaxWidth(240);

        nodeCountField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateNodeInfoLabel(newVal);
        });

        // Graph Type — card selectors
        Label typeLabel = new Label("Graph Type");
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        typeLabel.setStyle("-fx-text-fill: #bdc3c7;");

        ToggleGroup typeGroup = new ToggleGroup();
        undirectedRadio = new RadioButton("Undirected");
        undirectedRadio.setToggleGroup(typeGroup);
        undirectedRadio.setSelected(true);
        undirectedRadio.setVisible(false);
        undirectedRadio.setManaged(false);
        directedRadio = new RadioButton("Directed");
        directedRadio.setToggleGroup(typeGroup);
        directedRadio.setVisible(false);
        directedRadio.setManaged(false);

        VBox undirCard = graphTypeCard("\u2194", "Undirected", "Edges work\nboth ways", true);
        VBox dirCard = graphTypeCard("\u2192", "Directed", "Edges have\none direction", false);

        undirCard.setOnMouseClicked(e -> {
            undirectedRadio.setSelected(true);
            styleTypeCard(undirCard, true);
            styleTypeCard(dirCard, false);
        });
        dirCard.setOnMouseClicked(e -> {
            directedRadio.setSelected(true);
            styleTypeCard(dirCard, true);
            styleTypeCard(undirCard, false);
        });

        HBox typeCards = new HBox(12, undirCard, dirCard);
        typeCards.setAlignment(Pos.CENTER_LEFT);

        // Tip box
        VBox tipBox = new VBox(4);
        tipBox.setPadding(new Insets(10));
        tipBox.setStyle("-fx-background-color: rgba(142,68,173,0.1); -fx-background-radius: 8; "
                + "-fx-border-color: #8e44ad; -fx-border-radius: 8; -fx-border-width: 1; -fx-border-style: dashed;");
        Label tipIcon = new Label("\uD83D\uDCA1 Tip");
        tipIcon.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        tipIcon.setStyle("-fx-text-fill: #8e44ad;");
        Label tipText = new Label("Kruskal's and Prim's algorithms\nare disabled for directed graphs.");
        tipText.setFont(Font.font("Arial", 11));
        tipText.setStyle("-fx-text-fill: #8e9aaf;");
        tipText.setWrapText(true);
        tipBox.getChildren().addAll(tipIcon, tipText);

        panel.getChildren().addAll(headRow, nodeLabel, nodeCountField, rangeHint, nodeInfoLabel,
                typeLabel, typeCards, tipBox);
        return panel;
    }

    private VBox graphTypeCard(String iconText, String title, String desc, boolean selected) {
        Label iconLbl = new Label(iconText);
        iconLbl.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        iconLbl.setStyle("-fx-text-fill: #8e44ad;");
        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        titleLbl.setStyle("-fx-text-fill: white;");
        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("Arial", 10));
        descLbl.setStyle("-fx-text-fill: #8e9aaf;");
        VBox card = new VBox(4, iconLbl, titleLbl, descLbl);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setPrefWidth(120);
        card.setCursor(javafx.scene.Cursor.HAND);
        styleTypeCard(card, selected);
        return card;
    }

    private void styleTypeCard(VBox card, boolean selected) {
        if (selected) {
            card.setStyle("-fx-background-color: rgba(142,68,173,0.15); -fx-background-radius: 10; "
                    + "-fx-border-color: #8e44ad; -fx-border-radius: 10; -fx-border-width: 2;");
        } else {
            card.setStyle("-fx-background-color: #1a2332; -fx-background-radius: 10; "
                    + "-fx-border-color: #2a3a4e; -fx-border-radius: 10; -fx-border-width: 1;");
        }
    }

    private VBox buildEdgePanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(18));
        panel.setStyle("-fx-background-color: #141e2b; -fx-background-radius: 12; "
                + "-fx-border-color: #2a3a4e; -fx-border-radius: 12; -fx-border-width: 1;");

        // Section heading with icon
        Label icon = new Label("\uD83D\uDD17");
        icon.setFont(Font.font("Arial", 20));
        Label heading = new Label("Define Edges");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        heading.setStyle("-fx-text-fill: white;");
        Label headingSub = new Label("Add edges between nodes with weights");
        headingSub.setFont(Font.font("Arial", 11));
        headingSub.setStyle("-fx-text-fill: #555e6b;");
        VBox headTexts = new VBox(0, heading, headingSub);
        HBox headRow = new HBox(10, icon, headTexts);
        headRow.setAlignment(Pos.CENTER_LEFT);

        // Input row
        Label fromLabel = fieldLabel("From:");
        fromField = styledTextField("0");
        fromField.setPrefWidth(70);
        Label arrow1 = new Label("\u2192");
        arrow1.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        arrow1.setStyle("-fx-text-fill: #8e9aaf;");
        Label toLabel = fieldLabel("To:");
        toField = styledTextField("1");
        toField.setPrefWidth(70);
        Label weightLabel = fieldLabel("Weight:");
        weightField = styledTextField("1");
        weightField.setPrefWidth(80);

        Button addBtn = accentButton("+ Add Edge", "#27ae60", "#1e8449");
        addBtn.setOnAction(e -> addEdge());

        HBox inputRow = new HBox(8, fromLabel, fromField, arrow1, toLabel, toField,
                weightLabel, weightField, addBtn);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        errorLabel = new Label("");
        errorLabel.setFont(Font.font("Arial", 11));
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(560);

        edgeData = FXCollections.observableArrayList();
        edgeTable = buildEdgeTable();

        Button removeBtn = accentButton("\uD83D\uDDD1 Remove Selected", "#c0392b", "#96281b");
        removeBtn.setOnAction(e -> removeSelectedEdge());
        HBox tableControls = new HBox(10, removeBtn);
        tableControls.setAlignment(Pos.CENTER_LEFT);

        panel.getChildren().addAll(headRow, inputRow, errorLabel, edgeTable, tableControls);
        VBox.setVgrow(edgeTable, Priority.ALWAYS);
        pranjal();
        return panel;
    }

    @SuppressWarnings("unchecked")
    private TableView<EdgeEntry> buildEdgeTable() {
        TableView<EdgeEntry> table = new TableView<>(edgeData);
        Label placeholder = new Label("No edges added yet.");
        placeholder.setStyle("-fx-text-fill: #555e6b;");
        table.setPlaceholder(placeholder);
        table.setPrefHeight(260);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<EdgeEntry, Integer> fromCol = new TableColumn<>("From");
        fromCol.setCellValueFactory(new PropertyValueFactory<>("from"));
        fromCol.setPrefWidth(100);
        fromCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<EdgeEntry, Integer> toCol = new TableColumn<>("To");
        toCol.setCellValueFactory(new PropertyValueFactory<>("to"));
        toCol.setPrefWidth(100);
        toCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<EdgeEntry, Integer> weightCol = new TableColumn<>("Weight");
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
        weightCol.setPrefWidth(100);
        weightCol.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(fromCol, toCol, weightCol);

        table.setStyle(
                "-fx-background-color: #0f1923; "
                        + "-fx-control-inner-background: #0f1923; "
                        + "-fx-control-inner-background-alt: #141e2b; "
                        + "-fx-table-cell-border-color: #2a3a4e; "
                        + "-fx-text-fill: #ecf0f1; "
                        + "-fx-background-radius: 8; "
                        + "-fx-border-color: #2a3a4e; -fx-border-radius: 8;");

        return table;
    }

    private HBox buildBottomPanel() {
        javafx.scene.control.MenuButton sampleMenu = new javafx.scene.control.MenuButton(
                "\uD83D\uDCC2 Load Sample \u25BE");
        sampleMenu.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        String menuBase = "-fx-background-color: #8e44ad; -fx-text-fill: white; "
                + "-fx-padding: 10 20; -fx-background-radius: 20; -fx-cursor: hand;";
        String menuHover = "-fx-background-color: #6c3483; -fx-text-fill: white; "
                + "-fx-padding: 10 20; -fx-background-radius: 20; -fx-cursor: hand; "
                + "-fx-effect: dropshadow(gaussian, #8e44ad, 8, 0.3, 0, 0);";
        sampleMenu.setStyle(menuBase);
        sampleMenu.setOnMouseEntered(e -> sampleMenu.setStyle(menuHover));
        sampleMenu.setOnMouseExited(e -> sampleMenu.setStyle(menuBase));

        javafx.scene.control.MenuItem simple = new javafx.scene.control.MenuItem(
                "Simple (6 nodes) \u2014 General purpose");
        simple.setOnAction(e -> loadSampleSimple());
        javafx.scene.control.MenuItem tree = new javafx.scene.control.MenuItem("Small Tree (4 nodes) \u2014 BFS / DFS");
        tree.setOnAction(e -> loadSampleTree());
        javafx.scene.control.MenuItem complete = new javafx.scene.control.MenuItem(
                "Complete K5 (5 nodes) \u2014 Kruskal / Prim's / TSP");
        complete.setOnAction(e -> loadSampleComplete());
        javafx.scene.control.MenuItem negEdges = new javafx.scene.control.MenuItem(
                "Negative Edges (5 nodes) \u2014 Bellman-Ford");
        negEdges.setOnAction(e -> loadSampleNegative());
        javafx.scene.control.MenuItem dag = new javafx.scene.control.MenuItem("DAG (6 nodes) \u2014 Topological Sort");
        dag.setOnAction(e -> loadSampleDAG());
        javafx.scene.control.MenuItem disconnected = new javafx.scene.control.MenuItem(
                "Disconnected (6 nodes) \u2014 Edge cases");
        disconnected.setOnAction(e -> loadSampleDisconnected());
        javafx.scene.control.MenuItem largeGraph = new javafx.scene.control.MenuItem(
                "Large Graph (15 nodes) \u2014 Stress test");
        largeGraph.setOnAction(e -> loadSampleLargeGraph());
        sampleMenu.getItems().addAll(simple, tree, complete, negEdges, dag, disconnected, largeGraph);

        Button clearAllBtn = accentButton("\uD83D\uDDD1 Clear All", "#555e6b", "#7f8c8d");

        clearAllBtn.setOnAction(e -> clearAll());

        // Status indicator
        Label statusIcon = new Label("\u2728");
        statusIcon.setFont(Font.font("Arial", 16));
        Label statusTitle = new Label("Ready to visualize!");
        statusTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        statusTitle.setStyle("-fx-text-fill: #2ecc71;");
        Label statusDesc = new Label("Add edges and click Visualize\nto see your graph in action.");
        statusDesc.setFont(Font.font("Arial", 11));
        statusDesc.setStyle("-fx-text-fill: #8e9aaf;");
        VBox statusTexts = new VBox(0, statusTitle, statusDesc);
        HBox statusBox = new HBox(8, statusIcon, statusTexts);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(8, 20, 8, 20));
        statusBox.setStyle("-fx-background-color: rgba(46,204,113,0.08); -fx-background-radius: 10; "
                + "-fx-border-color: #2ecc71; -fx-border-radius: 10; -fx-border-width: 1; -fx-border-style: dashed;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button visualizeBtn = accentButton("\uD83D\uDE80 Visualize Graph \u2192", "#3498db", "#2980b9");
        visualizeBtn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        visualizeBtn.setPadding(new Insets(12, 32, 12, 32));
        visualizeBtn.setOnAction(e -> visualize());

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox bottom = new HBox(14, sampleMenu, clearAllBtn, spacer, statusBox, spacer2, visualizeBtn);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(12, 20, 14, 20));
        bottom.setStyle("-fx-background-color: linear-gradient(to right, #0f1923, #141e2b, #0f1923); "
                + "-fx-border-color: #2a3a4e; -fx-border-width: 1 0 0 0;");

        return bottom;
    }

    private void addEdge() {
        errorLabel.setText("");

        int nodeCount = parseNodeCount();
        if (nodeCount < 0)
            return;

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
            if (weight == 0) {
                errorLabel.setText("Weight cannot be zero.");
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
            pranjal();
            return;
        }

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

    private void loadSampleSimple() {
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

    private void loadSampleTree() {
        nodeCountField.setText("4");
        directedRadio.setSelected(false);
        undirectedRadio.setSelected(true);
        edgeData.clear();
        edgeData.add(new EdgeEntry(0, 1, 3));
        edgeData.add(new EdgeEntry(0, 2, 5));
        edgeData.add(new EdgeEntry(1, 3, 2));
        errorLabel.setText("");
    }

    private void loadSampleComplete() {
        nodeCountField.setText("5");
        directedRadio.setSelected(false);
        undirectedRadio.setSelected(true);
        edgeData.clear();
        edgeData.add(new EdgeEntry(0, 1, 2));
        edgeData.add(new EdgeEntry(0, 2, 6));
        edgeData.add(new EdgeEntry(0, 3, 3));
        edgeData.add(new EdgeEntry(0, 4, 7));
        edgeData.add(new EdgeEntry(1, 2, 4));
        edgeData.add(new EdgeEntry(1, 3, 8));
        edgeData.add(new EdgeEntry(1, 4, 5));
        edgeData.add(new EdgeEntry(2, 3, 1));
        edgeData.add(new EdgeEntry(2, 4, 9));
        edgeData.add(new EdgeEntry(3, 4, 3));
        errorLabel.setText("");
    }

    private void loadSampleNegative() {
        nodeCountField.setText("5");
        directedRadio.setSelected(true);
        undirectedRadio.setSelected(false);
        edgeData.clear();
        edgeData.add(new EdgeEntry(0, 1, 6));
        edgeData.add(new EdgeEntry(0, 3, 7));
        edgeData.add(new EdgeEntry(1, 2, 5));
        edgeData.add(new EdgeEntry(1, 3, 8));
        edgeData.add(new EdgeEntry(1, 4, -4));
        edgeData.add(new EdgeEntry(2, 1, -2));
        edgeData.add(new EdgeEntry(3, 2, -3));
        errorLabel.setText("");
    }

    private void loadSampleDAG() {
        nodeCountField.setText("6");
        directedRadio.setSelected(true);
        undirectedRadio.setSelected(false);
        edgeData.clear();
        edgeData.add(new EdgeEntry(5, 2, 1));
        edgeData.add(new EdgeEntry(5, 0, 1));
        edgeData.add(new EdgeEntry(4, 0, 1));
        edgeData.add(new EdgeEntry(4, 1, 1));
        edgeData.add(new EdgeEntry(2, 3, 1));
        edgeData.add(new EdgeEntry(3, 1, 1));
        errorLabel.setText("");
    }

    private void loadSampleLargeGraph() {
        nodeCountField.setText("15");
        directedRadio.setSelected(false);
        undirectedRadio.setSelected(true);
        edgeData.clear();
        // 15-node graph: backbone chain + selective cross-links for good coverage
        int[][] edges = {
                // Backbone chain (0 → 14)
                { 0, 1, 4 }, { 1, 2, 7 }, { 2, 3, 3 }, { 3, 4, 6 }, { 4, 5, 9 }, { 5, 6, 2 }, { 6, 7, 8 },
                { 7, 8, 5 }, { 8, 9, 11 }, { 9, 10, 3 }, { 10, 11, 7 }, { 11, 12, 4 }, { 12, 13, 6 }, { 13, 14, 5 },
                // Cross-links for interesting shortest paths
                { 0, 4, 12 }, { 0, 7, 18 }, { 1, 5, 10 }, { 2, 8, 14 }, { 3, 9, 8 },
                { 4, 10, 15 }, { 5, 11, 6 }, { 6, 12, 9 }, { 7, 13, 11 }, { 8, 14, 7 },
                // A few long-range shortcuts
                { 0, 14, 25 }, { 1, 10, 16 }, { 3, 12, 13 }, { 5, 13, 10 }
        };
        for (int[] e : edges) {
            edgeData.add(new EdgeEntry(e[0], e[1], e[2]));
        }
        errorLabel.setText("");
    }

    private void loadSampleDisconnected() {
        nodeCountField.setText("6");
        directedRadio.setSelected(false);
        undirectedRadio.setSelected(true);
        edgeData.clear();
        edgeData.add(new EdgeEntry(0, 1, 3));
        edgeData.add(new EdgeEntry(1, 2, 4));
        edgeData.add(new EdgeEntry(3, 4, 5));
        edgeData.add(new EdgeEntry(4, 5, 2));
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
        if (nodeCount < 0)
            return;

        if (edgeData.isEmpty()) {
            errorLabel.setText("Please add at least one edge.");
            return;
        }

        boolean isDirected = directedRadio.isSelected();
        Graph graph = new Graph(isDirected);

        for (int i = 0; i < nodeCount; i++) {
            graph.addVertex(i);
        }

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
            pranjal();
            if (count < 1 || count > 50) {
                errorLabel.setText("Node count must be between 1 and 50.");
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
            if (count >= 1 && count <= 50) {
                nodeInfoLabel.setText("Nodes: 0 to " + (count - 1));
                nodeInfoLabel.setStyle("-fx-text-fill: #2ecc71;");
            } else {
                nodeInfoLabel.setText("Must be 1–50");
                nodeInfoLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        } catch (NumberFormatException e) {
            nodeInfoLabel.setText("Enter a number");
            nodeInfoLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

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
        String base = "-fx-background-color: #1a2332; "
                + "-fx-text-fill: #ecf0f1; "
                + "-fx-prompt-text-fill: #555e6b; "
                + "-fx-border-color: #2a3a4e; "
                + "-fx-border-radius: 8; "
                + "-fx-background-radius: 8; "
                + "-fx-padding: 8 12;";
        String focus = "-fx-background-color: #1a2332; "
                + "-fx-text-fill: #ecf0f1; "
                + "-fx-prompt-text-fill: #555e6b; "
                + "-fx-border-color: #3498db; "
                + "-fx-border-radius: 8; "
                + "-fx-background-radius: 8; "
                + "-fx-padding: 8 12;";
        tf.setStyle(base);
        tf.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            tf.setStyle(isFocused ? focus : base);
        });
        return tf;
    }

    private Separator styledSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #2a3a4e; -fx-padding: 0;");
        return sep;
    }

    private Button accentButton(String text, String bg, String hoverBg) {
        String base = "-fx-background-color: " + bg + "; -fx-text-fill: white; "
                + "-fx-padding: 10 20; -fx-background-radius: 20; -fx-cursor: hand;";
        String hover = "-fx-background-color: " + hoverBg + "; -fx-text-fill: white; "
                + "-fx-padding: 10 20; -fx-background-radius: 20; -fx-cursor: hand; "
                + "-fx-effect: dropshadow(gaussian, " + hoverBg + ", 8, 0.3, 0, 0);";
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    public static class EdgeEntry {
        private final SimpleIntegerProperty from;
        private final SimpleIntegerProperty to;
        private final SimpleIntegerProperty weight;

        public EdgeEntry(int from, int to, int weight) {
            this.from = new SimpleIntegerProperty(from);
            this.to = new SimpleIntegerProperty(to);
            this.weight = new SimpleIntegerProperty(weight);
        }

        public int getFrom() {
            return from.get();
        }

        public int getTo() {
            return to.get();
        }

        public int getWeight() {
            return weight.get();
        }

        public SimpleIntegerProperty fromProperty() {
            return from;
        }

        public SimpleIntegerProperty toProperty() {
            return to;
        }

        public SimpleIntegerProperty weightProperty() {
            return weight;
        }
    }
}
