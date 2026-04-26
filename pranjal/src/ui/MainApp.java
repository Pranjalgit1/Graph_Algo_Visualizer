package ui;

import algorithms.BFS;
import algorithms.BellmanFord;
import algorithms.DFS;
import algorithms.Dijkstra;
import algorithms.FloydWarshall;
import algorithms.Kruskal;
import algorithms.Prims;
import algorithms.TSP;
import algorithms.TopologicalSort;
import graph.Graph;
import graph.GraphVisualData;
import step.Step;
import step.StepType;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainApp extends Application {

    private static final double CANVAS_WIDTH = 750;
    private static final double CANVAS_HEIGHT = 600;
    private static final double COMP_CANVAS_W = 680;
    private static final double COMP_CANVAS_H = 480;
    private static final double BASE_STEP_DELAY = 0.6;

    private Stage primaryStage;
    private Graph graph;
    private boolean isDirectedGraph = false;
    private boolean comparisonMode = false;
    private double currentSpeedMultiplier = 1.0;
    private Button btnPlayPause;
    private Slider speedSlider;
    private Label speedLabel;

    private Pane canvas;
    private GraphRenderer renderer;
    private GraphVisualData visualData;
    private StepAnimator animator;
    private Label stepLabel;
    private Label stepDescriptionLabel;
    private Label runtimeLabel;
    private VBox runtimeSection;
    private String selectedAlgorithm = null;
    private final List<Button> algoButtons = new ArrayList<>();
    private long algorithmRuntimeNanos = 0;
    private Label traversalLabel;
    private Label outputLabel;
    private VBox traversalBox;
    private final Set<Integer> traversalOrderSet = new LinkedHashSet<>();
    private Button btnKruskal;
    private Button btnPrims;
    private Button btnTopoSort;

    private int sourceNode = 0;
    private Label sourceLabel;

    private int destinationNode = -1;
    private Label destinationLabel;

    private ProgressBar progressBar;

    private VBox algoInfoPanel;
    private Label algoNameLabel;
    private Label algoComplexityLabel;
    private VBox pseudocodeBox;

    private Pane leftCanvas;
    private GraphRenderer leftRenderer;
    private GraphVisualData leftVisualData;
    private StepAnimator leftAnimator;
    private String leftAlgorithm = null;
    private long leftRuntimeNanos = 0;
    private Label leftStepLabel;
    private Label leftStepDescLabel;
    private Label leftRuntimeLabel;
    private final List<Button> leftAlgoButtons = new ArrayList<>();
    private Label leftTraversalLabel;
    private VBox leftTraversalBox;
    private final Set<Integer> leftTraversalOrderSet = new LinkedHashSet<>();

    private Pane rightCanvas;
    private GraphRenderer rightRenderer;
    private GraphVisualData rightVisualData;
    private StepAnimator rightAnimator;
    private String rightAlgorithm = null;
    private long rightRuntimeNanos = 0;
    private Label rightStepLabel;
    private Label rightStepDescLabel;
    private Label rightRuntimeLabel;
    private final List<Button> rightAlgoButtons = new ArrayList<>();
    private Label rightTraversalLabel;
    private VBox rightTraversalBox;
    private final Set<Integer> rightTraversalOrderSet = new LinkedHashSet<>();

    private boolean leftComplete = false;
    private boolean rightComplete = false;

    private int compSourceNode = 0;
    private int compDestinationNode = 3;
    private ComboBox<Integer> leftSourceCombo;
    private ComboBox<Integer> leftDestCombo;
    private ComboBox<Integer> rightSourceCombo;
    private ComboBox<Integer> rightDestCombo;

    private VBox leftPseudocodeBox;
    private VBox rightPseudocodeBox;

    private Label leftTimerLabel;
    private Label rightTimerLabel;
    private long leftStartTimeMs = 0;
    private long rightStartTimeMs = 0;
    private javafx.animation.AnimationTimer compTimer;

    private Label leftNodeInfoLabel;
    private Label rightNodeInfoLabel;

    private boolean isFullscreen = false;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showInputScreen();
    }

    private void pranjal() {
        return;
    }

    private void showInputScreen() {
        GraphInputScreen inputScreen = new GraphInputScreen(primaryStage);
        inputScreen.setOnVisualize((graph, isDirected) -> {
            showVisualization(graph, isDirected);
        });
        inputScreen.show();
        pranjal();
    }

    private void showVisualization(Graph inputGraph, boolean isDirected) {
        this.graph = inputGraph;
        this.isDirectedGraph = isDirected;
        pranjal();
        stopAllAnimators();

        HBox header = buildHeader();
        BorderPane root = new BorderPane();

        if (comparisonMode) {
            buildComparisonLayout(root, header);
        } else {
            buildSingleLayout(root, header);
        }

        Scene scene = new Scene(root);

        scene.setOnKeyPressed(e -> {
            if (comparisonMode) {
                switch (e.getCode()) {
                    case SPACE:
                        compTogglePlayPause();
                        break;
                    case RIGHT:
                        compStepForward();
                        break;
                    case LEFT:
                        compStepBackward();
                        break;
                    case R:
                        compResetAll();
                        break;
                    default:
                        break;
                }
            } else {
                switch (e.getCode()) {
                    case SPACE:
                        togglePlayPause();
                        break;
                    case RIGHT:
                        if (animator != null)
                            animator.stepForward();
                        break;
                    case LEFT:
                        if (animator != null)
                            animator.stepBackward();
                        break;
                    case R:
                        resetAll();
                        break;
                    case DIGIT1:
                    case NUMPAD1:
                        selectAlgoByIndex(0);
                        break;
                    case DIGIT2:
                    case NUMPAD2:
                        selectAlgoByIndex(1);
                        break;
                    case DIGIT3:
                    case NUMPAD3:
                        selectAlgoByIndex(2);
                        break;
                    case DIGIT4:
                    case NUMPAD4:
                        selectAlgoByIndex(3);
                        break;
                    case DIGIT5:
                    case NUMPAD5:
                        selectAlgoByIndex(4);
                        break;
                    case DIGIT6:
                    case NUMPAD6:
                        selectAlgoByIndex(5);
                        break;
                    case DIGIT7:
                    case NUMPAD7:
                        selectAlgoByIndex(6);
                        break;
                    case DIGIT8:
                    case NUMPAD8:
                        selectAlgoByIndex(7);
                        break;
                    case DIGIT9:
                    case NUMPAD9:
                        selectAlgoByIndex(8);
                        break;
                    default:
                        break;
                }
            }
            e.consume();
        });

        primaryStage.setTitle("Graph Algorithm Visualizer");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.show();
        pranjal();
    }

    private HBox buildHeader() {
        Button newGraphBtn = styledButton("\u2190 New Graph", "#8e44ad", "#6c3483");
        newGraphBtn.setOnAction(e -> {
            stopAllAnimators();
            showInputScreen();
        });

        Label title = new Label("Graph Algorithm Visualizer");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: white;");

        Label subtitle = new Label("");
        if (comparisonMode) {
            subtitle.setText("\u2694 COMPARE MODE");
            subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            subtitle.setStyle("-fx-text-fill: #00e5ff;");
        }
        VBox titleBox = new VBox(2, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);

        String compBtnText = comparisonMode ? "\u2726 Single Mode" : "\u2694 Compare Mode";
        String compBg = comparisonMode ? "#27ae60" : "#e67e22";
        String compHover = comparisonMode ? "#1e8449" : "#d35400";
        Button compToggle = styledButton(compBtnText, compBg, compHover);
        compToggle.setOnAction(e -> {
            comparisonMode = !comparisonMode;
            showVisualization(graph, isDirectedGraph);
        });
        compToggle.setVisible(true);
        compToggle.setManaged(true);

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        HBox header = new HBox(10, newGraphBtn, leftSpacer, titleBox, rightSpacer, compToggle);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(10, 14, 10, 14));
        header.setStyle("-fx-background-color: linear-gradient(to right, #1a1a2e, #16213e, #1a1a2e);");

        return header;
    }

    private void stopAllAnimators() {
        if (animator != null)
            animator.stop();
        if (leftAnimator != null)
            leftAnimator.stop();
        if (rightAnimator != null)
            rightAnimator.stop();
    }

    private void buildSingleLayout(BorderPane root, HBox header) {
        this.selectedAlgorithm = null;
        this.algoButtons.clear();
        pranjal();
        visualData = new GraphVisualData();
        visualData.buildFromGraph(graph, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2, 250);

        canvas = new Pane();
        canvas.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        canvas.setStyle("-fx-background-color: #1a2332; -fx-background-radius: 8;");

        renderer = new GraphRenderer(canvas);
        renderer.setSelectedSourceNode(sourceNode);
        if (destinationNode >= 0) {
            renderer.setDestinationNode(destinationNode);
        }
        renderer.setOnSourceNodeChanged(() -> {
            sourceNode = renderer.getSelectedSourceNode();
            if (sourceLabel != null) {
                sourceLabel.setText("Source: Node " + sourceNode);
            }
        });

        canvas.setOnMouseClicked(e -> {
            if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                int nearest = findNearestNode(e.getX(), e.getY());
                if (nearest >= 0) {
                    destinationNode = (destinationNode == nearest) ? -1 : nearest;
                    renderer.setDestinationNode(destinationNode);
                    if (destinationLabel != null) {
                        destinationLabel.setText(destinationNode >= 0 ? "Dest: Node " + destinationNode : "Dest: None");
                    }
                }
            }
        });

        renderer.render(visualData);
        buildTraversalOverlay();

        animator = new StepAnimator(renderer, BASE_STEP_DELAY / currentSpeedMultiplier);
        animator.setVisualData(visualData);
        animator.setOnStepChange(this::updateStepDisplay);
        animator.setOnComplete(this::onAnimationComplete);

        Label canvasTitle = new Label("Current Graph");
        canvasTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        canvasTitle.setStyle("-fx-text-fill: white;");
        Label nodeBadge = new Label(graph.getVertices().size() + " Nodes");
        nodeBadge.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        nodeBadge.setStyle(
                "-fx-text-fill: white; -fx-background-color: #3498db; -fx-padding: 2 8; -fx-background-radius: 10;");
        Label edgeBadge = new Label(graph.getEdgeCount() + " Edges");
        edgeBadge.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        edgeBadge.setStyle(
                "-fx-text-fill: white; -fx-background-color: #2ecc71; -fx-padding: 2 8; -fx-background-radius: 10;");
        HBox canvasTitleRow = new HBox(10, canvasTitle, nodeBadge, edgeBadge);
        canvasTitleRow.setAlignment(Pos.CENTER_LEFT);
        canvasTitleRow.setPadding(new Insets(8, 12, 4, 12));

        Pane canvasContainer = new Pane();
        canvasContainer.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        canvasContainer.setStyle(
                "-fx-background-color: #1a2332; -fx-background-radius: 8; -fx-border-color: #2a3a4e; -fx-border-radius: 8; -fx-border-width: 1;");
        canvasContainer.getChildren().addAll(canvas, traversalBox);
        traversalBox.layoutXProperty().bind(
                canvasContainer.widthProperty().subtract(traversalBox.widthProperty()).subtract(10));
        traversalBox.layoutYProperty().bind(
                canvasContainer.heightProperty().subtract(traversalBox.heightProperty()).subtract(10));

        VBox leftColumn = new VBox(4, canvasTitleRow, canvasContainer);
        leftColumn.setPadding(new Insets(8));
        leftColumn.setStyle("-fx-background-color: #0f1923;");

        VBox centerColumn = buildCenterPanel();

        VBox rightColumn = buildLegendSection();

        HBox mainContent = new HBox(0, leftColumn, centerColumn, rightColumn);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        mainContent.setStyle("-fx-background-color: #0f1923;");

        HBox infoStrip = buildInfoStrip();

        VBox bottomPanel = buildBottomPanel();

        VBox fullBottom = new VBox(0, infoStrip, bottomPanel);

        ScrollPane scroll = new ScrollPane(mainContent);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: #0f1923; -fx-background-color: #0f1923;");

        header.setStyle("-fx-background-color: linear-gradient(to right, #1a1a2e, #16213e, #1a1a2e);");

        root.setTop(header);
        root.setCenter(scroll);
        root.setRight(null);
        root.setBottom(fullBottom);
    }

    private Label infoAlgoLabel = null;
    private Label infoComplexityLabel = null;
    private Label infoSourceLabel = null;
    private Label infoDestLabel = null;
    private Label infoStepLabel = null;

    private void updateCanvasInfoBar() {

    }

    private int findNearestNode(double px, double py) {
        if (visualData == null)
            return -1;
        double bestDist = 30.0;
        int best = -1;
        for (graph.VisualNode vn : visualData.getAllNodes().values()) {
            double dx = vn.getX() - px;
            double dy = vn.getY() - py;
            double d = Math.sqrt(dx * dx + dy * dy);
            if (d < bestDist) {
                bestDist = d;
                best = vn.getId();
            }
        }
        return best;
    }

    private VBox buildCenterPanel() {
        VBox panel = new VBox(8);
        panel.setPrefWidth(360);
        panel.setMinWidth(340);
        panel.setPadding(new Insets(8, 8, 8, 0));
        panel.setStyle("-fx-background-color: #0f1923;");

        Label algoHeading = new Label("Algorithm");
        algoHeading.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        algoHeading.setStyle("-fx-text-fill: white;");
        Label algoSub = new Label("Select an algorithm to start");
        algoSub.setFont(Font.font("Arial", 11));
        algoSub.setStyle("-fx-text-fill: #555e6b;");

        Button btnBFS = algoToggleButton("BFS");
        Button btnDFS = algoToggleButton("DFS");
        Button btnDijkstra = algoToggleButton("Dijkstra");
        btnKruskal = algoToggleButton("Kruskal");
        btnPrims = algoToggleButton("Prim's");
        Button btnBellmanFord = algoToggleButton("Bellman-Ford");
        Button btnFloydWarshall = algoToggleButton("Floyd-Warshall");
        btnTopoSort = algoToggleButton("Topo Sort");
        Button btnTSP = algoToggleButton("TSP");

        algoButtons.add(btnBFS);
        algoButtons.add(btnDFS);
        algoButtons.add(btnDijkstra);
        algoButtons.add(btnKruskal);
        algoButtons.add(btnPrims);
        algoButtons.add(btnBellmanFord);
        algoButtons.add(btnFloydWarshall);
        pranjal();
        algoButtons.add(btnTopoSort);
        algoButtons.add(btnTSP);

        btnBFS.setOnAction(e -> selectAlgorithm("BFS", btnBFS));
        btnDFS.setOnAction(e -> selectAlgorithm("DFS", btnDFS));
        btnDijkstra.setOnAction(e -> selectAlgorithm("Dijkstra", btnDijkstra));
        btnKruskal.setOnAction(e -> selectAlgorithm("Kruskal", btnKruskal));
        btnPrims.setOnAction(e -> selectAlgorithm("Prim's", btnPrims));
        btnBellmanFord.setOnAction(e -> selectAlgorithm("Bellman-Ford", btnBellmanFord));
        btnFloydWarshall.setOnAction(e -> selectAlgorithm("Floyd-Warshall", btnFloydWarshall));
        btnTopoSort.setOnAction(e -> selectAlgorithm("Topo Sort", btnTopoSort));
        btnTSP.setOnAction(e -> selectAlgorithm("TSP", btnTSP));

        if (isDirectedGraph) {
            btnKruskal.setDisable(true);
            btnPrims.setDisable(true);
            btnKruskal.setOpacity(0.4);
            btnPrims.setOpacity(0.4);
        } else {
            btnTopoSort.setDisable(true);
            btnTopoSort.setOpacity(0.4);
        }

        FlowPane algoFlow = new FlowPane(6, 6);
        algoFlow.getChildren().addAll(btnBFS, btnDFS, btnDijkstra, btnKruskal, btnPrims,
                btnBellmanFord, btnFloydWarshall, btnTopoSort, btnTSP);
        algoFlow.setAlignment(Pos.CENTER_LEFT);

        VBox algoCard = new VBox(8, algoHeading, algoSub, algoFlow);
        algoCard.setPadding(new Insets(14));
        algoCard.setStyle("-fx-background-color: #141e2b; -fx-background-radius: 10; "
                + "-fx-border-color: #2a3a4e; -fx-border-radius: 10; -fx-border-width: 1;");

        Label stepsHeading = new Label("Algorithm Steps");
        stepsHeading.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        stepsHeading.setStyle("-fx-text-fill: white;");

        stepDescriptionLabel = new Label("Choose an algorithm and press Play to see steps here.");
        stepDescriptionLabel.setFont(Font.font("Arial", 12));
        stepDescriptionLabel.setStyle("-fx-text-fill: #555e6b;");
        stepDescriptionLabel.setWrapText(true);
        stepDescriptionLabel.setMaxWidth(270);

        algoNameLabel = new Label("No algorithm selected");
        algoNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        algoNameLabel.setStyle("-fx-text-fill: #f39c12;");
        algoNameLabel.setWrapText(true);
        algoNameLabel.setMaxWidth(270);
        algoNameLabel.setVisible(false);
        algoNameLabel.setManaged(false);

        algoComplexityLabel = new Label("");
        algoComplexityLabel.setFont(Font.font("Arial", 12));
        algoComplexityLabel.setStyle("-fx-text-fill: #bdc3c7;");
        algoComplexityLabel.setWrapText(true);
        algoComplexityLabel.setMaxWidth(270);

        pseudocodeBox = new VBox(2);
        pseudocodeBox.setPadding(new Insets(6, 0, 0, 0));

        Label emptyIcon = new Label("\u2261");
        emptyIcon.setFont(Font.font("Arial", 40));
        emptyIcon.setStyle("-fx-text-fill: #2a3a4e;");
        Label emptyText = new Label("Steps will appear here during execution");
        emptyText.setFont(Font.font("Arial", 11));
        emptyText.setStyle("-fx-text-fill: #555e6b;");
        VBox emptyState = new VBox(4, emptyIcon, emptyText);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setPadding(new Insets(20));

        VBox stepsCard = new VBox(6, stepsHeading, stepDescriptionLabel, algoComplexityLabel, pseudocodeBox,
                emptyState);
        stepsCard.setPadding(new Insets(14));
        stepsCard.setStyle("-fx-background-color: #141e2b; -fx-background-radius: 10; "
                + "-fx-border-color: #2a3a4e; -fx-border-radius: 10; -fx-border-width: 1;");
        VBox.setVgrow(stepsCard, Priority.ALWAYS);

        algoInfoPanel = stepsCard;
        runtimeSection = buildRuntimeSection();

        panel.getChildren().addAll(algoCard, stepsCard);
        return panel;
    }

    private HBox buildInfoStrip() {

        Label srcDestTitle = new Label("Source / Destination");
        srcDestTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        srcDestTitle.setStyle("-fx-text-fill: white;");
        Label srcDestSub = new Label("Select start and end nodes (optional)");
        srcDestSub.setFont(Font.font("Arial", 10));
        srcDestSub.setStyle("-fx-text-fill: #555e6b;");

        sourceLabel = new Label("Source");
        sourceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        sourceLabel.setStyle("-fx-text-fill: #e74c3c;");

        List<Integer> nodeIds = new java.util.ArrayList<>(graph.getVertices());
        java.util.Collections.sort(nodeIds);

        ComboBox<Integer> srcCombo = styledComboBox(nodeIds, sourceNode);
        srcCombo.setOnAction(e -> {
            if (srcCombo.getValue() != null) {
                sourceNode = srcCombo.getValue();
                renderer.setSelectedSourceNode(sourceNode);
                renderer.render(visualData);
            }
        });

        destinationLabel = new Label("Destination");
        destinationLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        destinationLabel.setStyle("-fx-text-fill: #2ecc71;");

        ComboBox<Integer> destCombo = styledComboBox(nodeIds, destinationNode >= 0 ? destinationNode : nodeIds.get(0));
        destCombo.getItems().add(0, -1);
        if (destinationNode < 0)
            destCombo.setValue(-1);
        destCombo.setOnAction(e -> {
            if (destCombo.getValue() != null) {
                destinationNode = destCombo.getValue();
                renderer.setDestinationNode(destinationNode);
                renderer.render(visualData);
            }
        });

        HBox srcRow = new HBox(6, sourceLabel, srcCombo);
        srcRow.setAlignment(Pos.CENTER_LEFT);
        HBox destRow = new HBox(6, destinationLabel, destCombo);
        destRow.setAlignment(Pos.CENTER_LEFT);

        VBox srcDestCard = new VBox(4, srcDestTitle, srcDestSub, srcRow, destRow);
        srcDestCard.setPadding(new Insets(10));
        srcDestCard.setPrefWidth(200);
        srcDestCard.setStyle("-fx-background-color: #141e2b; -fx-background-radius: 10; "
                + "-fx-border-color: #2a3a4e; -fx-border-radius: 10; -fx-border-width: 1;");

        Label graphTitle = new Label("Graph Info");
        graphTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        graphTitle.setStyle("-fx-text-fill: white;");
        Label graphSub = new Label("Quick overview of your graph");
        graphSub.setFont(Font.font("Arial", 10));
        graphSub.setStyle("-fx-text-fill: #555e6b;");

        int totalWeight = 0;
        for (int v : graph.getVertices()) {
            for (int[] edge : graph.getNeighbors(v)) {
                totalWeight += Math.abs(edge[1]);
            }
        }
        if (!graph.isDirected())
            totalWeight /= 2;

        Label nodesInfo = new Label("\u2B24 Nodes: " + graph.getVertices().size());
        nodesInfo.setStyle("-fx-text-fill: #3498db; -fx-font-size: 11;");
        Label edgesInfo = new Label("\u2737 Edges: " + graph.getEdgeCount());
        edgesInfo.setStyle("-fx-text-fill: #e67e22; -fx-font-size: 11;");
        Label typeInfo = new Label("\u2295 Type: " + (graph.isDirected() ? "Directed" : "Undirected"));
        typeInfo.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 11;");
        Label weightInfo = new Label("\u2211 Total Weight: " + totalWeight);
        weightInfo.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 11;");

        VBox graphCard = new VBox(3, graphTitle, graphSub, nodesInfo, edgesInfo, typeInfo, weightInfo);
        graphCard.setPadding(new Insets(10));
        graphCard.setPrefWidth(200);
        graphCard.setStyle("-fx-background-color: #141e2b; -fx-background-radius: 10; "
                + "-fx-border-color: #2a3a4e; -fx-border-radius: 10; -fx-border-width: 1;");

        Label outTitle = new Label("Algorithm Output");
        outTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        outTitle.setStyle("-fx-text-fill: white;");
        Label outSub = new Label("Results will appear here");
        outSub.setFont(Font.font("Arial", 10));
        outSub.setStyle("-fx-text-fill: #555e6b;");

        outputLabel = new Label("Output will be shown here after execution");
        outputLabel.setFont(Font.font("Arial", 11));
        outputLabel.setStyle("-fx-text-fill: #555e6b;");
        outputLabel.setWrapText(true);
        outputLabel.setMaxWidth(200);

        VBox outCard = new VBox(4, outTitle, outSub, outputLabel);
        outCard.setPadding(new Insets(10));
        outCard.setPrefWidth(200);
        outCard.setStyle("-fx-background-color: #141e2b; -fx-background-radius: 10; "
                + "-fx-border-color: #2a3a4e; -fx-border-radius: 10; -fx-border-width: 1;");

        Label ctrlTitle = new Label("Controls Guide");
        ctrlTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        ctrlTitle.setStyle("-fx-text-fill: white;");
        Label ctrl1 = new Label("\u25B6 Play — Start or resume algorithm");
        ctrl1.setStyle("-fx-text-fill: #8e9aaf; -fx-font-size: 10;");
        Label ctrl2 = new Label("\u23ED Next Step — Go to next step");
        ctrl2.setStyle("-fx-text-fill: #8e9aaf; -fx-font-size: 10;");
        Label ctrl3 = new Label("\uD83D\uDD04 Reset — Clear and start over");
        ctrl3.setStyle("-fx-text-fill: #8e9aaf; -fx-font-size: 10;");
        Label ctrl4 = new Label("\u26A1 Speed — Adjust animation speed");
        ctrl4.setStyle("-fx-text-fill: #8e9aaf; -fx-font-size: 10;");

        VBox ctrlCard = new VBox(3, ctrlTitle, ctrl1, ctrl2, ctrl3, ctrl4);
        ctrlCard.setPadding(new Insets(10));
        ctrlCard.setPrefWidth(200);
        ctrlCard.setStyle("-fx-background-color: #141e2b; -fx-background-radius: 10; "
                + "-fx-border-color: #2a3a4e; -fx-border-radius: 10; -fx-border-width: 1;");

        HBox strip = new HBox(10, srcDestCard, graphCard, outCard, ctrlCard);
        strip.setPadding(new Insets(8, 12, 4, 12));
        strip.setAlignment(Pos.CENTER);
        strip.setStyle("-fx-background-color: #0f1923; -fx-border-color: #2a3a4e; -fx-border-width: 1 0 0 0;");
        HBox.setHgrow(outCard, Priority.ALWAYS);

        return strip;
    }

    private VBox buildLegendSection() {
        VBox box = new VBox(4);
        box.setPadding(new Insets(8, 12, 8, 8));
        box.setPrefWidth(220);
        box.setMinWidth(200);
        box.setStyle("-fx-background-color: #0f1923;");

        Label heading = new Label("Legend \u24D8");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        heading.setStyle("-fx-text-fill: white;");
        heading.setPadding(new Insets(4, 0, 4, 0));

        Label nodeHead = new Label("Node States  (What nodes mean)");
        nodeHead.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        nodeHead.setStyle("-fx-text-fill: #8e9aaf;");

        VBox nodeItems = new VBox(3,
                legendItemFriendly(Color.web("#555e6b"), "Not Visited", "Node not visited yet"),
                legendItemFriendly(Color.web("#f39c12"), "In Queue / Stack", "Node in queue/stack to explore"),
                legendItemFriendly(Color.web("#e74c3c"), "Visiting", "Currently exploring this node"),
                legendItemFriendly(Color.web("#2ecc71"), "Visited", "Node has been visited"),
                legendItemFriendly(Color.web("#9b59b6"), "Backtracked", "Returned from this node"),
                legendRingFriendly(Color.web("#3498db"), "Source Node", "Starting point"),
                legendRingFriendly(Color.web("#e74c3c"), "Destination Node", "Target point"));

        Label edgeHead = new Label("Edge States  (What edges mean)");
        edgeHead.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        edgeHead.setStyle("-fx-text-fill: #8e9aaf;");
        edgeHead.setPadding(new Insets(6, 0, 0, 0));

        VBox edgeItems = new VBox(3,
                legendLineFriendly(Color.web("#95a5a6"), "Unexplored", "Edge not explored yet"),
                legendLineFriendly(Color.web("#e67e22"), "Exploring", "Edge is being explored"),
                legendLineFriendly(Color.web("#2ecc71"), "Discovered", "Edge discovered"),
                legendLineFriendly(Color.web("#e74c3c"), "Part of Path", "Edge in final path"),
                legendLineFriendly(Color.web("#00bcd4"), "Relaxed / Updated", "Edge was relaxed (e.g., Dijkstra)"));

        VBox legendCard = new VBox(4, heading, nodeHead, nodeItems, edgeHead, edgeItems);
        legendCard.setPadding(new Insets(12));
        legendCard.setStyle("-fx-background-color: #141e2b; -fx-background-radius: 10; "
                + "-fx-border-color: #2a3a4e; -fx-border-radius: 10; -fx-border-width: 1;");

        box.getChildren().add(legendCard);
        return box;
    }

    private HBox legendItemFriendly(Color color, String name, String desc) {
        Circle c = new Circle(6, color);
        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        nameLbl.setStyle("-fx-text-fill: white;");
        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("Arial", 9));
        descLbl.setStyle("-fx-text-fill: #555e6b;");
        VBox texts = new VBox(0, nameLbl, descLbl);
        HBox row = new HBox(8, c, texts);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox legendRingFriendly(Color color, String name, String desc) {
        Circle c = new Circle(6);
        c.setFill(Color.TRANSPARENT);
        c.setStroke(color);
        c.setStrokeWidth(2);
        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        nameLbl.setStyle("-fx-text-fill: white;");
        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("Arial", 9));
        descLbl.setStyle("-fx-text-fill: #555e6b;");
        VBox texts = new VBox(0, nameLbl, descLbl);
        HBox row = new HBox(8, c, texts);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox legendLineFriendly(Color color, String name, String desc) {
        Line l = new Line(0, 0, 16, 0);
        l.setStroke(color);
        l.setStrokeWidth(3);
        Label nameLbl = new Label(name);
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        nameLbl.setStyle("-fx-text-fill: white;");
        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("Arial", 9));
        descLbl.setStyle("-fx-text-fill: #555e6b;");
        VBox texts = new VBox(0, nameLbl, descLbl);
        HBox row = new HBox(8, l, texts);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox buildSourceSection() {
        VBox box = new VBox(4);
        box.setPadding(new Insets(10, 14, 8, 14));

        Label heading = sectionHeading("\uD83C\uDFAF Source / Dest");
        sourceLabel = new Label("Source: Node " + sourceNode);
        sourceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        sourceLabel.setStyle("-fx-text-fill: #e74c3c;");

        Label srcHint = new Label("Left-click a node to set source");
        srcHint.setFont(Font.font("Arial", 10));
        srcHint.setStyle("-fx-text-fill: #7f8c8d;");

        String destText = destinationNode >= 0 ? "Dest: Node " + destinationNode : "Dest: None";
        destinationLabel = new Label(destText);
        destinationLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        destinationLabel.setStyle("-fx-text-fill: #2ecc71;");

        Label destHint = new Label("Right-click a node to set destination");
        destHint.setFont(Font.font("Arial", 10));
        destHint.setStyle("-fx-text-fill: #7f8c8d;");

        box.getChildren().addAll(heading, sourceLabel, srcHint, destinationLabel, destHint);
        return box;
    }

    private VBox buildAlgoInfoPanel() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(12, 14, 12, 14));

        Label heading = sectionHeading("\uD83D\uDCCB Algorithm");
        algoNameLabel = new Label("No algorithm selected");
        algoNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        algoNameLabel.setStyle("-fx-text-fill: #f39c12;");
        algoNameLabel.setWrapText(true);
        algoNameLabel.setMaxWidth(250);

        algoComplexityLabel = new Label("");
        algoComplexityLabel.setFont(Font.font("Arial", 12));
        algoComplexityLabel.setStyle("-fx-text-fill: #bdc3c7;");
        algoComplexityLabel.setWrapText(true);
        algoComplexityLabel.setMaxWidth(250);

        pseudocodeBox = new VBox(2);
        pseudocodeBox.setPadding(new Insets(6, 0, 0, 0));

        box.getChildren().addAll(heading, algoNameLabel, algoComplexityLabel, pseudocodeBox);
        return box;
    }

    private void updateAlgoInfoPanel(String algorithm, step.StepType currentStepType) {
        AlgorithmInfo info = AlgorithmInfo.get(algorithm);
        if (info == null) {
            algoNameLabel.setText("No algorithm selected");
            algoComplexityLabel.setText("");
            pseudocodeBox.getChildren().clear();
            return;
        }

        algoNameLabel.setText(info.getName());
        algoComplexityLabel.setText("Time: " + info.getTimeComplexity() + "  |  Space: " + info.getSpaceComplexity());

        pseudocodeBox.getChildren().clear();
        String[] lines = info.getPseudocode();
        int highlightLine = currentStepType != null ? info.getHighlightLine(currentStepType) : -1;

        for (int i = 0; i < lines.length; i++) {
            Label line = new Label(lines[i]);
            line.setFont(Font.font("Consolas", 12));
            line.setMaxWidth(250);
            line.setWrapText(true);

            if (i == highlightLine) {
                line.setStyle("-fx-text-fill: #2ecc71; -fx-background-color: rgba(46,204,113,0.15); -fx-padding: 1 4;");
            } else {
                line.setStyle("-fx-text-fill: #bdc3c7; -fx-padding: 1 4;");
            }
            pseudocodeBox.getChildren().add(line);
        }
    }

    private VBox buildStepSection() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(12, 14, 12, 14));

        Label heading = sectionHeading("Step");
        box.getChildren().add(heading);

        stepDescriptionLabel = new Label("No algorithm running.");
        stepDescriptionLabel.setFont(Font.font("Arial", 12));
        stepDescriptionLabel.setStyle("-fx-text-fill: #bdc3c7;");
        stepDescriptionLabel.setWrapText(true);
        stepDescriptionLabel.setMaxWidth(210);

        box.getChildren().add(stepDescriptionLabel);
        return box;
    }

    private VBox buildRuntimeSection() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(12, 14, 16, 14));

        Label heading = sectionHeading("Runtime");
        box.getChildren().add(heading);

        runtimeLabel = new Label("\u2014");
        runtimeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        runtimeLabel.setStyle("-fx-text-fill: #bdc3c7;");

        box.getChildren().add(runtimeLabel);
        box.setVisible(false);
        box.setManaged(false);
        return box;
    }

    private VBox buildBottomPanel() {
        Button btnBack = styledButton("\u23EE Prev", "#e74c3c", "#c0392b");
        btnPlayPause = styledButton("\u25B6 Play", "#27ae60", "#1e8449");
        Button btnForward = styledButton("Next \u23ED", "#8e44ad", "#6c3483");
        Button btnReset = styledButton("\uD83D\uDD04 Reset", "#555e6b", "#7f8c8d");

        btnBack.setOnAction(e -> animator.stepBackward());
        btnForward.setOnAction(e -> animator.stepForward());
        btnPlayPause.setOnAction(e -> togglePlayPause());
        btnReset.setOnAction(e -> resetAll());

        Label speedText = new Label("Speed");
        speedText.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        speedText.setStyle("-fx-text-fill: #8e9aaf;");

        speedSlider = new Slider(0.25, 4.0, currentSpeedMultiplier);
        speedSlider.setPrefWidth(140);
        speedSlider.setBlockIncrement(0.25);

        speedLabel = new Label(String.format("%.1fx", currentSpeedMultiplier));
        speedLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        speedLabel.setStyle("-fx-text-fill: #f39c12;");
        speedLabel.setMinWidth(35);

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentSpeedMultiplier = newVal.doubleValue();
            double newDelay = BASE_STEP_DELAY / currentSpeedMultiplier;
            speedLabel.setText(String.format("%.1fx", currentSpeedMultiplier));
            animator.setStepDuration(newDelay);
        });

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        stepLabel = new Label("Step: 0 / 0");
        stepLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        stepLabel.setStyle("-fx-text-fill: white;");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(120);
        progressBar.setPrefHeight(12);
        progressBar.setStyle("-fx-accent: #3498db;");

        HBox playbackBar = new HBox(12, btnBack, btnPlayPause, btnForward, btnReset,
                spacer1, speedText, speedSlider, speedLabel, stepLabel, progressBar);
        playbackBar.setAlignment(Pos.CENTER);
        playbackBar.setPadding(new Insets(8, 20, 10, 20));

        VBox bottomPanel = new VBox(0, playbackBar);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-background-color: linear-gradient(to right, #0f1923, #141e2b, #0f1923); "
                + "-fx-border-color: #2a3a4e; -fx-border-width: 1 0 0 0;");
        pranjal();

        return bottomPanel;
    }

    private void selectAlgorithm(String name, Button selected) {
        resetAll();
        selectedAlgorithm = name;

        for (Button btn : algoButtons) {
            applyAlgoButtonStyle(btn, false);
        }
        applyAlgoButtonStyle(selected, true);

        if (algoInfoPanel != null) {
            updateAlgoInfoPanel(name, null);
        }

        updateCanvasInfoBar();
    }

    private void selectAlgoByIndex(int index) {
        if (index >= 0 && index < algoButtons.size()) {
            Button btn = algoButtons.get(index);
            if (!btn.isDisabled()) {
                String[] algoNames = { "BFS", "DFS", "Dijkstra", "Kruskal", "Prim's", "Bellman-Ford", "Floyd-Warshall",
                        "Topo Sort", "TSP" };
                selectAlgorithm(algoNames[index], btn);
            }
        }
    }

    private void runSelectedAlgorithm() {
        if (selectedAlgorithm == null)
            return;

        List<Step> steps;
        long startTime = System.nanoTime();

        switch (selectedAlgorithm) {
            case "BFS":
                steps = BFS.run(graph, sourceNode, destinationNode);
                break;
            case "DFS":
                steps = DFS.run(graph, sourceNode, destinationNode);
                break;
            case "Dijkstra":
                steps = Dijkstra.run(graph, sourceNode, destinationNode);
                break;
            case "Kruskal":
                steps = Kruskal.run(graph);
                break;
            case "Prim's":
                steps = Prims.run(graph, sourceNode);
                break;
            case "Bellman-Ford":
                steps = BellmanFord.run(graph, sourceNode);
                break;
            case "Floyd-Warshall":
                steps = FloydWarshall.run(graph);
                break;
            case "Topo Sort":
                steps = TopologicalSort.run(graph);
                break;
            case "TSP":
                steps = TSP.run(graph, sourceNode);
                break;
            default:
                return;
        }

        long endTime = System.nanoTime();
        algorithmRuntimeNanos = endTime - startTime;

        runtimeSection.setVisible(false);
        runtimeSection.setManaged(false);

        animator.load(steps);
        pranjal();
        animator.play();
        btnPlayPause.setText("\u23F8 Pause");
    }

    private void togglePlayPause() {
        if (animator.isPlaying()) {
            animator.pause();
            btnPlayPause.setText("\u25B6 Play");
        } else {
            if (animator.getTotalSteps() == 0 && selectedAlgorithm != null) {

                runSelectedAlgorithm();
            } else {
                animator.play();
                btnPlayPause.setText("\u23F8 Pause");
            }
        }
    }

    private void resetAll() {
        animator.stop();
        animator.load(List.of());
        renderer.render(visualData);
        btnPlayPause.setText("\u25B6 Play");
        stepLabel.setText("Step: 0 / 0");
        if (stepDescriptionLabel != null)
            stepDescriptionLabel.setText("No algorithm running.");
        runtimeSection.setVisible(false);
        runtimeSection.setManaged(false);
        runtimeLabel.setText("\u2014");
        algorithmRuntimeNanos = 0;

        if (progressBar != null) {
            progressBar.setProgress(0);
        }

        traversalOrderSet.clear();
        traversalLabel.setText("");
        traversalBox.setVisible(false);
    }

    private void updateStepDisplay() {
        int cur = animator.getCurrentIndex() + 1;
        int total = animator.getTotalSteps();
        stepLabel.setText("Step: " + cur + " / " + total);

        if (progressBar != null && total > 0) {
            progressBar.setProgress((double) cur / total);
        }

        Step currentStep = animator.getCurrentStep();
        if (currentStep != null) {
            String desc = currentStep.toDescription();
            if (stepDescriptionLabel != null)
                stepDescriptionLabel.setText(desc);

            if (algoInfoPanel != null && selectedAlgorithm != null) {
                updateAlgoInfoPanel(selectedAlgorithm, currentStep.getType());
            }
        }

        rebuildTraversalDisplay();
    }

    private void rebuildTraversalDisplay() {
        traversalOrderSet.clear();

        int currentIdx = animator.getCurrentIndex();
        if (currentIdx < 0) {
            traversalLabel.setText("");
            traversalBox.setVisible(false);
            return;
        }

        boolean isMST = "Kruskal".equals(selectedAlgorithm) || "Prim's".equals(selectedAlgorithm);
        boolean isTraversalAlgo = "BFS".equals(selectedAlgorithm)
                || "DFS".equals(selectedAlgorithm)
                || "Dijkstra".equals(selectedAlgorithm)
                || "Bellman-Ford".equals(selectedAlgorithm)
                || "Topo Sort".equals(selectedAlgorithm)
                || "TSP".equals(selectedAlgorithm)
                || isMST;

        if (!isTraversalAlgo) {
            traversalLabel.setText("");
            if (outputLabel != null)
                outputLabel.setText("");
            traversalBox.setVisible(false);
            return;
        }

        List<String> outputItems = new java.util.ArrayList<>();

        for (int i = 0; i <= currentIdx; i++) {
            Step s = animator.getStepAt(i);
            if (s != null) {
                if (isMST) {
                    if (s.getType() == StepType.EDGE_SELECTED) {
                        outputItems.add("(" + s.getFromNode() + "-" + s.getToNode() + ")");
                    }
                } else if ("Topo Sort".equals(selectedAlgorithm)) {
                    if (s.getType() == StepType.TOPO_PUSH_STACK) {
                        outputItems.add(0, String.valueOf(s.getNode()));
                    }
                } else {
                    if (s.getType() == StepType.VISIT_NODE || s.getType() == StepType.REACH_DESTINATION) {
                        String nodeStr = String.valueOf(s.getNode());
                        if (!outputItems.contains(nodeStr)) {
                            outputItems.add(nodeStr);
                        }
                    }
                }
            }
        }

        if (!outputItems.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String item : outputItems) {
                if (!first)
                    sb.append(isMST ? ", " : " \u2192 ");
                sb.append(item);
                first = false;
            }
            String outText = sb.toString();
            traversalLabel.setText(outText);
            if (outputLabel != null) {
                outputLabel.setText(outText);
                outputLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            }
            pranjal();
            traversalBox.setVisible(true);
        } else {
            traversalLabel.setText("");
            if (outputLabel != null) {
                outputLabel.setText("Output will be shown here after execution");
                outputLabel.setStyle("-fx-text-fill: #555e6b;");
            }
            traversalBox.setVisible(false);
        }
    }

    private void onAnimationComplete() {
        btnPlayPause.setText("\u25B6 Play");

        double runtimeMs = algorithmRuntimeNanos / 1_000_000.0;
        runtimeLabel.setText("Total: " + formatRuntime(runtimeMs));
        runtimeSection.setVisible(true);
        runtimeSection.setManaged(true);
    }

    private void buildTraversalOverlay() {
        traversalLabel = new Label("");
        traversalLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 13));
        traversalLabel.setStyle("-fx-text-fill: #ecf0f1;");
        traversalLabel.setWrapText(true);
        traversalLabel.setMaxWidth(380);

        Label traversalTitle = new Label("\uD83D\uDCCC Traversal Order");
        traversalTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        traversalTitle.setStyle("-fx-text-fill: #f39c12;");

        traversalBox = new VBox(4, traversalTitle, traversalLabel);
        traversalBox.setPadding(new Insets(8, 12, 8, 12));
        traversalBox.setStyle(
                "-fx-background-color: rgba(44, 62, 80, 0.92);" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 2);");
        traversalBox.setMaxWidth(400);
        traversalBox.setVisible(false);
    }

    private void buildComparisonLayout(BorderPane root, HBox header) {
        leftAlgorithm = null;
        rightAlgorithm = null;
        leftAlgoButtons.clear();
        rightAlgoButtons.clear();
        leftComplete = false;
        rightComplete = false;

        int maxNode = graph.getVertices().size() - 1;
        if (compSourceNode > maxNode)
            compSourceNode = 0;
        if (compDestinationNode > maxNode || compDestinationNode < 0)
            compDestinationNode = Math.min(3, maxNode);

        double compW = 480;
        double compH = 340;

        leftVisualData = new GraphVisualData();
        leftVisualData.buildFromGraph(graph, compW / 2, compH / 2, 140);
        leftCanvas = new Pane();
        leftCanvas.setPrefSize(compW, compH);
        leftCanvas.setStyle("-fx-background-color: #1a2332; -fx-background-radius: 8;");
        leftRenderer = new GraphRenderer(leftCanvas);
        leftRenderer.setSelectedSourceNode(compSourceNode);
        leftRenderer.setDestinationNode(compDestinationNode);
        leftRenderer.render(leftVisualData);
        leftAnimator = new StepAnimator(leftRenderer, BASE_STEP_DELAY / currentSpeedMultiplier);
        leftAnimator.setVisualData(leftVisualData);
        leftAnimator.setOnStepChange(() -> updateCompStepDisplay("left"));
        leftAnimator.setOnComplete(() -> onCompAnimationComplete("left"));

        rightVisualData = new GraphVisualData();
        rightVisualData.buildFromGraph(graph, compW / 2, compH / 2, 140);
        rightCanvas = new Pane();
        rightCanvas.setPrefSize(compW, compH);
        rightCanvas.setStyle("-fx-background-color: #1a2332; -fx-background-radius: 8;");
        rightRenderer = new GraphRenderer(rightCanvas);
        rightRenderer.setSelectedSourceNode(compSourceNode);
        rightRenderer.setDestinationNode(compDestinationNode);
        rightRenderer.render(rightVisualData);
        rightAnimator = new StepAnimator(rightRenderer, BASE_STEP_DELAY / currentSpeedMultiplier);
        rightAnimator.setVisualData(rightVisualData);
        rightAnimator.setOnStepChange(() -> updateCompStepDisplay("right"));
        rightAnimator.setOnComplete(() -> onCompAnimationComplete("right"));

        VBox leftPane = buildSidePane("left");
        VBox rightPane = buildSidePane("right");

        Region divider = new Region();
        divider.setPrefWidth(2);
        divider.setMinWidth(2);
        divider.setMaxWidth(2);
        divider.setStyle("-fx-background-color: linear-gradient(to bottom, #3d566e, #1a2332, #3d566e);");

        HBox splitCenter = new HBox(0, leftPane, divider, rightPane);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        splitCenter.setStyle("-fx-background-color: #0f1923;");

        ScrollPane scrollWrapper = new ScrollPane(splitCenter);
        scrollWrapper.setFitToWidth(true);
        scrollWrapper.setFitToHeight(true);
        scrollWrapper.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollWrapper.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollWrapper.setStyle("-fx-background: #0f1923; -fx-background-color: #0f1923;");

        VBox bottomPanel = buildCompBottomPanel();

        pranjal();

        header.setStyle("-fx-background-color: linear-gradient(to right, #1a1a2e, #16213e, #1a1a2e);");

        root.setTop(header);
        root.setCenter(scrollWrapper);
        root.setRight(null);
        root.setBottom(bottomPanel);
    }

    private VBox buildSidePane(String side) {
        boolean isLeft = side.equals("left");

        Label sideTitle = new Label(isLeft ? "Algorithm A" : "Algorithm B");
        sideTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        sideTitle.setStyle("-fx-text-fill: white;");
        HBox titleRow = new HBox(sideTitle);
        titleRow.setAlignment(Pos.CENTER);
        titleRow.setPadding(new Insets(6, 0, 2, 0));

        HBox algoRow = buildCompAlgoRow(side);

        List<Integer> nodeIds = new ArrayList<>(graph.getVertices());
        java.util.Collections.sort(nodeIds);

        Label srcLbl = new Label("Source Node:");
        srcLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        srcLbl.setStyle("-fx-text-fill: #bdc3c7;");
        ComboBox<Integer> srcCombo = styledComboBox(nodeIds, compSourceNode);
        srcCombo.setOnAction(e -> {
            if (srcCombo.getValue() != null) {
                compSourceNode = srcCombo.getValue();
                if (leftRenderer != null) {
                    leftRenderer.setSelectedSourceNode(compSourceNode);
                    leftRenderer.render(leftVisualData);
                }
                if (rightRenderer != null) {
                    rightRenderer.setSelectedSourceNode(compSourceNode);
                    rightRenderer.render(rightVisualData);
                }
                if (isLeft && rightSourceCombo != null)
                    rightSourceCombo.setValue(compSourceNode);
                if (!isLeft && leftSourceCombo != null)
                    leftSourceCombo.setValue(compSourceNode);
            }
        });

        Label dstLbl = new Label("Destination Node:");
        dstLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        dstLbl.setStyle("-fx-text-fill: #bdc3c7;");
        ComboBox<Integer> dstCombo = styledComboBox(nodeIds, compDestinationNode);
        dstCombo.setOnAction(e -> {
            if (dstCombo.getValue() != null) {
                compDestinationNode = dstCombo.getValue();
                if (leftRenderer != null) {
                    leftRenderer.setDestinationNode(compDestinationNode);
                    leftRenderer.render(leftVisualData);
                }
                if (rightRenderer != null) {
                    rightRenderer.setDestinationNode(compDestinationNode);
                    rightRenderer.render(rightVisualData);
                }
                if (isLeft && rightDestCombo != null)
                    rightDestCombo.setValue(compDestinationNode);
                if (!isLeft && leftDestCombo != null)
                    leftDestCombo.setValue(compDestinationNode);
            }
        });

        if (isLeft) {
            leftSourceCombo = srcCombo;
            leftDestCombo = dstCombo;
        } else {
            rightSourceCombo = srcCombo;
            rightDestCombo = dstCombo;
        }

        HBox nodeSelectionRow = new HBox(8, srcLbl, srcCombo, dstLbl, dstCombo);
        nodeSelectionRow.setAlignment(Pos.CENTER);
        nodeSelectionRow.setPadding(new Insets(2, 6, 4, 6));

        Pane sideCanvas = isLeft ? leftCanvas : rightCanvas;
        buildCompTraversalOverlay(side);
        VBox travBox = isLeft ? leftTraversalBox : rightTraversalBox;

        Label nodeInfoLbl = new Label("Nodes: " + graph.getVertices().size() + "\nEdges: " + graph.getEdgeCount());
        nodeInfoLbl.setFont(Font.font("Consolas", 11));
        nodeInfoLbl.setStyle(
                "-fx-text-fill: #bdc3c7; -fx-background-color: rgba(15,25,35,0.85); -fx-padding: 4 8; -fx-background-radius: 6;");
        if (isLeft)
            leftNodeInfoLabel = nodeInfoLbl;
        else
            rightNodeInfoLabel = nodeInfoLbl;
        nodeInfoLbl.setLayoutX(10);
        nodeInfoLbl.setLayoutY(10);

        Pane canvasContainer = new Pane();
        canvasContainer.setPrefSize(480, 340);
        canvasContainer.setStyle(
                "-fx-background-color: #1a2332; -fx-background-radius: 8; -fx-border-color: #2a3a4e; -fx-border-radius: 8; -fx-border-width: 1;");
        canvasContainer.getChildren().addAll(sideCanvas, nodeInfoLbl, travBox);

        travBox.layoutXProperty().bind(
                canvasContainer.widthProperty().subtract(travBox.widthProperty()).subtract(10));
        travBox.layoutYProperty().bind(
                canvasContainer.heightProperty().subtract(travBox.heightProperty()).subtract(10));

        Label stepLbl = new Label("Step 0 / 0");
        stepLbl.setFont(Font.font("Consolas", FontWeight.BOLD, 13));
        stepLbl.setStyle("-fx-text-fill: #8e9aaf;");

        Label runtimeLbl = new Label("");
        runtimeLbl.setFont(Font.font("Consolas", FontWeight.BOLD, 13));
        runtimeLbl.setStyle("-fx-text-fill: #e74c3c;");
        runtimeLbl.setVisible(false);

        if (isLeft) {
            leftStepLabel = stepLbl;
            leftRuntimeLabel = runtimeLbl;
        } else {
            rightStepLabel = stepLbl;
            rightRuntimeLabel = runtimeLbl;
        }

        Region statsSpacer = new Region();
        HBox.setHgrow(statsSpacer, Priority.ALWAYS);
        HBox statsBar = new HBox(10, stepLbl, statsSpacer, runtimeLbl);
        statsBar.setAlignment(Pos.CENTER_LEFT);
        statsBar.setPadding(new Insets(4, 12, 2, 12));

        Label stepDesc = new Label("Select an algorithm.");
        stepDesc.setFont(Font.font("Arial", 11));
        stepDesc.setStyle("-fx-text-fill: #7f8c8d;");
        stepDesc.setWrapText(true);
        stepDesc.setMaxWidth(460);
        stepDesc.setVisible(false);
        stepDesc.setManaged(false);
        if (isLeft)
            leftStepDescLabel = stepDesc;
        else
            rightStepDescLabel = stepDesc;

        Label pseudoTitle = new Label("Pseudo Code");
        pseudoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        pseudoTitle.setStyle("-fx-text-fill: white;");
        HBox pseudoTitleRow = new HBox(pseudoTitle);
        pseudoTitleRow.setPadding(new Insets(4, 10, 2, 10));

        VBox pseudoBox = new VBox(2);
        pseudoBox.setPadding(new Insets(4, 10, 8, 10));
        pseudoBox.setStyle("-fx-background-color: #0f1923; -fx-background-radius: 6;");
        pseudoBox.setMinHeight(140);
        pseudoBox.setPrefHeight(160);
        if (isLeft)
            leftPseudocodeBox = pseudoBox;
        else
            rightPseudocodeBox = pseudoBox;

        Label pseudoPlaceholder = new Label("  Select an algorithm to view pseudo code.");
        pseudoPlaceholder.setFont(Font.font("Consolas", 12));
        pseudoPlaceholder.setStyle("-fx-text-fill: #555e6b;");
        pseudoBox.getChildren().add(pseudoPlaceholder);

        VBox pseudoSection = new VBox(0, pseudoTitleRow, pseudoBox);
        pseudoSection.setStyle(
                "-fx-background-color: #141e2b; -fx-background-radius: 8; -fx-border-color: #2a3a4e; -fx-border-radius: 8; -fx-border-width: 1;");
        pseudoSection.setPadding(new Insets(6, 4, 6, 4));

        VBox pane = new VBox(4, titleRow, algoRow, nodeSelectionRow, canvasContainer, statsBar, pseudoSection);
        pane.setPadding(new Insets(6, 8, 6, 8));
        pane.setStyle("-fx-background-color: linear-gradient(to bottom, #141e2b, #0f1923);");
        pane.setAlignment(Pos.TOP_CENTER);

        return pane;
    }

    private HBox buildCompAlgoRow(String side) {
        boolean isLeft = side.equals("left");
        List<Button> buttons = isLeft ? leftAlgoButtons : rightAlgoButtons;
        buttons.clear();

        Button btnBFS = algoToggleButton("BFS");
        Button btnDFS = algoToggleButton("DFS");
        Button btnDijkstra = algoToggleButton("Dijkstra");
        Button btnKruskalComp = algoToggleButton("Kruskal");
        Button btnPrimsComp = algoToggleButton("Prim's");
        Button btnBellmanFordComp = algoToggleButton("B-Ford");
        Button btnFloydWComp = algoToggleButton("Floyd-W");
        Button btnTopoComp = algoToggleButton("Topo");
        Button btnTSPComp = algoToggleButton("TSP");

        buttons.add(btnBFS);
        buttons.add(btnDFS);
        buttons.add(btnDijkstra);
        buttons.add(btnKruskalComp);
        buttons.add(btnPrimsComp);
        buttons.add(btnBellmanFordComp);
        buttons.add(btnFloydWComp);
        buttons.add(btnTopoComp);
        buttons.add(btnTSPComp);

        btnBFS.setOnAction(e -> selectCompAlgorithm(side, "BFS", btnBFS));
        btnDFS.setOnAction(e -> selectCompAlgorithm(side, "DFS", btnDFS));
        btnDijkstra.setOnAction(e -> selectCompAlgorithm(side, "Dijkstra", btnDijkstra));
        btnKruskalComp.setOnAction(e -> selectCompAlgorithm(side, "Kruskal", btnKruskalComp));
        btnPrimsComp.setOnAction(e -> selectCompAlgorithm(side, "Prim's", btnPrimsComp));
        pranjal();
        btnBellmanFordComp.setOnAction(e -> selectCompAlgorithm(side, "Bellman-Ford", btnBellmanFordComp));
        btnFloydWComp.setOnAction(e -> selectCompAlgorithm(side, "Floyd-Warshall", btnFloydWComp));
        btnTopoComp.setOnAction(e -> selectCompAlgorithm(side, "Topo Sort", btnTopoComp));
        btnTSPComp.setOnAction(e -> selectCompAlgorithm(side, "TSP", btnTSPComp));

        if (isDirectedGraph) {
            btnKruskalComp.setDisable(true);
            btnPrimsComp.setDisable(true);
            btnKruskalComp.setOpacity(0.4);
            btnPrimsComp.setOpacity(0.4);
        } else {
            btnTopoComp.setDisable(true);
            btnTopoComp.setOpacity(0.4);
        }

        HBox row = new HBox(4, btnBFS, btnDFS, btnDijkstra, btnKruskalComp, btnPrimsComp,
                btnBellmanFordComp, btnFloydWComp, btnTopoComp, btnTSPComp);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(4, 0, 4, 0));
        return row;
    }

    private void selectCompAlgorithm(String side, String name, Button selected) {
        boolean isLeft = side.equals("left");
        List<Button> buttons = isLeft ? leftAlgoButtons : rightAlgoButtons;

        if (isLeft) {
            leftAlgorithm = name;
            leftAnimator.stop();
            leftAnimator.load(List.of());
            leftRenderer.render(leftVisualData);
            leftStepLabel.setText("Step 0 / 0");
            leftRuntimeLabel.setVisible(false);
            leftTraversalOrderSet.clear();
            leftTraversalLabel.setText("");
            leftTraversalBox.setVisible(false);
            leftComplete = false;
        } else {
            rightAlgorithm = name;
            rightAnimator.stop();
            rightAnimator.load(List.of());
            rightRenderer.render(rightVisualData);
            rightStepLabel.setText("Step 0 / 0");
            rightRuntimeLabel.setVisible(false);
            rightTraversalOrderSet.clear();
            rightTraversalLabel.setText("");
            rightTraversalBox.setVisible(false);
            rightComplete = false;
        }

        for (Button btn : buttons) {
            applyAlgoButtonStyle(btn, false);
        }
        applyAlgoButtonStyle(selected, true);

        updateCompPseudocode(side, name, null);
    }

    private void updateCompPseudocode(String side, String algorithm, step.StepType currentStepType) {
        boolean isLeft = side.equals("left");
        VBox pseudoBox = isLeft ? leftPseudocodeBox : rightPseudocodeBox;
        if (pseudoBox == null)
            return;

        pseudoBox.getChildren().clear();

        AlgorithmInfo info = AlgorithmInfo.get(algorithm);
        if (info == null) {
            Label placeholder = new Label("  Select an algorithm to view pseudo code.");
            placeholder.setFont(Font.font("Consolas", 12));
            placeholder.setStyle("-fx-text-fill: #555e6b;");
            pseudoBox.getChildren().add(placeholder);
            return;
        }

        String[] lines = info.getPseudocode();
        int highlightLine = currentStepType != null ? info.getHighlightLine(currentStepType) : -1;

        for (int i = 0; i < lines.length; i++) {
            Label lineNum = new Label(String.format("%2d", i + 1));
            lineNum.setFont(Font.font("Consolas", 12));
            lineNum.setMinWidth(24);

            Label lineText = new Label("  " + lines[i]);
            lineText.setFont(Font.font("Consolas", 12));
            lineText.setWrapText(true);

            HBox lineRow = new HBox(4, lineNum, lineText);
            lineRow.setPadding(new Insets(1, 4, 1, 4));

            if (i == highlightLine) {
                lineNum.setStyle("-fx-text-fill: #00e5ff;");
                lineText.setStyle("-fx-text-fill: #00e5ff; -fx-font-weight: bold;");
                lineRow.setStyle("-fx-background-color: rgba(0,229,255,0.12); -fx-background-radius: 4;");

                Label arrow = new Label("\u2192");
                arrow.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
                arrow.setStyle("-fx-text-fill: #00e5ff;");
                lineRow.getChildren().add(0, arrow);
            } else {
                lineNum.setStyle("-fx-text-fill: #555e6b;");
                lineText.setStyle("-fx-text-fill: #bdc3c7;");
            }

            pseudoBox.getChildren().add(lineRow);
        }
    }

    private void runCompAlgorithm(String side) {
        boolean isLeft = side.equals("left");
        String algo = isLeft ? leftAlgorithm : rightAlgorithm;
        if (algo == null)
            return;

        int src = compSourceNode;
        int dst = compDestinationNode;

        List<Step> steps;
        long startTime = System.nanoTime();

        switch (algo) {
            case "BFS":
                steps = BFS.run(graph, src, dst);
                break;
            case "DFS":
                steps = DFS.run(graph, src, dst);
                break;
            case "Dijkstra":
                steps = Dijkstra.run(graph, src, dst);
                break;
            case "Kruskal":
                steps = Kruskal.run(graph);
                break;
            case "Prim's":
                steps = Prims.run(graph, src);
                break;
            case "Bellman-Ford":
                steps = BellmanFord.run(graph, src);
                break;
            case "Floyd-Warshall":
                steps = FloydWarshall.run(graph);
                break;
            case "Topo Sort":
                steps = TopologicalSort.run(graph);
                break;
            case "TSP":
                steps = TSP.run(graph, src);
                break;
            default:
                return;
        }

        long endTime = System.nanoTime();
        pranjal();
        long elapsed = endTime - startTime;

        if (isLeft) {
            leftRuntimeNanos = elapsed;
            leftRuntimeLabel.setVisible(false);
            leftAnimator.load(steps);
        } else {
            rightRuntimeNanos = elapsed;
            rightRuntimeLabel.setVisible(false);
            rightAnimator.load(steps);
        }
    }

    private VBox buildCompBottomPanel() {

        Button btnBack = compStyledButton("\u23EE Prev", "#6c3483", "#8e44ad");
        btnPlayPause = compStyledButton("\u25B6 Play", "#1e8449", "#27ae60");
        Button btnForward = compStyledButton("\u23ED Next", "#6c3483", "#8e44ad");
        Button btnReset = compStyledButton("\uD83D\uDD04 Reset", "#555e6b", "#7f8c8d");

        btnBack.setOnAction(e -> compStepBackward());
        btnForward.setOnAction(e -> compStepForward());
        btnPlayPause.setOnAction(e -> compTogglePlayPause());
        btnReset.setOnAction(e -> compResetAll());

        Label speedText = new Label("Animation Speed");
        speedText.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        speedText.setStyle("-fx-text-fill: #8e9aaf;");

        speedSlider = new Slider(0.25, 4.0, currentSpeedMultiplier);
        speedSlider.setPrefWidth(140);
        speedSlider.setBlockIncrement(0.25);

        speedLabel = new Label(String.format("%.1fx", currentSpeedMultiplier));
        speedLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 13));
        speedLabel.setStyle("-fx-text-fill: #00e5ff;");
        speedLabel.setMinWidth(40);

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentSpeedMultiplier = newVal.doubleValue();
            double newDelay = BASE_STEP_DELAY / currentSpeedMultiplier;
            speedLabel.setText(String.format("%.1fx", currentSpeedMultiplier));
            if (leftAnimator != null)
                leftAnimator.setStepDuration(newDelay);
            if (rightAnimator != null)
                rightAnimator.setStepDuration(newDelay);
        });

        HBox legend = new HBox(12);
        legend.setAlignment(Pos.CENTER);
        legend.getChildren().addAll(
                compLegendDot("#3498db", "Unvisited"),
                compLegendDot("#f39c12", "Visiting"),
                compLegendDot("#2ecc71", "Visited"),
                compLegendDot("#9b59b6", "Path"),
                compLegendDot("#e74c3c", "Current"));
        Label legendTitle = new Label("Legend:");
        legendTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        legendTitle.setStyle("-fx-text-fill: #8e9aaf;");

        HBox legendBar = new HBox(10, legendTitle, legend);
        legendBar.setAlignment(Pos.CENTER);
        legendBar.setPadding(new Insets(4, 10, 4, 10));
        legendBar.setStyle(
                "-fx-background-color: rgba(20,30,43,0.8); -fx-background-radius: 6; -fx-border-color: #2a3a4e; -fx-border-radius: 6;");

        Button fullscreenBtn = compStyledButton("\u2726 Fullscreen", "#2a3a4e", "#3d566e");
        fullscreenBtn.setOnAction(e -> {
            isFullscreen = !isFullscreen;
            primaryStage.setFullScreen(isFullscreen);
            fullscreenBtn.setText(isFullscreen ? "\u2726 Exit FS" : "\u2726 Fullscreen");
        });

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox playbackBar = new HBox(12, btnBack, btnPlayPause, btnForward, btnReset,
                spacer1, speedText, speedSlider, speedLabel,
                spacer2, legendBar, fullscreenBtn);
        playbackBar.setAlignment(Pos.CENTER);
        playbackBar.setPadding(new Insets(8, 16, 10, 16));

        VBox bottomPanel = new VBox(0, playbackBar);
        bottomPanel.setStyle(
                "-fx-background-color: linear-gradient(to right, #0f1923, #141e2b, #0f1923); -fx-border-color: #2a3a4e; -fx-border-width: 1 0 0 0;");
        bottomPanel.setPadding(new Insets(4, 12, 6, 12));

        return bottomPanel;
    }

    private Button compStyledButton(String text, String bg, String hoverBg) {
        String base = "-fx-background-color: " + bg + "; -fx-text-fill: #ecf0f1; "
                + "-fx-padding: 8 18; -fx-background-radius: 20; -fx-cursor: hand; "
                + "-fx-border-color: transparent; -fx-border-radius: 20; -fx-border-width: 1;";
        String hover = "-fx-background-color: " + hoverBg + "; -fx-text-fill: white; "
                + "-fx-padding: 8 18; -fx-background-radius: 20; -fx-cursor: hand; "
                + "-fx-border-color: " + hoverBg + "; -fx-border-radius: 20; -fx-border-width: 1; "
                + "-fx-effect: dropshadow(gaussian, " + hoverBg + ", 8, 0.3, 0, 0);";
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    private HBox compLegendDot(String color, String text) {
        Circle dot = new Circle(5, Color.web(color));
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 10));
        lbl.setStyle("-fx-text-fill: #8e9aaf;");
        HBox row = new HBox(4, dot, lbl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void compTogglePlayPause() {
        boolean eitherPlaying = (leftAnimator != null && leftAnimator.isPlaying())
                || (rightAnimator != null && rightAnimator.isPlaying());

        if (eitherPlaying) {
            if (leftAnimator != null)
                leftAnimator.pause();
            if (rightAnimator != null)
                rightAnimator.pause();
            btnPlayPause.setText("\u25B6 Play");
        } else {

            if (leftAnimator != null && leftAnimator.getTotalSteps() == 0 && leftAlgorithm != null) {
                runCompAlgorithm("left");
                leftComplete = false;
            }
            if (rightAnimator != null && rightAnimator.getTotalSteps() == 0 && rightAlgorithm != null) {
                runCompAlgorithm("right");
                rightComplete = false;
            }

            boolean anyStarted = false;
            if (leftAnimator != null && leftAnimator.getTotalSteps() > 0) {
                leftAnimator.play();
                anyStarted = true;
            }
            if (rightAnimator != null && rightAnimator.getTotalSteps() > 0) {
                rightAnimator.play();
                anyStarted = true;
            }

            pranjal();
            if (anyStarted) {
                btnPlayPause.setText("\u23F8 Pause");
            }
        }
    }

    private void compStepForward() {
        if (leftAnimator != null && leftAnimator.getTotalSteps() == 0 && leftAlgorithm != null) {
            runCompAlgorithm("left");
        }
        if (rightAnimator != null && rightAnimator.getTotalSteps() == 0 && rightAlgorithm != null) {
            runCompAlgorithm("right");
        }
        if (leftAnimator != null)
            leftAnimator.stepForward();
        if (rightAnimator != null)
            rightAnimator.stepForward();
    }

    private void compStepBackward() {
        if (leftAnimator != null)
            leftAnimator.stepBackward();
        if (rightAnimator != null)
            rightAnimator.stepBackward();
    }

    private void compResetAll() {
        if (leftAnimator != null) {
            leftAnimator.stop();
            leftAnimator.load(List.of());
        }
        if (rightAnimator != null) {
            rightAnimator.stop();
            rightAnimator.load(List.of());
        }
        if (leftRenderer != null)
            leftRenderer.render(leftVisualData);
        if (rightRenderer != null)
            rightRenderer.render(rightVisualData);

        btnPlayPause.setText("\u25B6 Play");
        leftComplete = false;
        rightComplete = false;
        leftRuntimeNanos = 0;
        rightRuntimeNanos = 0;

        if (leftStepLabel != null)
            leftStepLabel.setText("Step 0 / 0");
        if (rightStepLabel != null)
            rightStepLabel.setText("Step 0 / 0");
        if (leftRuntimeLabel != null)
            leftRuntimeLabel.setVisible(false);
        if (rightRuntimeLabel != null)
            rightRuntimeLabel.setVisible(false);

        leftTraversalOrderSet.clear();
        if (leftTraversalLabel != null)
            leftTraversalLabel.setText("");
        if (leftTraversalBox != null)
            leftTraversalBox.setVisible(false);

        rightTraversalOrderSet.clear();
        if (rightTraversalLabel != null)
            rightTraversalLabel.setText("");
        if (rightTraversalBox != null)
            rightTraversalBox.setVisible(false);

        if (leftAlgorithm != null && leftPseudocodeBox != null)
            updateCompPseudocode("left", leftAlgorithm, null);
        if (rightAlgorithm != null && rightPseudocodeBox != null)
            updateCompPseudocode("right", rightAlgorithm, null);
    }

    private void updateCompStepDisplay(String side) {
        boolean isLeft = side.equals("left");
        StepAnimator anim = isLeft ? leftAnimator : rightAnimator;
        Label stepLbl = isLeft ? leftStepLabel : rightStepLabel;
        String algo = isLeft ? leftAlgorithm : rightAlgorithm;

        int cur = anim.getCurrentIndex() + 1;
        int total = anim.getTotalSteps();
        stepLbl.setText("Step " + cur + " / " + total);

        Step currentStep = anim.getCurrentStep();
        if (currentStep != null && algo != null) {

            updateCompPseudocode(side, algo, currentStep.getType());
        }

        Label runtimeLbl = isLeft ? leftRuntimeLabel : rightRuntimeLabel;
        long nanos = isLeft ? leftRuntimeNanos : rightRuntimeNanos;
        if (cur > 0 && total > 0) {
            double progressFraction = (double) cur / total;
            double estimatedMs = (nanos / 1_000_000.0) * progressFraction;
            runtimeLbl.setText("Time: " + String.format("%.2fs", estimatedMs / 1000.0));
            runtimeLbl.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            runtimeLbl.setVisible(true);
        }

        rebuildCompTraversalDisplay(side);
    }

    private void onCompAnimationComplete(String side) {
        boolean isLeft = side.equals("left");

        if (isLeft) {
            leftComplete = true;
            double runtimeMs = leftRuntimeNanos / 1_000_000.0;
            leftRuntimeLabel.setText("Time: " + formatRuntime(runtimeMs));
            leftRuntimeLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            pranjal();
            leftRuntimeLabel.setVisible(true);
        } else {
            rightComplete = true;
            double runtimeMs = rightRuntimeNanos / 1_000_000.0;
            rightRuntimeLabel.setText("Time: " + formatRuntime(runtimeMs));
            rightRuntimeLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            rightRuntimeLabel.setVisible(true);
        }

        boolean leftDone = leftAlgorithm == null || leftComplete;
        boolean rightDone = rightAlgorithm == null || rightComplete;
        if (leftDone && rightDone) {
            btnPlayPause.setText("\u25B6 Play");
        }
    }

    private void buildCompTraversalOverlay(String side) {
        boolean isLeft = side.equals("left");

        Label travLabel = new Label("");
        travLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 13));
        travLabel.setStyle("-fx-text-fill: #ecf0f1;");
        travLabel.setWrapText(true);
        travLabel.setMaxWidth(440);

        Label travTitle = new Label("\uD83D\uDCCC Traversal");
        travTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        travTitle.setStyle("-fx-text-fill: #2ecc71;");

        Label costLabel = new Label("");
        costLabel.setFont(Font.font("Consolas", 11));
        costLabel.setStyle("-fx-text-fill: #8e9aaf;");

        VBox box = new VBox(3, travTitle, travLabel, costLabel);
        box.setPadding(new Insets(8, 12, 8, 12));
        box.setStyle(
                "-fx-background-color: rgba(15, 25, 35, 0.92);" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #2a3a4e; -fx-border-radius: 8; -fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,229,255,0.15), 10, 0, 0, 2);");
        box.setMaxWidth(460);
        box.setVisible(false);

        if (isLeft) {
            leftTraversalLabel = travLabel;
            leftTraversalBox = box;
        } else {
            rightTraversalLabel = travLabel;
            rightTraversalBox = box;
        }
    }

    private void rebuildCompTraversalDisplay(String side) {
        boolean isLeft = side.equals("left");
        StepAnimator anim = isLeft ? leftAnimator : rightAnimator;
        String algo = isLeft ? leftAlgorithm : rightAlgorithm;
        Set<Integer> orderSet = isLeft ? leftTraversalOrderSet : rightTraversalOrderSet;
        Label travLabel = isLeft ? leftTraversalLabel : rightTraversalLabel;
        VBox travBox = isLeft ? leftTraversalBox : rightTraversalBox;

        orderSet.clear();

        int currentIdx = anim.getCurrentIndex();
        if (currentIdx < 0) {
            travLabel.setText("");
            travBox.setVisible(false);
            return;
        }

        boolean isTraversalAlgo = "BFS".equals(algo) || "DFS".equals(algo) || "Dijkstra".equals(algo)
                || "Bellman-Ford".equals(algo) || "Topo Sort".equals(algo) || "TSP".equals(algo);
        if (!isTraversalAlgo) {
            travLabel.setText("");
            travBox.setVisible(false);
            return;
        }

        for (int i = 0; i <= currentIdx; i++) {
            Step s = anim.getStepAt(i);
            if (s != null && s.getType() == StepType.VISIT_NODE) {
                orderSet.add(s.getNode());
            }
        }

        if (!orderSet.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (int node : orderSet) {
                if (!first)
                    sb.append(" \u2192 ");
                sb.append(node);
                first = false;
            }
            travLabel.setText(sb.toString());
            travBox.setVisible(true);
        } else {
            travLabel.setText("");
            travBox.setVisible(false);
        }
    }

    private String formatRuntime(double runtimeMs) {
        pranjal();
        if (runtimeMs < 1.0) {
            return String.format("%.3f ms", runtimeMs);
        } else {
            return String.format("%.2f ms", runtimeMs);
        }
    }

    private Label sectionHeading(String text) {
        Label heading = new Label(text);
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        heading.setStyle("-fx-text-fill: white;");
        return heading;
    }

    private Label subHeading(String text) {
        Label heading = new Label(text);
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        heading.setStyle("-fx-text-fill: #bdc3c7;");
        heading.setPadding(new Insets(6, 0, 0, 0));
        return heading;
    }

    private Separator styledSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #3d566e; -fx-padding: 0;");
        return sep;
    }

    private HBox legendItem(Color color, String text) {
        Circle dot = new Circle(7, color);
        dot.setStroke(Color.web("#2c3e50"));
        dot.setStrokeWidth(1.5);
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 12));
        lbl.setStyle("-fx-text-fill: #ecf0f1;");
        HBox row = new HBox(8, dot, lbl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox legendRing(Color color, String text) {
        Circle ring = new Circle(7);
        ring.setFill(Color.TRANSPARENT);
        ring.setStroke(color);
        ring.setStrokeWidth(2.5);
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 12));
        lbl.setStyle("-fx-text-fill: #ecf0f1;");
        HBox row = new HBox(8, ring, lbl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox legendLine(Color color, String text) {
        Line line = new Line(0, 0, 20, 0);
        line.setStroke(color);
        line.setStrokeWidth(3);
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 12));
        lbl.setStyle("-fx-text-fill: #ecf0f1;");
        HBox row = new HBox(8, line, lbl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Button algoToggleButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        applyAlgoButtonStyle(btn, false);
        return btn;
    }

    private void applyAlgoButtonStyle(Button btn, boolean selected) {
        String bg = selected ? "#3498db" : "#2a3a4e";
        String hoverBg = selected ? "#2980b9" : "#3d566e";
        String border = selected ? "#00e5ff" : "transparent";
        String glowEffect = selected ? "-fx-effect: dropshadow(gaussian, #3498db, 10, 0.4, 0, 0);" : "";

        String base = "-fx-background-color: " + bg + "; -fx-text-fill: white; "
                + "-fx-padding: 6 14; -fx-background-radius: 20; -fx-cursor: hand; "
                + "-fx-border-color: " + border + "; -fx-border-radius: 20; -fx-border-width: 1.5; "
                + glowEffect;
        String hover = "-fx-background-color: " + hoverBg + "; -fx-text-fill: white; "
                + "-fx-padding: 6 14; -fx-background-radius: 20; -fx-cursor: hand; "
                + "-fx-border-color: " + border + "; -fx-border-radius: 20; -fx-border-width: 1.5; "
                + "-fx-effect: dropshadow(gaussian, " + hoverBg + ", 8, 0.3, 0, 0);";

        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
    }

    private Button styledButton(String text, String bg, String hoverBg) {
        String base = "-fx-background-color: " + bg + "; -fx-text-fill: white; "
                + "-fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;";
        String hover = "-fx-background-color: " + hoverBg + "; -fx-text-fill: white; "
                + "-fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;";
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    private ComboBox<Integer> styledComboBox(List<Integer> items, int initialValue) {
        ComboBox<Integer> combo = new ComboBox<>();
        combo.getItems().addAll(items);
        combo.setValue(initialValue);
        combo.setPrefWidth(70);
        combo.setStyle(
                "-fx-background-color: #2a3a4e; -fx-background-radius: 6; -fx-border-color: #3d566e; -fx-border-radius: 6;");

        combo.setButtonCell(new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(String.valueOf(item));
                }
                setStyle(
                        "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-color: transparent;");
            }
        });

        combo.setCellFactory(lv -> new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(String.valueOf(item));
                }
                setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-background-color: #2a3a4e;");
                setOnMouseEntered(
                        e -> setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-background-color: #3d566e;"));
                setOnMouseExited(
                        e -> setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-background-color: #2a3a4e;"));
            }
        });

        return combo;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
