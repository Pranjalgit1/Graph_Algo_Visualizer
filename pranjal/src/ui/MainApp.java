package ui;

import algorithms.BFS;
import algorithms.DFS;
import algorithms.Kruskal;
import algorithms.Dijkstra;
import algorithms.Prims;
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
import javafx.scene.control.Separator;
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
    private static final double STEP_DELAY = 0.6;

    private Stage primaryStage;
    private Pane canvas;
    private GraphRenderer renderer;
    private GraphVisualData visualData;
    private Graph graph;
    private boolean isDirectedGraph = false;
    private StepAnimator animator;

    private Label stepLabel;
    private Label stepDescriptionLabel;
    private Label runtimeLabel;
    private VBox runtimeSection;
    private Button btnPlayPause;

    private String selectedAlgorithm = null;
    private final List<Button> algoButtons = new ArrayList<>();
    private long algorithmRuntimeNanos = 0;

    // Traversal display
    private Label traversalLabel;
    private VBox traversalBox;
    private final Set<Integer> traversalOrderSet = new LinkedHashSet<>();

    // Track Kruskal/Prim's buttons for disabling on directed graphs
    private Button btnKruskal;
    private Button btnPrims;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showInputScreen();
    }

    private void showInputScreen() {
        GraphInputScreen inputScreen = new GraphInputScreen(primaryStage);
        inputScreen.setOnVisualize((graph, isDirected) -> {
            showVisualization(graph, isDirected);
        });
        inputScreen.show();
    }

    private void showVisualization(Graph inputGraph, boolean isDirected) {
        this.graph = inputGraph;
        this.isDirectedGraph = isDirected;
        this.selectedAlgorithm = null;
        this.algoButtons.clear();

        visualData = new GraphVisualData();
        visualData.buildFromGraph(graph, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2, 190);

        // ── Canvas ──
        canvas = new Pane();
        canvas.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        canvas.setStyle("-fx-background-color: #ecf0f1;");

        renderer = new GraphRenderer(canvas);
        renderer.render(visualData);

        // ── Traversal Overlay ──
        buildTraversalOverlay();

        animator = new StepAnimator(renderer, STEP_DELAY);
        animator.setVisualData(visualData);
        animator.setOnStepChange(this::updateStepDisplay);
        animator.setOnComplete(this::onAnimationComplete);

        // ── Header ──
        Button newGraphBtn = styledButton("\u2190 New Graph", "#8e44ad", "#6c3483");
        newGraphBtn.setOnAction(e -> {
            if (animator != null) animator.stop();
            showInputScreen();
        });

        Label title = new Label("Graph Algorithm Visualizer");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: white;");

        Region leftSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        Region rightSpacer = new Region();
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        HBox header = new HBox(10, newGraphBtn, leftSpacer, title, rightSpacer);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(14));
        header.setStyle("-fx-background-color: #34495e;");

        // ── Right Sidebar ──
        VBox sidebar = buildSidebar();

        // ── Bottom Panel ──
        VBox bottomPanel = buildBottomPanel();

        // ── Canvas with traversal overlay ──
        Pane canvasContainer = new Pane();
        canvasContainer.setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        canvasContainer.getChildren().addAll(canvas, traversalBox);

        // Bind overlay to bottom-right of canvas
        traversalBox.layoutXProperty().bind(
                canvasContainer.widthProperty().subtract(traversalBox.widthProperty()).subtract(10));
        traversalBox.layoutYProperty().bind(
                canvasContainer.heightProperty().subtract(traversalBox.heightProperty()).subtract(10));

        // ── Root Layout ──
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(canvasContainer);
        root.setRight(sidebar);
        root.setBottom(bottomPanel);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Graph Algorithm Visualizer");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // ═══════════════════════════════════════════════════════
    // SIDEBAR: Legend + Step + Runtime
    // ═══════════════════════════════════════════════════════

    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        // ── Legend Section ──
        VBox legendSection = buildLegendSection();

        // ── Separator ──
        Separator sep1 = styledSeparator();

        // ── Step Section ──
        VBox stepSection = buildStepSection();

        // ── Separator ──
        Separator sep2 = styledSeparator();

        // ── Runtime Section ──
        runtimeSection = buildRuntimeSection();

        sidebar.getChildren().addAll(legendSection, sep1, stepSection, sep2, runtimeSection);
        return sidebar;
    }

    private VBox buildLegendSection() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(16, 14, 12, 14));

        Label heading = sectionHeading("Legend");
        box.getChildren().add(heading);

        // Node colours
        Label nodeHeading = subHeading("Nodes");
        box.getChildren().add(nodeHeading);
        box.getChildren().add(legendItem(Color.web("#3498db"), "Default"));
        box.getChildren().add(legendItem(Color.web("#f39c12"), "In Queue"));
        box.getChildren().add(legendItem(Color.web("#e74c3c"), "Visiting"));
        box.getChildren().add(legendItem(Color.web("#2ecc71"), "Processed"));

        // Edge colours
        Label edgeHeading = subHeading("Edges");
        box.getChildren().add(edgeHeading);
        box.getChildren().add(legendLine(Color.web("#95a5a6"), "Default"));
        box.getChildren().add(legendLine(Color.web("#e67e22"), "Exploring"));
        box.getChildren().add(legendLine(Color.web("#f1c40f"), "Considered"));
        box.getChildren().add(legendLine(Color.web("#27ae60"), "Selected (MST)"));
        box.getChildren().add(legendLine(Color.web("#c0392b"), "Rejected"));

        return box;
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
        stepDescriptionLabel.setMaxWidth(190);

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

    // ═══════════════════════════════════════════════════════
    // BOTTOM PANEL: Algorithm Selection + Playback
    // ═══════════════════════════════════════════════════════

    private VBox buildBottomPanel() {
        // ── Algorithm Selection Bar ──
        Button btnBFS = algoToggleButton("BFS");
        Button btnDFS = algoToggleButton("DFS");
        Button btnDijkstra = algoToggleButton("Dijkstra");
        btnKruskal = algoToggleButton("Kruskal");
        btnPrims = algoToggleButton("Prim's");

        algoButtons.add(btnBFS);
        algoButtons.add(btnDFS);
        algoButtons.add(btnDijkstra);
        algoButtons.add(btnKruskal);
        algoButtons.add(btnPrims);

        btnBFS.setOnAction(e -> selectAlgorithm("BFS", btnBFS));
        btnDFS.setOnAction(e -> selectAlgorithm("DFS", btnDFS));
        btnDijkstra.setOnAction(e -> selectAlgorithm("Dijkstra", btnDijkstra));
        btnKruskal.setOnAction(e -> selectAlgorithm("Kruskal", btnKruskal));
        btnPrims.setOnAction(e -> selectAlgorithm("Prim's", btnPrims));

        // Disable Kruskal & Prim's for directed graphs
        if (isDirectedGraph) {
            btnKruskal.setDisable(true);
            btnPrims.setDisable(true);
            btnKruskal.setOpacity(0.4);
            btnPrims.setOpacity(0.4);
        }

        Label algoLabel = new Label("Algorithm:");
        algoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        algoLabel.setStyle("-fx-text-fill: #ecf0f1;");

        HBox algoBar = new HBox(10, algoLabel, btnBFS, btnDFS, btnDijkstra, btnKruskal, btnPrims);
        algoBar.setAlignment(Pos.CENTER);
        algoBar.setPadding(new Insets(10, 0, 6, 0));

        // ── Playback Controls ──
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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox playbackBar = new HBox(10, btnBack, btnPlayPause, btnForward, btnReset, spacer, stepLabel);
        playbackBar.setAlignment(Pos.CENTER);
        playbackBar.setPadding(new Insets(6, 16, 10, 16));

        // ── Combined Bottom ──
        Separator sep = styledSeparator();

        VBox bottomPanel = new VBox(0, algoBar, sep, playbackBar);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setStyle("-fx-background-color: #2c3e50;");
        bottomPanel.setPadding(new Insets(4, 12, 6, 12));

        return bottomPanel;
    }

    // ═══════════════════════════════════════════════════════
    // ALGORITHM SELECTION + EXECUTION
    // ═══════════════════════════════════════════════════════

    private void selectAlgorithm(String name, Button selected) {
        resetAll();
        selectedAlgorithm = name;

        // Update toggle button styles
        for (Button btn : algoButtons) {
            applyAlgoButtonStyle(btn, false);
        }
        applyAlgoButtonStyle(selected, true);
    }

    private void runSelectedAlgorithm() {
        if (selectedAlgorithm == null)
            return;

        List<Step> steps;
        long startTime = System.nanoTime();

        switch (selectedAlgorithm) {
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
            default:
                return;
        }

        long endTime = System.nanoTime();
        algorithmRuntimeNanos = endTime - startTime;

        // Hide runtime until animation completes
        runtimeSection.setVisible(false);
        runtimeSection.setManaged(false);

        animator.load(steps);
        animator.play();
        btnPlayPause.setText("\u23F8 Pause");
    }

    // ═══════════════════════════════════════════════════════
    // PLAYBACK CONTROLS
    // ═══════════════════════════════════════════════════════

    private void togglePlayPause() {
        if (animator.isPlaying()) {
            animator.pause();
            btnPlayPause.setText("\u25B6 Play");
        } else {
            if (animator.getTotalSteps() == 0 && selectedAlgorithm != null) {
                // First play after selecting algorithm
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
        // Reset traversal display
        traversalOrderSet.clear();
        traversalLabel.setText("");
        traversalBox.setVisible(false);
    }

    private void updateStepDisplay() {
        int cur = animator.getCurrentIndex() + 1;
        int total = animator.getTotalSteps();
        stepLabel.setText("Step: " + cur + " / " + total);

        Step currentStep = animator.getCurrentStep();
        if (currentStep != null) {
            stepDescriptionLabel.setText(currentStep.toDescription());
        }

        // Rebuild traversal display from all steps up to current index
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
                || "Dijkstra".equals(selectedAlgorithm);

        if (!isTraversalAlgo) {
            traversalLabel.setText("");
            traversalBox.setVisible(false);
            return;
        }

        // Scan all steps from 0 to current index
        for (int i = 0; i <= currentIdx; i++) {
            Step s = animator.getStepAt(i);
            if (s != null && s.getType() == StepType.VISIT_NODE) {
                traversalOrderSet.add(s.getNode());
            }
        }

        // Build display string
        if (!traversalOrderSet.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (int node : traversalOrderSet) {
                if (!first) sb.append(" \u2192 ");
                sb.append(node);
                first = false;
            }
            traversalLabel.setText(sb.toString());
            traversalBox.setVisible(true);
        } else {
            traversalLabel.setText("");
            traversalBox.setVisible(false);
        }
    }

    private void onAnimationComplete() {
        btnPlayPause.setText("\u25B6 Play");

        // Show runtime
        double runtimeMs = algorithmRuntimeNanos / 1_000_000.0;
        String formatted;
        if (runtimeMs < 1.0) {
            formatted = String.format("%.3f ms", runtimeMs);
        } else {
            formatted = String.format("%.2f ms", runtimeMs);
        }
        runtimeLabel.setText("Total: " + formatted);
        runtimeSection.setVisible(true);
        runtimeSection.setManaged(true);
    }

    // ═══════════════════════════════════════════════════════
    // TRAVERSAL OVERLAY
    // ═══════════════════════════════════════════════════════

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

    // ═══════════════════════════════════════════════════════
    // UI HELPER METHODS
    // ═══════════════════════════════════════════════════════

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
