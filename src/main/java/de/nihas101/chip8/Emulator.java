package de.nihas101.chip8;

import de.nihas101.chip8.debug.Debugger;
import de.nihas101.chip8.hardware.BlackBox;
import de.nihas101.chip8.savestates.SaveState;
import de.nihas101.chip8.utils.ResizableCanvas;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.logging.Logger;

import static de.nihas101.chip8.hardware.memory.ScreenMemory.SCREEN_HEIGHT;
import static de.nihas101.chip8.hardware.memory.ScreenMemory.SCREEN_WIDTH;
import static de.nihas101.chip8.utils.Constants.*;
import static java.lang.Thread.*;

public class Emulator extends Application{
    public BlackBox blackBox;
    private Scene scene;
    private Pane root;
    private MainController mainController;
    private ResizableCanvas canvas;
    private Timeline timeline;

    /* Debugging */
    private Debugger debugger;
    private Stage debuggerStage;
    private boolean stepByStep = false;
    private boolean nextStep = false;

    private Logger logger = Logger.getLogger(Emulator.class.getName());

    /**
     *  Standard constructor needed for JavaFX
     */
    public Emulator(){
    }

    /**
     * Calls the method to launch the application
     * @param args The arguments for the application
     */
    public static void main(String[] args) {
        /* Launch Application */
        launch(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        /* TODO: Implement ability to reconfigure keys */
        /* TODO: Refactor variables into data classes */
        /* TODO: Refactor */
        /* TODO: Pause when choosing ROM */

        /* Load root-node */
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        root = loader.load();
        mainController = loader.getController();

        /* Create Scene */
        scene = new Scene(root);
        primaryStage.setTitle("CHIP-8 Emulator");
        primaryStage.setScene(scene);

        /* Setup emulator instance */
        blackBox = BlackBox.createBlackBox();
        //this.cpu = setupHardware();
        setupCanvas();
        setupEmulation();

        /* Keep aspect ratio on resize */
        primaryStage.minWidthProperty().bind(root.heightProperty().multiply(2));
        primaryStage.minHeightProperty().bind(root.widthProperty().divide(2));

        /* Set initial size */
        primaryStage.setHeight(SCREEN_HEIGHT*10);
        primaryStage.setWidth(SCREEN_WIDTH*10);

        primaryStage.setOnCloseRequest(windowEvent -> {
            /* Close debugger if it is open on closing the main window */
            if(debugger.isDebugging()) debugger.stop();
            /* In case cpu is looking for user input interrupt it */
            blackBox.stop();
        });

        primaryStage.show();

        /* Bind size properties of canvas */
        double gridPaneHeight = ((BorderPane)root.getChildren().get(0)).getTop().getBoundsInParent().getHeight() - 8;
        canvas.widthProperty().bind(root.widthProperty().add(1));
        canvas.heightProperty().bind(root.heightProperty().subtract(gridPaneHeight));
    }

    private void setupCanvas(){
        /* Create resizable canvas and add it to the scene */
        canvas = new ResizableCanvas(blackBox.getCentralProcessingUnit().getScreenMemory());
        ((BorderPane)root.getChildren().get(0)).setCenter(canvas);

        timeline = canvas.setupTimeLine();
    }

    private void setupController(){
        /* Interface called to produce Threads */
        Runnable cpuThreadRunner = () -> {
            /* Start the thread to execute cpu cycles */
            new Thread(() -> {
                double cycles = 1;
                double leftCycle;
                while (!blackBox.getCentralProcessingUnit().isStop()) {
                    if (blackBox.getCentralProcessingUnit().isPause()) {
                        yield();
                        continue;
                    }
                    leftCycle = blackBox.executeCPUCycles(cycles);
                    waitForStep();
                    /* Wait and calculate how many cycles to execute */
                    if (stepByStep) cycles = 1; // Always execute one step in step-by-step mode
                    else {
                        cycles = System.currentTimeMillis() + CYCLE_WAIT_TIME;
                        waitFor(CYCLE_WAIT_TIME);
                        cycles = (System.currentTimeMillis() / cycles) * mainController.getSpeed() + leftCycle;
                    }
                }
            }).start();
        };

        mainController.setup(cpuThreadRunner, this, canvas);
    }

    /**
     * Waits for the given amount of milliseconds
     * @param milliseconds The milliseconds to wait
     */
    private void waitFor(long milliseconds) {
        try { sleep(milliseconds); }
        catch (InterruptedException e) {
            logger.severe(e.getMessage());
            logger.severe(blackBox.getState());
            currentThread().interrupt();
        }
    }

    /**
     * Waits for the user to hit the key to calculate the next step
     */
    private void waitForStep() {
        if(stepByStep && !blackBox.getCentralProcessingUnit().isStop()){
            nextStep = false;
            while(stepByStep && !nextStep && !blackBox.getCentralProcessingUnit().isStop()) waitFor(STEP_WAIT_TIME);
        }
    }

    /**
     * Stops the main loop executing cpu cycles
     */
    @Override
    public void stop(){
        /* Stop the thread that is used as timer */
        blackBox.stop();
        timeline.stop();
        if(debugger.isDebugging()) debugger.stop();
    }

    /**
     * Sets up {@link KeyEvent}s
     */
    private void setupEventHandler() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            switch(event.getCode()){
                case N: blackBox.getCentralProcessingUnit().setKeyCode(KEY_0); break;
                case Q: blackBox.getCentralProcessingUnit().setKeyCode(KEY_1); break;
                case W: blackBox.getCentralProcessingUnit().setKeyCode(KEY_2); break;
                case E: blackBox.getCentralProcessingUnit().setKeyCode(KEY_3); break;
                case A: blackBox.getCentralProcessingUnit().setKeyCode(KEY_4); break;
                case S: blackBox.getCentralProcessingUnit().setKeyCode(KEY_5); break;
                case D: blackBox.getCentralProcessingUnit().setKeyCode(KEY_6); break;
                case Z: blackBox.getCentralProcessingUnit().setKeyCode(KEY_7); break;
                case X: blackBox.getCentralProcessingUnit().setKeyCode(KEY_8); break;
                case C: blackBox.getCentralProcessingUnit().setKeyCode(KEY_9); break;
                case B: blackBox.getCentralProcessingUnit().setKeyCode(KEY_A); break;
                case M: blackBox.getCentralProcessingUnit().setKeyCode(KEY_B); break;
                case R: blackBox.getCentralProcessingUnit().setKeyCode(KEY_C); break;
                case F: blackBox.getCentralProcessingUnit().setKeyCode(KEY_D); break;
                case V: blackBox.getCentralProcessingUnit().setKeyCode(KEY_E); break;
                case COMMA: blackBox.getCentralProcessingUnit().setKeyCode(KEY_F); break;
                case F3: nextStep = true; break;
                case F1: handleDebugger(); break;
                case F2: switchStepByStep(); break;
                case F4: blackBox.getCentralProcessingUnit().reset(); break;
                default: /* NOP */
            }
        });
        /* 255 = No key pressed*/
        scene.addEventHandler(KeyEvent.KEY_RELEASED, (event) -> blackBox.getCentralProcessingUnit().setKeyCode(NO_KEY));
    }

    private void switchStepByStep(){
        stepByStep = !stepByStep;
        debugger.setStepByStep(stepByStep);
    }

    private void handleDebugger() {
        if(!debugger.isDebugging()){
            try {
                debuggerStage = new Stage();
                debugger.start(debuggerStage);
            } catch (Exception e) {
                logger.severe(e.getMessage());
            }
        }else debugger.stop();
    }

    private void setupEmulation(){
        canvas.setMemory(blackBox.getCentralProcessingUnit().getScreenMemory());
        setupController();
        setupEventHandler();
        debugger = new Debugger();
        debugger.setDebuggable(blackBox.getCentralProcessingUnit());
        timeline = canvas.setupTimeLine();
        timeline.play();
    }

    public void startEmulation(){
        mainController.startEmulation();
    }

    public SaveState createSaveState(){
        return SaveState.createSaveState(blackBox.getCentralProcessingUnit());
    }

    public void setState(SaveState saveState){
        stop();
        blackBox.setCentralProcessingUnit(saveState.cpu);
        setupEmulation();
        startEmulation();
    }
}
