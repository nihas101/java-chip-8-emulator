package de.nihas101.chip8;

import de.nihas101.chip8.hardware.memory.Chip8Memory;
import de.nihas101.chip8.utils.ResizableCanvas;
import de.nihas101.chip8.utils.RomLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class MainController {
    @FXML
    public Button romLoaderButton;
    @FXML
    public ColorPicker colorPickerSprite;
    @FXML
    public ColorPicker colorPickerBackground;

    private Chip8Memory memory;
    private FileChooser fileChooser;
    private Window ownerWindow;
    private File romFile;
    private RomLoader romLoader;
    private Runnable runnable;
    private ResizableCanvas resizableCanvas;

    public void loadRom(ActionEvent actionEvent) {
        ownerWindow = romLoaderButton.getScene().getWindow();
        romFile = fileChooser.showOpenDialog(ownerWindow);
        romLoader.loadRom(romFile, memory);
        runnable.run();
    }

    public void setColorSprite(ActionEvent actionEvent) {
        resizableCanvas.setPaintOn(colorPickerSprite.getValue());
    }

    public void setColorBackground(ActionEvent actionEvent) {
        resizableCanvas.setPaintOff(colorPickerBackground.getValue());
    }

    public void setup(Runnable runnable, Chip8Memory memory, ResizableCanvas resizableCanvas){
        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a ROM to load");

        romLoader = new RomLoader();
        this.runnable = runnable;
        this.memory = memory;
        this.resizableCanvas = resizableCanvas;

        colorPickerSprite.setPromptText("Set sprite color");
        colorPickerBackground.setPromptText("Set background color");
    }
}
