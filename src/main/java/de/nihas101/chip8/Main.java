package de.nihas101.chip8;

import de.nihas101.chip8.debug.Debugger;
import de.nihas101.chip8.hardware.Emulator;
import de.nihas101.chip8.savestates.SaveState;
import de.nihas101.chip8.utils.ResizableCanvas;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
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

public class Main extends Application{
    public Emulator emulator;
    private Scene scene;
    private Pane root;
    private MainController mainController;
    private ResizableCanvas canvas;
    private Timeline timeline;

    private EventHandler<KeyEvent> keyPressedEventEventHandler;
    private EventHandler<KeyEvent> keyReleasedEventHandler;

    /* Debugging */
    private Debugger debugger;
    private Stage debuggerStage;
    private boolean stepByStep = false;
    private boolean nextStep = false;

    private Logger logger = Logger.getLogger(Main.class.getName());

    /**
     *  Standard constructor needed for JavaFX
     */
    public Main(){
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
        /* TODO: Refactor */
        /* TODO: Pause when choosing ROM */

        /* Load root-node */
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        root = loader.load();
        mainController = loader.getController();

        /* Create Scene */
        scene = new Scene(root);
        primaryStage.setTitle("CHIP-8 Main");
        primaryStage.setScene(scene);

        /* Setup emulator instance */
        emulator = Emulator.createBlackBox();
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
            emulator.stop();
        });

        primaryStage.show();

        /* Bind size properties of canvas */
        double gridPaneHeight = ((BorderPane)root.getChildren().get(0)).getTop().getBoundsInParent().getHeight() - 8;
        canvas.widthProperty().bind(root.widthProperty().add(1));
        canvas.heightProperty().bind(root.heightProperty().subtract(gridPaneHeight));
    }

    private void setupCanvas(){
        /* Create resizable canvas and add it to the scene */
        canvas = new ResizableCanvas(emulator.getCentralProcessingUnit().getScreenMemory());
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
                while (!emulator.getCentralProcessingUnit().isStop()) {
                    if (emulator.getCentralProcessingUnit().isPause()) {
                        yield();
                        continue;
                    }
                    leftCycle = emulator.executeCPUCycles(cycles);
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
            logger.severe(emulator.getState());
            currentThread().interrupt();
        }
    }

    /**
     * Waits for the user to hit the key to calculate the next step
     */
    private void waitForStep() {
        if(stepByStep && !emulator.getCentralProcessingUnit().isStop()){
            nextStep = false;
            while(stepByStep && !nextStep && !emulator.getCentralProcessingUnit().isStop()) waitFor(STEP_WAIT_TIME);
        }
    }

    /**
     * Stops the main loop executing cpu cycles
     */
    @Override
    public void stop(){
        /* Stop the thread that is used as timer */
        emulator.stop();
        timeline.stop();
        if(debugger.isDebugging()) debugger.stop();
    }

    /**
     * Sets up {@link KeyEvent}s
     */
    private void setupEventHandler() {
        /* Remove old EventHandlers */
        if(keyPressedEventEventHandler != null)
            scene.removeEventHandler(KeyEvent.KEY_PRESSED, keyPressedEventEventHandler);
        if(keyReleasedEventHandler != null)
            scene.removeEventHandler(KeyEvent.KEY_RELEASED, keyReleasedEventHandler);

        keyPressedEventEventHandler = createKeyPressedEventHandler();
        keyReleasedEventHandler = (event) -> emulator.getCentralProcessingUnit().setKeyCode(NO_KEY);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyPressedEventEventHandler);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, keyReleasedEventHandler);
    }

    private EventHandler<KeyEvent> createKeyPressedEventHandler(){
        return (event) -> {
            switch(event.getCode()){
                case N: emulator.getCentralProcessingUnit().setKeyCode(KEY_0); break;
                case Q: emulator.getCentralProcessingUnit().setKeyCode(KEY_1); break;
                case W: emulator.getCentralProcessingUnit().setKeyCode(KEY_2); break;
                case E: emulator.getCentralProcessingUnit().setKeyCode(KEY_3); break;
                case A: emulator.getCentralProcessingUnit().setKeyCode(KEY_4); break;
                case S: emulator.getCentralProcessingUnit().setKeyCode(KEY_5); break;
                case D: emulator.getCentralProcessingUnit().setKeyCode(KEY_6); break;
                case Z: emulator.getCentralProcessingUnit().setKeyCode(KEY_7); break;
                case X: emulator.getCentralProcessingUnit().setKeyCode(KEY_8); break;
                case C: emulator.getCentralProcessingUnit().setKeyCode(KEY_9); break;
                case B: emulator.getCentralProcessingUnit().setKeyCode(KEY_A); break;
                case M: emulator.getCentralProcessingUnit().setKeyCode(KEY_B); break;
                case R: emulator.getCentralProcessingUnit().setKeyCode(KEY_C); break;
                case F: emulator.getCentralProcessingUnit().setKeyCode(KEY_D); break;
                case V: emulator.getCentralProcessingUnit().setKeyCode(KEY_E); break;
                case COMMA: emulator.getCentralProcessingUnit().setKeyCode(KEY_F); break;
                case F3: nextStep = true; break;
                case F1: handleDebugger(); break;
                case F2: switchStepByStep(); break;
                case F4: emulator.getCentralProcessingUnit().reset(); break;
                default: /* NOP */
            }
        };
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
        canvas.setMemory(emulator.getCentralProcessingUnit().getScreenMemory());
        setupController();
        setupEventHandler();
        debugger = new Debugger();
        debugger.setDebuggable(emulator.getCentralProcessingUnit());
        timeline = canvas.setupTimeLine();
        timeline.play();
    }

    public void startEmulation(){
        mainController.startEmulation();
    }

    public SaveState createSaveState(){
        return SaveState.createSaveState(emulator.getCentralProcessingUnit());
    }

    public void setState(SaveState saveState){
        stop();
        emulator.setCentralProcessingUnit(saveState.cpu);
        setupEmulation();
        startEmulation();
    }
}
