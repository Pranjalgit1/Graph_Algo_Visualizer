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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainApp extends Application {

    private static final double CANVAS_WIDTH = 750;
    private static final double CANVAS_HEIGHT = 550;
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
    private VBox traversalBox;
    private final Set<Integer> traversalOrderSet = new LinkedHashSet<>();
    private Button btnKruskal;
    private Button btnPrims;
    private Button btnTopoSort;

    // Feature #2: Source node
    private int sourceNode = 0;
    private Label sourceLabel;

    // Feature #4: Progress bar
    private ProgressBar progressBar;

    // Feature #6: Algorithm info panel
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

        // Feature #1: Keyboard shortcuts
        scene.setOnKeyPressed(e -> {
            if (comparisonMode) {
                switch (e.getCode()) {
                    case SPACE: compTogglePlayPause(); break;
                    case RIGHT: compStepForward(); break;
                    case LEFT: compStepBackward(); break;
                    case R: compResetAll(); break;
                    default: break;
                }
            } else {
                switch (e.getCode()) {
                    case SPACE: togglePlayPause(); break;
                    case RIGHT: if (animator != null) animator.stepForward(); break;
                    case LEFT: if (animator != null) animator.stepBackward(); break;
                    case R: resetAll(); break;
                    case DIGIT1: case NUMPAD1: selectAlgoByIndex(0); break;
                    case DIGIT2: case NUMPAD2: selectAlgoByIndex(1); break;
                    case DIGIT3: case NUMPAD3: selectAlgoByIndex(2); break;
                    case DIGIT4: case NUMPAD4: selectAlgoByIndex(3); break;
                    case DIGIT5: case NUMPAD5: selectAlgoByIndex(4); break;
                    case DIGIT6: case NUMPAD6: selectAlgoByIndex(5); break;
                    case DIGIT7: case NUMPAD7: selectAlgoByIndex(6); break;
                    case DIGIT8: case NUMPAD8: selectAlgoByIndex(7); break;
                    case DIGIT9: case NUMPAD9: selectAlgoByIndex(8); break;
                    default: break;
                }
            }
            e.consume();
        });

        primaryStage.setTitle("Graph Algorithm Visualizer");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
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

        String compBtnText = comparisonMode ? "\u2726 Single Mode" : "\u2694 Compare Mode";
        String compBg = comparisonMode ? "#27ae60" : "#e67e22";
        String compHover = comparisonMode ? "#1e8449" : "#d35400";
        Button compToggle = styledButton(compBtnText, compBg, compHover);
        compToggle.setOnAction(e -> {
            comparisonMode = !comparisonMode;
            showVisualization(graph, isDirectedGraph);
        });
        compToggle.setVisible(false);
        compToggle.setManaged(false); // so it doesn't take up space

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        HBox header = new HBox(10, newGraphBtn, leftSpacer, title, rightSpacer, compToggle);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(14));
        header.setStyle("-fx-background-color: #34495e;");

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
        visualData.buildFromGraph(graph, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2, 190);

        canvas = new Pane();
        canvas.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        canvas.setStyle("-fx-background-color: #ecf0f1;");

        renderer = new GraphRenderer(canvas);
        renderer.setSelectedSourceNode(sourceNode);
        renderer.setOnSourceNodeChanged(() -> {
            sourceNode = renderer.getSelectedSourceNode();
            if (sourceLabel != null) {
                sourceLabel.setText("Source: Node " + sourceNode);
            }
        });
        renderer.render(visualData);

        buildTraversalOverlay();

        animator = new StepAnimator(renderer, BASE_STEP_DELAY / currentSpeedMultiplier);
        animator.setVisualData(visualData);
        animator.setOnStepChange(this::updateStepDisplay);
        animator.setOnComplete(this::onAnimationComplete);

        VBox sidebar = buildSidebar();

        VBox bottomPanel = buildBottomPanel();

        Pane canvasContainer = new Pane();
        canvasContainer.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        canvasContainer.getChildren().addAll(canvas, traversalBox);

        traversalBox.layoutXProperty().bind(
                canvasContainer.widthProperty().subtract(traversalBox.widthProperty()).subtract(10));
        traversalBox.layoutYProperty().bind(
                canvasContainer.heightProperty().subtract(traversalBox.heightProperty()).subtract(10));

        root.setTop(header);
        root.setCenter(canvasContainer);
        root.setRight(sidebar);
        root.setBottom(bottomPanel);
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: #2c3e50;");
        pranjal();
        VBox legendSection = buildLegendSection();

        Separator sep1 = styledSeparator();

        // Feature #2: Source node display
        VBox sourceSection = buildSourceSection();

        Separator sep1b = styledSeparator();

        VBox stepSection = buildStepSection();

        Separator sep2 = styledSeparator();

        runtimeSection = buildRuntimeSection();

        Separator sep3 = styledSeparator();

        // Feature #6: Algorithm info panel
        algoInfoPanel = buildAlgoInfoPanel();

        ScrollPane sidebarScroll = new ScrollPane(new VBox(0, legendSection, sep1, sourceSection, sep1b, stepSection, sep2, runtimeSection, sep3, algoInfoPanel));
        sidebarScroll.setFitToWidth(true);
        sidebarScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sidebarScroll.setStyle("-fx-background: #2c3e50; -fx-background-color: #2c3e50;");
        sidebarScroll.setPrefWidth(240);

        sidebar.getChildren().add(sidebarScroll);
        VBox.setVgrow(sidebarScroll, Priority.ALWAYS);
        return sidebar;
    }

    private VBox buildLegendSection() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(16, 14, 12, 14));

        Label heading = sectionHeading("Legend");
        box.getChildren().add(heading);

        Label nodeHeading = subHeading("Nodes");
        box.getChildren().add(nodeHeading);
        box.getChildren().add(legendItem(Color.web("#3498db"), "Default"));
        box.getChildren().add(legendItem(Color.web("#f39c12"), "In Queue"));
        box.getChildren().add(legendItem(Color.web("#e74c3c"), "Visiting"));
        box.getChildren().add(legendItem(Color.web("#2ecc71"), "Processed"));
        box.getChildren().add(legendItem(Color.web("#e91e63"), "Neg. Cycle"));
        box.getChildren().add(legendItem(Color.web("#1a237e"), "Topo Push"));

        Label edgeHeading = subHeading("Edges");
        box.getChildren().add(edgeHeading);
        box.getChildren().add(legendLine(Color.web("#95a5a6"), "Default"));
        box.getChildren().add(legendLine(Color.web("#e67e22"), "Exploring"));
        box.getChildren().add(legendLine(Color.web("#f1c40f"), "Considered"));
        box.getChildren().add(legendLine(Color.web("#27ae60"), "Selected (MST)"));
        box.getChildren().add(legendLine(Color.web("#c0392b"), "Rejected"));
        box.getChildren().add(legendLine(Color.web("#00bcd4"), "Relaxed"));
        box.getChildren().add(legendLine(Color.web("#9b59b6"), "Tour (TSP)"));

        return box;
    }

    private VBox buildSourceSection() {
        VBox box = new VBox(4);
        box.setPadding(new Insets(10, 14, 8, 14));

        Label heading = sectionHeading("\uD83C\uDFAF Source");
        sourceLabel = new Label("Source: Node " + sourceNode);
        sourceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        sourceLabel.setStyle("-fx-text-fill: #e74c3c;");

        Label hint = new Label("Click a node to change");
        hint.setFont(Font.font("Arial", 10));
        hint.setStyle("-fx-text-fill: #7f8c8d;");

        box.getChildren().addAll(heading, sourceLabel, hint);
        return box;
    }

    private VBox buildAlgoInfoPanel() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(12, 14, 12, 14));

        Label heading = sectionHeading("\uD83D\uDCCB Algorithm");
        algoNameLabel = new Label("No algorithm selected");
        algoNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        algoNameLabel.setStyle("-fx-text-fill: #f39c12;");
        algoNameLabel.setWrapText(true);
        algoNameLabel.setMaxWidth(210);

        algoComplexityLabel = new Label("");
        algoComplexityLabel.setFont(Font.font("Arial", 11));
        algoComplexityLabel.setStyle("-fx-text-fill: #bdc3c7;");
        algoComplexityLabel.setWrapText(true);
        algoComplexityLabel.setMaxWidth(210);

        pseudocodeBox = new VBox(1);
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
            line.setFont(Font.font("Consolas", 10));
            line.setMaxWidth(210);
            line.setWrapText(true);

            if (i == highlightLine) {
                line.setStyle("-fx-text-fill: #2ecc71; -fx-background-color: rgba(46,204,113,0.15); -fx-padding: 1 4;");
            } else {
                line.setStyle("-fx-text-fill: #95a5a6; -fx-padding: 1 4;");
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

        Button btnBFS = algoToggleButton("BFS");
        Button btnDFS = algoToggleButton("DFS");
        Button btnDijkstra = algoToggleButton("Dijkstra");
        btnKruskal = algoToggleButton("Kruskal");
        btnPrims = algoToggleButton("Prim's");
        Button btnBellmanFord = algoToggleButton("Bellman-Ford");
        Button btnFloydWarshall = algoToggleButton("Floyd-W");
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

        Label algoLabel = new Label("Algorithm:");
        algoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        algoLabel.setStyle("-fx-text-fill: #ecf0f1;");

        HBox algoBar = new HBox(8, algoLabel, btnBFS, btnDFS, btnDijkstra, btnKruskal, btnPrims,
                btnBellmanFord, btnFloydWarshall, btnTopoSort, btnTSP);
        algoBar.setAlignment(Pos.CENTER);
        algoBar.setPadding(new Insets(10, 0, 6, 0));

        Button btnBack = styledButton("\u23EE Prev", "#8e44ad", "#6c3483");
        btnPlayPause = styledButton("\u25B6 Play", "#27ae60", "#1e8449");
        Button btnForward = styledButton("\u23ED Next", "#8e44ad", "#6c3483");
        Button btnReset = styledButton("\uD83D\uDD04 Reset", "#7f8c8d", "#636e72");

        btnBack.setOnAction(e -> animator.stepBackward());
        btnForward.setOnAction(e -> animator.stepForward());
        btnPlayPause.setOnAction(e -> togglePlayPause());
        btnReset.setOnAction(e -> resetAll());

        stepLabel = new Label("Step: 0 / 0");
        stepLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        stepLabel.setStyle("-fx-text-fill: #ecf0f1;");

        Label speedText = new Label("\u26A1 Speed:");
        speedText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        speedText.setStyle("-fx-text-fill: #ecf0f1;");

        speedSlider = new Slider(0.25, 4.0, currentSpeedMultiplier);
        speedSlider.setPrefWidth(120);
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

        // Feature #4: Progress bar
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(100);
        progressBar.setPrefHeight(14);
        progressBar.setStyle("-fx-accent: #3498db;");

        HBox playbackBar = new HBox(10, btnBack, btnPlayPause, btnForward, btnReset,
                spacer1, stepLabel, progressBar, speedText, speedSlider, speedLabel);
        playbackBar.setAlignment(Pos.CENTER);
        playbackBar.setPadding(new Insets(6, 16, 10, 16));

        Separator sep = styledSeparator();

        VBox bottomPanel = new VBox(0, algoBar, sep, playbackBar);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-background-color: #2c3e50;");
        pranjal();
        bottomPanel.setPadding(new Insets(4, 12, 6, 12));

        return bottomPanel;
    }

    private void selectAlgorithm(String name, Button selected) {
        resetAll();
        selectedAlgorithm = name;

        for (Button btn : algoButtons) {
            applyAlgoButtonStyle(btn, false);
        }
        applyAlgoButtonStyle(selected, true);

        // Feature #6: Update algorithm info panel
        if (algoInfoPanel != null) {
            updateAlgoInfoPanel(name, null);
        }
    }

    // Feature #1: Select algorithm by keyboard number
    private void selectAlgoByIndex(int index) {
        if (index >= 0 && index < algoButtons.size()) {
            Button btn = algoButtons.get(index);
            if (!btn.isDisabled()) {
                String[] algoNames = {"BFS", "DFS", "Dijkstra", "Kruskal", "Prim's", "Bellman-Ford", "Floyd-Warshall", "Topo Sort", "TSP"};
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
                steps = BFS.run(graph, sourceNode);
                break;
            case "DFS":
                steps = DFS.run(graph, sourceNode);
                break;
            case "Dijkstra":
                steps = Dijkstra.run(graph, sourceNode);
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
        stepDescriptionLabel.setText("No algorithm running.");
        runtimeSection.setVisible(false);
        runtimeSection.setManaged(false);
        runtimeLabel.setText("\u2014");
        algorithmRuntimeNanos = 0;

        // Feature #4: Reset progress bar
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

        // Feature #4: Update progress bar
        if (progressBar != null && total > 0) {
            progressBar.setProgress((double) cur / total);
        }

        Step currentStep = animator.getCurrentStep();
        if (currentStep != null) {
            stepDescriptionLabel.setText(currentStep.toDescription());

            // Feature #6: Update pseudocode highlighting
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

        boolean isTraversalAlgo = "BFS".equals(selectedAlgorithm)
                || "DFS".equals(selectedAlgorithm)
                || "Dijkstra".equals(selectedAlgorithm)
                || "Bellman-Ford".equals(selectedAlgorithm)
                || "Topo Sort".equals(selectedAlgorithm)
                || "TSP".equals(selectedAlgorithm);

        if (!isTraversalAlgo) {
            traversalLabel.setText("");
            traversalBox.setVisible(false);
            return;
        }

        for (int i = 0; i <= currentIdx; i++) {
            Step s = animator.getStepAt(i);
            if (s != null && s.getType() == StepType.VISIT_NODE) {
                traversalOrderSet.add(s.getNode());
            }
        }

        if (!traversalOrderSet.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (int node : traversalOrderSet) {
                if (!first)
                    sb.append(" \u2192 ");
                sb.append(node);
                first = false;
            }
            traversalLabel.setText(sb.toString());
            pranjal();
            traversalBox.setVisible(true);
        } else {
            traversalLabel.setText("");
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

        leftVisualData = new GraphVisualData();
        leftVisualData.buildFromGraph(graph, COMP_CANVAS_W / 2, COMP_CANVAS_H / 2, 160);
        leftCanvas = new Pane();
        leftCanvas.setPrefSize(COMP_CANVAS_W, COMP_CANVAS_H);
        leftCanvas.setStyle("-fx-background-color: #ecf0f1;");
        leftRenderer = new GraphRenderer(leftCanvas);
        leftRenderer.render(leftVisualData);
        leftAnimator = new StepAnimator(leftRenderer, BASE_STEP_DELAY / currentSpeedMultiplier);
        leftAnimator.setVisualData(leftVisualData);
        leftAnimator.setOnStepChange(() -> updateCompStepDisplay("left"));
        leftAnimator.setOnComplete(() -> onCompAnimationComplete("left"));

        rightVisualData = new GraphVisualData();
        rightVisualData.buildFromGraph(graph, COMP_CANVAS_W / 2, COMP_CANVAS_H / 2, 160);
        rightCanvas = new Pane();
        rightCanvas.setPrefSize(COMP_CANVAS_W, COMP_CANVAS_H);
        rightCanvas.setStyle("-fx-background-color: #ecf0f1;");
        rightRenderer = new GraphRenderer(rightCanvas);
        rightRenderer.render(rightVisualData);
        rightAnimator = new StepAnimator(rightRenderer, BASE_STEP_DELAY / currentSpeedMultiplier);
        rightAnimator.setVisualData(rightVisualData);
        rightAnimator.setOnStepChange(() -> updateCompStepDisplay("right"));
        rightAnimator.setOnComplete(() -> onCompAnimationComplete("right"));

        VBox leftPane = buildSidePane("left");
        VBox rightPane = buildSidePane("right");

        Region divider = new Region();
        divider.setPrefWidth(3);
        divider.setMinWidth(3);
        divider.setMaxWidth(3);
        divider.setStyle("-fx-background-color: #3d566e;");

        HBox splitCenter = new HBox(0, leftPane, divider, rightPane);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        splitCenter.setStyle("-fx-background-color: #2c3e50;");

        VBox bottomPanel = buildCompBottomPanel();

        pranjal();
        root.setTop(header);
        root.setCenter(splitCenter);
        root.setRight(null);
        root.setBottom(bottomPanel);
    }

    private VBox buildSidePane(String side) {
        boolean isLeft = side.equals("left");

        Label sideTitle = new Label(isLeft ? "\u25C0 Algorithm A" : "\u25B6 Algorithm B");
        sideTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        sideTitle.setStyle("-fx-text-fill: white;");

        HBox algoRow = buildCompAlgoRow(side);

        Label stepDesc = new Label("Select an algorithm.");
        stepDesc.setFont(Font.font("Arial", 11));
        stepDesc.setStyle("-fx-text-fill: #bdc3c7;");
        stepDesc.setWrapText(true);
        stepDesc.setMaxWidth(COMP_CANVAS_W - 20);

        if (isLeft)
            leftStepDescLabel = stepDesc;
        else
            rightStepDescLabel = stepDesc;

        Pane sideCanvas = isLeft ? leftCanvas : rightCanvas;
        buildCompTraversalOverlay(side);
        VBox travBox = isLeft ? leftTraversalBox : rightTraversalBox;

        Pane canvasContainer = new Pane();
        canvasContainer.setPrefSize(COMP_CANVAS_W, COMP_CANVAS_H);
        canvasContainer.getChildren().addAll(sideCanvas, travBox);

        travBox.layoutXProperty().bind(
                canvasContainer.widthProperty().subtract(travBox.widthProperty()).subtract(10));
        travBox.layoutYProperty().bind(
                canvasContainer.heightProperty().subtract(travBox.heightProperty()).subtract(10));

        Label stepLbl = new Label("Step: 0 / 0");
        stepLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        stepLbl.setStyle("-fx-text-fill: #ecf0f1;");

        Label runtimeLbl = new Label("");
        runtimeLbl.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        runtimeLbl.setStyle("-fx-text-fill: #2ecc71;");
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
        statsBar.setPadding(new Insets(4, 10, 4, 10));

        VBox pane = new VBox(4, sideTitle, algoRow, stepDesc, canvasContainer, statsBar);
        pane.setPadding(new Insets(8, 6, 4, 6));
        pane.setStyle("-fx-background-color: #2c3e50;");
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
            leftStepLabel.setText("Step: 0 / 0");
            leftStepDescLabel.setText("Selected: " + name);
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
            rightStepLabel.setText("Step: 0 / 0");
            rightStepDescLabel.setText("Selected: " + name);
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
    }

    private void runCompAlgorithm(String side) {
        boolean isLeft = side.equals("left");
        String algo = isLeft ? leftAlgorithm : rightAlgorithm;
        if (algo == null)
            return;

        List<Step> steps;
        long startTime = System.nanoTime();

        switch (algo) {
            case "BFS":
                steps = BFS.run(graph, 0);
                break;
            case "DFS":
                steps = DFS.run(graph, 0);
                break;
            case "Dijkstra":
                steps = Dijkstra.run(graph, 0);
                break;
            case "Kruskal":
                steps = Kruskal.run(graph);
                break;
            case "Prim's":
                steps = Prims.run(graph, 0);
                break;
            case "Bellman-Ford":
                steps = BellmanFord.run(graph, 0);
                break;
            case "Floyd-Warshall":
                steps = FloydWarshall.run(graph);
                break;
            case "Topo Sort":
                steps = TopologicalSort.run(graph);
                break;
            case "TSP":
                steps = TSP.run(graph, 0);
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

        Button btnBack = styledButton("\u23EE Prev", "#8e44ad", "#6c3483");
        btnPlayPause = styledButton("\u25B6 Play", "#27ae60", "#1e8449");
        Button btnForward = styledButton("\u23ED Next", "#8e44ad", "#6c3483");
        Button btnReset = styledButton("\uD83D\uDD04 Reset", "#7f8c8d", "#636e72");

        btnBack.setOnAction(e -> compStepBackward());
        btnForward.setOnAction(e -> compStepForward());
        btnPlayPause.setOnAction(e -> compTogglePlayPause());
        btnReset.setOnAction(e -> compResetAll());

        Label speedText = new Label("\u26A1 Speed:");
        speedText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        speedText.setStyle("-fx-text-fill: #ecf0f1;");

        speedSlider = new Slider(0.25, 4.0, currentSpeedMultiplier);
        speedSlider.setPrefWidth(120);
        speedSlider.setBlockIncrement(0.25);

        speedLabel = new Label(String.format("%.1fx", currentSpeedMultiplier));
        speedLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        speedLabel.setStyle("-fx-text-fill: #f39c12;");
        speedLabel.setMinWidth(35);

        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentSpeedMultiplier = newVal.doubleValue();
            double newDelay = BASE_STEP_DELAY / currentSpeedMultiplier;
            speedLabel.setText(String.format("%.1fx", currentSpeedMultiplier));
            if (leftAnimator != null)
                leftAnimator.setStepDuration(newDelay);
            if (rightAnimator != null)
                rightAnimator.setStepDuration(newDelay);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox playbackBar = new HBox(10, btnBack, btnPlayPause, btnForward, btnReset,
                spacer, speedText, speedSlider, speedLabel);
        playbackBar.setAlignment(Pos.CENTER);
        playbackBar.setPadding(new Insets(8, 16, 10, 16));

        VBox bottomPanel = new VBox(0, styledSeparator(), playbackBar);
        bottomPanel.setStyle("-fx-background-color: #2c3e50;");
        bottomPanel.setPadding(new Insets(2, 12, 6, 12));

        return bottomPanel;
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
            leftStepLabel.setText("Step: 0 / 0");
        if (rightStepLabel != null)
            rightStepLabel.setText("Step: 0 / 0");
        if (leftStepDescLabel != null)
            leftStepDescLabel.setText(leftAlgorithm != null ? "Selected: " + leftAlgorithm : "Select an algorithm.");
        if (rightStepDescLabel != null)
            rightStepDescLabel.setText(rightAlgorithm != null ? "Selected: " + rightAlgorithm : "Select an algorithm.");
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
    }

    private void updateCompStepDisplay(String side) {
        boolean isLeft = side.equals("left");
        StepAnimator anim = isLeft ? leftAnimator : rightAnimator;
        Label stepLbl = isLeft ? leftStepLabel : rightStepLabel;
        Label descLbl = isLeft ? leftStepDescLabel : rightStepDescLabel;

        int cur = anim.getCurrentIndex() + 1;
        int total = anim.getTotalSteps();
        stepLbl.setText("Step: " + cur + " / " + total);

        Step currentStep = anim.getCurrentStep();
        if (currentStep != null) {
            descLbl.setText(currentStep.toDescription());
        }

        rebuildCompTraversalDisplay(side);
    }

    private void onCompAnimationComplete(String side) {
        boolean isLeft = side.equals("left");

        if (isLeft) {
            leftComplete = true;
            double runtimeMs = leftRuntimeNanos / 1_000_000.0;
            leftRuntimeLabel.setText("\u23F1 " + formatRuntime(runtimeMs));
            pranjal();
            leftRuntimeLabel.setVisible(true);
        } else {
            rightComplete = true;
            double runtimeMs = rightRuntimeNanos / 1_000_000.0;
            rightRuntimeLabel.setText("\u23F1 " + formatRuntime(runtimeMs));
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
        travLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        travLabel.setStyle("-fx-text-fill: #ecf0f1;");
        travLabel.setWrapText(true);
        travLabel.setMaxWidth(COMP_CANVAS_W - 40);

        Label travTitle = new Label("\uD83D\uDCCC Traversal");
        travTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        travTitle.setStyle("-fx-text-fill: #f39c12;");

        VBox box = new VBox(3, travTitle, travLabel);
        box.setPadding(new Insets(6, 10, 6, 10));
        box.setStyle(
                "-fx-background-color: rgba(44, 62, 80, 0.92);" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 2);");
        box.setMaxWidth(COMP_CANVAS_W - 20);
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
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        applyAlgoButtonStyle(btn, false);
        return btn;
    }

    private void applyAlgoButtonStyle(Button btn, boolean selected) {
        String bg = selected ? "#3498db" : "#455a6e";
        String hoverBg = selected ? "#2980b9" : "#3d566e";
        String border = selected ? "#2980b9" : "transparent";

        String base = "-fx-background-color: " + bg + "; -fx-text-fill: white; "
                + "-fx-padding: 7 18; -fx-background-radius: 20; -fx-cursor: hand; "
                + "-fx-border-color: " + border + "; -fx-border-radius: 20; -fx-border-width: 2;";
        String hover = "-fx-background-color: " + hoverBg + "; -fx-text-fill: white; "
                + "-fx-padding: 7 18; -fx-background-radius: 20; -fx-cursor: hand; "
                + "-fx-border-color: " + border + "; -fx-border-radius: 20; -fx-border-width: 2;";

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

    public static void main(String[] args) {
        launch(args);
    }
}
