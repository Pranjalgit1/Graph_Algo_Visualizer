package ui;

import graph.GraphVisualData;
import step.Step;
import step.StepType;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.List;

public class StepAnimator {

    // colors
    private static final Color COLOR_VISIT = Color.web("#e74c3c"); // red
    private static final Color COLOR_PROCESS = Color.web("#2ecc71"); // green
    private static final Color COLOR_IN_QUEUE = Color.web("#f39c12"); // orange
    private static final Color COLOR_EXPLORE_EDGE = Color.web("#e67e22"); // dark orange
    private static final Color COLOR_EDGE_SELECT = Color.web("#27ae60"); // green
    private static final Color COLOR_EDGE_REJECT = Color.web("#c0392b"); // dark red
    private static final Color COLOR_EDGE_CONSIDER = Color.web("#f1c40f"); // yellow

    private final GraphRenderer renderer;
    private final double stepDurationSeconds;

    // playing state
    private List<Step> steps;
    private int currentIndex = -1; // steps
    private Timeline timeline;
    private boolean isPlaying = false;

    private Runnable onStepChange;
    private Runnable onComplete;

    private GraphVisualData visualData;

    public StepAnimator(GraphRenderer renderer, double stepDurationSeconds) {
        this.renderer = renderer;
        this.stepDurationSeconds = stepDurationSeconds;
    }

    public void setOnStepChange(Runnable callback) {
        this.onStepChange = callback;
    }

    public void setOnComplete(Runnable callback) {
        this.onComplete = callback;
    }

    public void setVisualData(GraphVisualData data) {
        this.visualData = data;
    }

    public void load(List<Step> steps) {
        stop();
        this.steps = steps;
        this.currentIndex = -1;
        fireStepChange();
    }

    public void play() {
        if (steps == null || steps.isEmpty())
            return;
        if (currentIndex >= steps.size() - 1)
            return; // already at the end

        isPlaying = true;
        timeline = new Timeline();
        timeline.setCycleCount(1);

        int startFrom = currentIndex + 1;
        for (int i = startFrom; i < steps.size(); i++) {
            final int idx = i;
            double delay = (i - startFrom + 1) * stepDurationSeconds;
            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.seconds(delay),
                    event -> {
                        currentIndex = idx;
                        processStep(steps.get(idx));
                        fireStepChange();
                    }));
        }

        timeline.setOnFinished(e -> {
            isPlaying = false;
            if (onComplete != null) {
                onComplete.run();
            }
        });
        timeline.play();
    }

    public void pause() {
        if (timeline != null) {
            timeline.pause();
            isPlaying = false;
        }
    }

    public void resume() {
        if (timeline != null) {
            timeline.play();
            isPlaying = true;
        }
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
        isPlaying = false;
    }

    public void stepForward() {
        if (steps == null || currentIndex >= steps.size() - 1)
            return;
        stop();
        currentIndex++;
        processStep(steps.get(currentIndex));
        fireStepChange();

        // fixing of no runtime on next button click
        if (currentIndex == steps.size() - 1 && onComplete != null) {
            onComplete.run();
        }
    }

    public void stepBackward() {
        if (steps == null || currentIndex < 0)
            return;
        stop();
        currentIndex--;

        renderer.render(visualData);

        // Replay all steps up to the new current index
        for (int i = 0; i <= currentIndex; i++) {
            processStep(steps.get(i));
        }
        fireStepChange();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalSteps() {
        return steps == null ? 0 : steps.size();
    }

    public Step getCurrentStep() {
        if (steps == null || currentIndex < 0 || currentIndex >= steps.size()) {
            return null;
        }
        return steps.get(currentIndex);
    }

    private void processStep(Step step) {
        switch (step.getType()) {

            case VISIT_NODE: {
                Circle c = renderer.getNodeCircle(step.getNode());
                if (c != null)
                    c.setFill(COLOR_VISIT);
                break;
            }

            case PROCESS_NODE: {
                Circle c = renderer.getNodeCircle(step.getNode());
                if (c != null)
                    c.setFill(COLOR_PROCESS);
                break;
            }

            case ADD_TO_QUEUE: {
                Circle c = renderer.getNodeCircle(step.getNode());
                if (c != null)
                    c.setFill(COLOR_IN_QUEUE);
                break;
            }

            case REMOVE_FROM_QUEUE: {
                Circle c = renderer.getNodeCircle(step.getNode());
                if (c != null)
                    c.setFill(COLOR_VISIT);
                break;
            }

            case EXPLORE_EDGE: {
                Line l = renderer.getEdgeLine(step.getFromNode(), step.getToNode());
                if (l != null) {
                    l.setStroke(COLOR_EXPLORE_EDGE);
                    l.setStrokeWidth(3.5);
                }
                break;
            }

            case EDGE_CONSIDERED: {
                Line l = renderer.getEdgeLine(step.getFromNode(), step.getToNode());
                if (l != null) {
                    l.setStroke(COLOR_EDGE_CONSIDER);
                    l.setStrokeWidth(4);
                }
                break;
            }

            case EDGE_SELECTED: {
                Line l = renderer.getEdgeLine(step.getFromNode(), step.getToNode());
                if (l != null) {
                    l.setStroke(COLOR_EDGE_SELECT);
                    l.setStrokeWidth(4);
                }
                break;
            }

            case EDGE_REJECTED: {
                Line l = renderer.getEdgeLine(step.getFromNode(), step.getToNode());
                if (l != null) {
                    l.setStroke(COLOR_EDGE_REJECT);
                    l.setStrokeWidth(3);
                }
                break;
            }
        }
    }

    private void fireStepChange() {
        if (onStepChange != null) {
            onStepChange.run();
        }
    }
}
