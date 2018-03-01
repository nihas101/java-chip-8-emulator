package de.nihas101.chip8;

import de.nihas101.chip8.config.ConfigureWindow;
import de.nihas101.chip8.hardware.memory.Memory;
import de.nihas101.chip8.savestates.FailedReadingStateException;
import de.nihas101.chip8.savestates.SaveState;
import de.nihas101.chip8.savestates.SaveStateHandler;
import de.nihas101.chip8.utils.ResizableCanvas;
import de.nihas101.chip8.utils.RomLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

import static java.lang.Double.parseDouble;

public final class MainController {
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
    private Main main;
    /* The speed of the emulation */
    private double speed = 1;

    private UnaryOperator<Change> doubleFilter;

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
        main.emulator.getCentralProcessingUnit().setPause(true);
        ownerWindow = romLoaderButton.getScene().getWindow();
        romFile = romFileChooser.showOpenDialog(ownerWindow);
        main.emulator.getCentralProcessingUnit().setPause(false);

        if (romFile != null) {
            /* Stop last threadRunner */
            main.emulator.getCentralProcessingUnit().setStop(true);
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                logger.severe(e.getMessage());
                Thread.currentThread().interrupt();
            }
            /* Clear memory and load in new ROM */
            main.emulator.getCentralProcessingUnit().clearMemory();
            main.emulator.getCentralProcessingUnit().reset();
            romLoader.loadRom(romFile, memory);
            /* Start CPU */
            main.emulator.getCentralProcessingUnit().setStop(false);
            threadRunner.run();
        }

        actionEvent.consume();
    }

    public void setColorSprite(ActionEvent actionEvent) {
        resizableCanvas.setPaintOn(colorPickerSprite.getValue());
        /* Leave Focus again */
        colorPickerSprite.getParent().requestFocus();

        actionEvent.consume();
    }

    public void setColorBackground(ActionEvent actionEvent) {
        resizableCanvas.setPaintOff(colorPickerBackground.getValue());
        /* Leave Focus again */
        colorPickerBackground.getParent().requestFocus();

        actionEvent.consume();
    }

    public void setup(Runnable threadRunner, Main main, ResizableCanvas resizableCanvas) {
        setupFileChoosers();
        saveStateHandler = new SaveStateHandler();

        romLoader = new RomLoader();

        this.threadRunner = threadRunner;
        this.main = main;
        this.memory = main.emulator.getCentralProcessingUnit().getMemory();
        this.resizableCanvas = resizableCanvas;

        if (speedTextField != null) {
            doubleFilter = createDoubleFilter();
            speedTextField.setTextFormatter(new TextFormatter<>(doubleFilter));
        }
    }

    private UnaryOperator<Change> createDoubleFilter() {
        /* Source: gist.github.com/karimsqualli96/f8d4c2995da8e11496ed */
        return change -> {
            replaced(change);
            changed(change);

            return change;
        };
    }

    private void replaced(Change change) {
        if (change.isReplaced() && change.getText().matches("[^0-9]"))
            change.setText(change.getControlText().substring(change.getRangeStart(), change.getRangeEnd()));
    }

    private void changed(Change change) {
        if (change.isAdded()) {
            if (change.getControlText().contains(".")) {
                if (change.getText().matches("[^0-9]")) change.setText("");
            } else if (change.getText().matches("[^0-9.]")) change.setText("");
        }
    }

    public void startEmulation() {
        threadRunner.run();
    }

    private void setupFileChoosers() {
        Path currentPath = Paths.get("").toAbsolutePath();

        /* Setup RomFileChooser */
        romFileChooser = new FileChooser();
        romFileChooser.setTitle("Choose a ROM to load");

        /* Setup SaveFileChooser */
        saveFileChooser = new FileChooser();
        saveFileChooser.setInitialFileName("savestate.c8s");
        saveFileChooser.setTitle("Save state");
        saveFileChooser.setInitialDirectory(currentPath.toFile());

        /* Setup LoadFileChooser */
        loadFileChooser = new FileChooser();
        loadFileChooser.setTitle("Load state");
        saveFileChooser.setInitialDirectory(currentPath.toFile());
    }

    public void setSpeed(ActionEvent actionEvent) {
        if (parseDouble(speedTextField.getText()) > 0) {
            this.speed = parseDouble(speedTextField.getText());
            /* Leave Focus again */
            speedTextField.getParent().requestFocus();
            main.emulator.getCentralProcessingUnit().changeTimerSpeed(speed);
        }

        actionEvent.consume();
    }

    public double getSpeed() {
        return speed;
    }

    public void saveState(ActionEvent actionEvent) throws IOException {
        main.emulator.getCentralProcessingUnit().setPause(true);

        File saveFile = saveFileChooser.showSaveDialog(ownerWindow);
        if (saveFile != null) saveStateHandler.writeState(saveFile, main.createSaveState());

        main.emulator.getCentralProcessingUnit().setPause(false);
        actionEvent.consume();
    }

    public void loadState(ActionEvent actionEvent) {
        SaveState saveState = null;
        File loadFile = loadFileChooser.showOpenDialog(ownerWindow);

        if (loadFile != null) {
            try {
                saveState = saveStateHandler.readState(loadFile);
            } catch (FailedReadingStateException e) {
                logger.severe(e.getMessage());
            }
            main.setState(saveState);
        }

        actionEvent.consume();
    }

    public void openControlConfigurationWindow(ActionEvent actionEvent) {
        main.emulator.getCentralProcessingUnit().setPause(true);
        main.emulator.setKeyConfiguration(ConfigureWindow.configureControls(main.emulator));
        main.emulator.getCentralProcessingUnit().setPause(false);

        actionEvent.consume();
    }
}
