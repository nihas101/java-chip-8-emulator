package de.nihas101.chip8;

import de.nihas101.chip8.hardware.memory.Memory;
import de.nihas101.chip8.savestates.FailedReadingStateException;
import de.nihas101.chip8.savestates.SaveState;
import de.nihas101.chip8.savestates.SaveStateHandler;
import de.nihas101.chip8.utils.ResizableCanvas;
import de.nihas101.chip8.utils.RomLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

public class MainController {
    @FXML
    public Button romLoaderButton;
    @FXML
    public ColorPicker colorPickerSprite;
    @FXML
    public ColorPicker colorPickerBackground;
    @FXML
    public TextField speedTextField;
    public Button saveStateButton;
    public Button loadStateButton;
    public Button configureControlsButton;

    private Memory memory;
    private Emulator emulator;
    /* The speed of the emulation */
    private double speed = 1;

    private UnaryOperator<TextFormatter.Change> doubleFilter;

    private FileChooser romFileChooser;
    private FileChooser saveFileChooser;
    private FileChooser loadFileChooser;
    private SaveStateHandler saveStateHandler;

    private Window ownerWindow;
    private File romFile;
    private RomLoader romLoader;
    private Runnable threadRunner;
    private ResizableCanvas resizableCanvas;

    private Logger logger = Logger.getLogger(MainController.class.getName());

    public void loadRom(ActionEvent actionEvent) {
        ownerWindow = romLoaderButton.getScene().getWindow();
        romFile = romFileChooser.showOpenDialog(ownerWindow);

        if(romFile != null) {
            /* Stop last threadRunner */
            emulator.blackBox.getCentralProcessingUnit().stopCPU();
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                logger.severe(e.getMessage());
                Thread.currentThread().interrupt();
            }
            /* Clear memory and load in new ROM */
            emulator.blackBox.getCentralProcessingUnit().clearMemory();
            emulator.blackBox.getCentralProcessingUnit().reset();
            romLoader.loadRom(romFile, memory);
            /* Start CPU */
            emulator.blackBox.getCentralProcessingUnit().startCPU();
            threadRunner.run();
        }
    }

    public void setColorSprite(ActionEvent actionEvent) {
        resizableCanvas.setPaintOn(colorPickerSprite.getValue());
        /* Leave Focus again */
        colorPickerSprite.getParent().requestFocus();
    }

    public void setColorBackground(ActionEvent actionEvent) {
        resizableCanvas.setPaintOff(colorPickerBackground.getValue());
        /* Leave Focus again */
        colorPickerBackground.getParent().requestFocus();
    }

    public void setup(Runnable threadRunner, Emulator emulator, ResizableCanvas resizableCanvas){
        setupFileChoosers();
        saveStateHandler = new SaveStateHandler();

        romLoader = new RomLoader();

        this.threadRunner = threadRunner;
        this.emulator = emulator;
        this.memory = emulator.blackBox.getCentralProcessingUnit().getMemory();
        this.resizableCanvas = resizableCanvas;

        colorPickerSprite.setPromptText("Set sprite color");
        colorPickerBackground.setPromptText("Set background color");

        /* Setup textfield | source: gist.github.com/karimsqualli96/f8d4c2995da8e11496ed */
        doubleFilter = (TextFormatter.Change change) -> {
            if (change.isReplaced() && change.getText().matches("[^0-9]"))
                    change.setText(change.getControlText().substring(change.getRangeStart(), change.getRangeEnd()));

            if (change.isAdded()) {
                if (change.getControlText().contains(".")) {
                    if (change.getText().matches("[^0-9]"))     change.setText("");
                } else if (change.getText().matches("[^0-9.]")) change.setText("");
            }

            return change;
        };
        speedTextField.setTextFormatter(new TextFormatter<>(doubleFilter));
    }

    public void startEmulation(){
        threadRunner.run();
    }

    private void setupFileChoosers(){
        Path currentPath = Paths.get("").toAbsolutePath();

        /* Setup RomFileChooser */
        romFileChooser = new FileChooser();
        romFileChooser.setTitle("Choose a ROM to load");

        /* Setup SaveFileChooser */
        saveFileChooser = new FileChooser();
        saveFileChooser.setInitialFileName("savestate.c8s");
        saveFileChooser.setTitle("Save memory");
        saveFileChooser.setInitialDirectory(currentPath.toFile());

        /* Setup SaveFileChooser */
        loadFileChooser = new FileChooser();
        loadFileChooser.setTitle("Load memory");
        saveFileChooser.setInitialDirectory(currentPath.toFile());
    }

    public void setSpeed(ActionEvent actionEvent) {
        this.speed = Double.parseDouble(speedTextField.getText());
        /* Leave Focus again */
        speedTextField.getParent().requestFocus();
        emulator.blackBox.getCentralProcessingUnit().changeTimerSpeed(speed);
    }

    public double getSpeed(){
        return speed;
    }

    public void saveState(ActionEvent actionEvent) throws IOException {
        emulator.blackBox.getCentralProcessingUnit().setPause(true);

        File saveFile = saveFileChooser.showSaveDialog(ownerWindow);
        if(saveFile != null) saveStateHandler.writeState(saveFile, emulator.createSaveState());

        emulator.blackBox.getCentralProcessingUnit().setPause(false);
    }

    public void loadState(ActionEvent actionEvent) {
        SaveState saveState = null;
        File loadFile = loadFileChooser.showOpenDialog(ownerWindow);

        if(loadFile != null) {
            try {
                saveState = saveStateHandler.readState(loadFile);
            } catch (FailedReadingStateException e) {
                logger.severe(e.getMessage());
            }
        }

        emulator.setState(saveState);
    }

    public void configureControls(ActionEvent actionEvent) {
        /* TODO */
    }
}
