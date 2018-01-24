package de.nihas101.chip8;

import de.nihas101.chip8.debug.Debugger;
import de.nihas101.chip8.hardware.CentralProcessingUnit;
import de.nihas101.chip8.hardware.memory.*;
import de.nihas101.chip8.hardware.timers.DelayTimer;
import de.nihas101.chip8.hardware.timers.SoundTimer;
import de.nihas101.chip8.unsignedDataTypes.UnsignedShort;
import de.nihas101.chip8.utils.ResizableCanvas;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.midi.*;
import java.util.Random;
import java.util.Timer;
import java.util.logging.Logger;

import static de.nihas101.chip8.hardware.memory.ScreenMemory.SCREEN_HEIGHT;
import static de.nihas101.chip8.hardware.memory.ScreenMemory.SCREEN_WIDTH;
import static de.nihas101.chip8.utils.Constants.*;
import static java.lang.Thread.*;

public class Emulator extends Application{
    public CentralProcessingUnit cpu;
    private ResizableCanvas canvas;
    private Synthesizer synthesizer = null;
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
        /* Setup emulator instance */
        this.cpu = setupEmulator();

        /* Load root-node */
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        Pane root = loader.load();
        MainController mainController = loader.getController();

        /* Create resizable canvas and add it to the scene */
        canvas = new ResizableCanvas(cpu.getScreenMemory());
        ((BorderPane)root.getChildren().get(0)).setCenter(canvas);

        /* Create Scene */
        Scene scene = new Scene(root);
        primaryStage.setTitle("CHIP-8 Emulator");
        primaryStage.setScene(scene);

        /* Setup key events */
        setupEventHandler(scene, cpu);

        /* Create Debugger */
        debugger = new Debugger();
        debugger.setDebuggable(cpu);

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
            cpu.stopCPU();
        });

        /* Setup keyframes to draw the canvas */
        final Duration oneFrameAmt = Duration.ONE;
        final KeyFrame oneFrame = new KeyFrame(oneFrameAmt, event -> canvas.draw());
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(oneFrame);
        timeline.play();
        /* Interface called to produce Threads */
        Runnable cpuThreadRunner = () ->{
            /* Start the thread to execute cpu cycles */
            new Thread(() -> {
                double cycles = 1;
                double leftCycle;
                while(!cpu.isStop()) {
                    leftCycle = executeCPUCycles(cycles);
                    waitForStep();
                    /* Wait and calculate how many cycles to execute */
                    if(stepByStep) cycles = 1; // Always execute one step in step-by-step mode
                    else {
                        cycles = System.currentTimeMillis() + CYCLE_WAIT_TIME;
                        waitFor(CYCLE_WAIT_TIME);
                        cycles = (System.currentTimeMillis() / cycles) * mainController.getSpeed() + leftCycle;
                    }
                }
            }).start();
        };

        mainController.setup(cpuThreadRunner, cpu, canvas);

        primaryStage.show();

        /* Bind size properties of canvas */
        double gridPaneHeight = ((BorderPane)root.getChildren().get(0)).getTop().getBoundsInParent().getHeight() - 8;
        canvas.widthProperty().bind(root.widthProperty().add(1));
        canvas.heightProperty().bind(root.heightProperty().subtract(gridPaneHeight));
    }

    /**
     * Waits for the given amount of milliseconds
     * @param milliseconds The milliseconds to wait
     */
    private void waitFor(long milliseconds) {
        try { Thread.sleep(milliseconds); }
        catch (InterruptedException e) {
            logger.severe(e.getMessage());
            logger.severe(cpu.getState());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Waits for the user to hit the key to calculate the next step
     */
    private void waitForStep() {
        if(stepByStep && !cpu.isStop()){
            nextStep = false;
            while(stepByStep && !nextStep && !cpu.isStop()) waitFor(STEP_WAIT_TIME);
        }
    }

    /**
     * Executes CPU cycles
     * @param cycles The number of cycles to execute
     * @return The part of the cycles that wasn't  a full cycle
     * e.g. if the cpu was instructed to calculate 1.2 cycles this
     * will return 0.2, so this debt can be accumulated and corrected once this debt
     * equals a full cycle
     */
    private double executeCPUCycles(double cycles) {
        for( ; cycles >= 1 ; cycles --) {
            try {
                cpu.decodeNextOpCode();
            } catch (Exception e) {
                cpu.stopCPU();
                logger.severe(cpu.getState());
                logger.severe(e.getMessage());
            }
        }
        return cycles;
    }

    /**
     * Stops the main loop executing cpu cycles
     */
    @Override
    public void stop(){
        /* Stop the thread that is used as timer */
        cpu.stopCPU();
        cpu.stopTimer();
        timeline.stop();
        if(debugger.isDebugging()) debugger.stop();
    }

    /**
     * Sets up {@link KeyEvent}s
     * @param scene The {@link Scene} for which to set up the {@link KeyEvent}s
     * @param cpu The {@link Chip8CentralProcessingUnit} to send the {@link KeyEvent}s to
     */
    private void setupEventHandler(Scene scene, Chip8CentralProcessingUnit cpu) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            switch(event.getCode()){
                case N: cpu.setKeyCode(KEY_0); break;
                case Q: cpu.setKeyCode(KEY_1); break;
                case W: cpu.setKeyCode(KEY_2); break;
                case E: cpu.setKeyCode(KEY_3); break;
                case A: cpu.setKeyCode(KEY_4); break;
                case S: cpu.setKeyCode(KEY_5); break;
                case D: cpu.setKeyCode(KEY_6); break;
                case Z: cpu.setKeyCode(KEY_7); break;
                case X: cpu.setKeyCode(KEY_8); break;
                case C: cpu.setKeyCode(KEY_9); break;
                case B: cpu.setKeyCode(KEY_A); break;
                case M: cpu.setKeyCode(KEY_B); break;
                case R: cpu.setKeyCode(KEY_C); break;
                case F: cpu.setKeyCode(KEY_D); break;
                case V: cpu.setKeyCode(KEY_E); break;
                case COMMA: cpu.setKeyCode(KEY_F); break;
                case F3: nextStep = true; break;
                case F1: handleDebugger(); break;
                case F2: switchStepByStep(); break;
                case F4: cpu.reset(); break;
                default: /* NOP */
            }
        });
        /* 255 = No key pressed*/
        scene.addEventHandler(KeyEvent.KEY_RELEASED, (event) -> this.cpu.setKeyCode(NO_KEY));
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

    /**
     * Creates the necessary instances for the emulator
     * @return The created {@link Chip8CentralProcessingUnit}
     */
    private Chip8CentralProcessingUnit setupEmulator() {
        /* Build Hardware */
        Chip8Memory memory = new Chip8Memory();
        ScreenMemory screenMemory = new ScreenMemory();
        Chip8Registers registers = new Chip8Registers();
        Chip8AddressRegister addressRegister = new Chip8AddressRegister();
        Chip8ProgramCounter programCounter = new Chip8ProgramCounter(new UnsignedShort(PROGRAM_COUNTER_START));
        Chip8Stack stack = new Chip8Stack(new Stack<>());
        Timer timer = new Timer("Timer");
        DelayTimer delayTimer = new DelayTimer();
        SoundTimer soundTimer = new SoundTimer();
        Random random = new Random();

        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
        } catch (MidiUnavailableException e) {
            logger.severe(e.getMessage());
            System.exit(1);
        }

        /* get and load default instrument and channel lists */
        Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();
        MidiChannel[] midiChannels = synthesizer.getChannels();

        synthesizer.loadInstrument(instruments[0]);

        return new Chip8CentralProcessingUnit(
                memory, screenMemory,
                registers, addressRegister, programCounter, stack,
                timer, delayTimer, soundTimer,
                random,
                midiChannels[0]);
    }
}
