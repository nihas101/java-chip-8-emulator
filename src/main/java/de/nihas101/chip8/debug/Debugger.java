package de.nihas101.chip8.debug;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * A class for displaying debugger information of {@link Debuggable} instances
 */
public class Debugger extends Application {
    private Debuggable debuggable;
    private Timeline timeline;
    private volatile boolean isDebugging = false;
    private TextFlowController textFlowController;
    private Stage primaryStage;
    private boolean stepByStep;

    @Override
    public void start(Stage primaryStage) throws Exception {
        isDebugging = true;
        this.primaryStage = primaryStage;
        /* Load root-node and get controller */
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("debug.fxml"));
        Pane root = loader.load();
        textFlowController = loader.getController();
        textFlowController.addText(new Text());

        /* Create Scene */
        Scene scene = new Scene(root);
        primaryStage.setTitle("CHIP-8 Main - Debug");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(windowEvent -> isDebugging = false);

        /* Setup keyframes to draw the canvas */
        final Duration oneFrameAmt = Duration.seconds(1);
        final KeyFrame oneFrame = new KeyFrame(oneFrameAmt, event -> updateDebugInfo());
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(oneFrame);
        timeline.play();

        primaryStage.show();
    }

    private void updateDebugInfo() {
        textFlowController.setDebugText(debuggable.getState() + "step by step: " + stepByStep);
    }

    @Override
    public void stop(){
        isDebugging = false;
        timeline.stop();
        primaryStage.close();
    }

    public void setDebuggable(Debuggable debuggable){
        this.debuggable = debuggable;
    }

    public boolean isDebugging() {
        return isDebugging;
    }

    public void setStepByStep(boolean stepByStep) {
        this.stepByStep = stepByStep;
    }
}
