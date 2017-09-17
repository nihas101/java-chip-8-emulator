package de.nihas101.chip8;

import de.nihas101.chip8.hardware.Chip8CentralProcessingUnit;
import de.nihas101.chip8.hardware.memory.Chip8Memory;
import de.nihas101.chip8.utils.ResizableCanvas;
import de.nihas101.chip8.utils.RomLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.function.UnaryOperator;

public class MainController {
    @FXML
    public Button romLoaderButton;
    @FXML
    public ColorPicker colorPickerSprite;
    @FXML
    public ColorPicker colorPickerBackground;
    @FXML
    public TextField speedTextField;

    private Chip8Memory memory;
    private Chip8CentralProcessingUnit cpu;
    /* The speed of the emulation */
    private double speed = 1;

    UnaryOperator<TextFormatter.Change> doubleFilter;


    private FileChooser fileChooser;
    private Window ownerWindow;
    private File romFile;
    private RomLoader romLoader;
    private Runnable threadRunner;
    private ResizableCanvas resizableCanvas;

    public void loadRom(ActionEvent actionEvent) {
        ownerWindow = romLoaderButton.getScene().getWindow();
        romFile = fileChooser.showOpenDialog(ownerWindow);

        if(romFile != null) {
            /* Stop last threadRunner */
            cpu.stopCPU();
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            /* Clear memory and load in new ROM */
            cpu.clearMemory();
            cpu.reset();
            romLoader.loadRom(romFile, memory);
            /* Start CPU */
            cpu.startCPU();
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

    public void setup(Runnable threadRunner, Chip8CentralProcessingUnit cpu, ResizableCanvas resizableCanvas){
        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a ROM to load");

        romLoader = new RomLoader();

        this.threadRunner = threadRunner;
        this.cpu = cpu;
        this.memory = cpu.getMemory();
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

    public void setSpeed(ActionEvent actionEvent) {
        this.speed = Double.parseDouble(speedTextField.getText());
        /* Leave Focus again */
        speedTextField.getParent().requestFocus();
        cpu.changeTimerSpeed(speed);
    }

    public double getSpeed(){
        return speed;
    }
}
