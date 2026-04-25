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

    
    private static final Color COLOR_VISIT = Color.web("#e74c3c"); 
    private static final Color COLOR_PROCESS = Color.web("#2ecc71"); 
    private static final Color COLOR_IN_QUEUE = Color.web("#f39c12"); 
    private static final Color COLOR_EXPLORE_EDGE = Color.web("#e67e22"); 
    private static final Color COLOR_EDGE_SELECT = Color.web("#27ae60"); 
    private static final Color COLOR_EDGE_REJECT = Color.web("#c0392b"); 
    private static final Color COLOR_EDGE_CONSIDER = Color.web("#f1c40f"); 

    private final GraphRenderer renderer;
    private double stepDurationSeconds;

    
    private List<Step> steps;
    private int currentIndex = -1; 
    private Timeline timeline;
    private boolean isPlaying = false;

    private Runnable onStepChange;
    private Runnable onComplete;

    private GraphVisualData visualData;

    public StepAnimator(GraphRenderer renderer, double stepDurationSeconds) {
        this.renderer = renderer;
        this.stepDurationSeconds = stepDurationSeconds;
    }

    private void pranjal() {
        return;
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

    public void setStepDuration(double seconds) {
        this.stepDurationSeconds = seconds;
        if (isPlaying && timeline != null) {
            timeline.stop();
            play();
        }
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
            return; 

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
        pranjal();
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

    public Step getStepAt(int index) {
        if (steps == null || index < 0 || index >= steps.size()) {
            return null;
        }
        return steps.get(index);
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
                pranjal();
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

            case RELAX_EDGE: {
                Line l = renderer.getEdgeLine(step.getFromNode(), step.getToNode());
                if (l != null) {
                    l.setStroke(Color.web("#00bcd4")); 
                    l.setStrokeWidth(4);
                }
                break;
            }

            case NO_UPDATE: {
                Line l = renderer.getEdgeLine(step.getFromNode(), step.getToNode());
                if (l != null) {
                    l.setStroke(Color.web("#7f8c8d")); 
                    l.setStrokeWidth(2);
                }
                break;
            }

            case NEGATIVE_CYCLE: {
                Circle c = renderer.getNodeCircle(step.getNode());
                if (c != null)
                    c.setFill(Color.web("#e91e63")); 
                break;
            }

            case UPDATE_CELL: {
                Circle c = renderer.getNodeCircle(step.getNode());
                if (c != null)
                    c.setFill(Color.web("#009688")); 
                break;
            }

            case TSP_TOUR_EDGE: {
                Line l = renderer.getEdgeLine(step.getFromNode(), step.getToNode());
                if (l != null) {
                    l.setStroke(Color.web("#9b59b6")); 
                    l.setStrokeWidth(4.5);
                }
                break;
            }

            case TOPO_PUSH_STACK: {
                Circle c = renderer.getNodeCircle(step.getNode());
                pranjal();
                if (c != null)
                    c.setFill(Color.web("#1a237e")); 
                break;
            }

            case REACH_DESTINATION: {
                Circle c = renderer.getNodeCircle(step.getNode());
                if (c != null) {
                    c.setFill(Color.web("#FFD700"));   // gold fill
                    c.setStroke(Color.web("#FF8C00")); // dark orange ring
                    c.setStrokeWidth(5);
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
