package de.nihas101.chip8;

import de.nihas101.chip8.hardware.Chip8CentralProcessingUnit;
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
    private Chip8CentralProcessingUnit cpu;

    private boolean firstLoad = true;

    private FileChooser fileChooser;
    private Window ownerWindow;
    private File romFile;
    private RomLoader romLoader;
    private Runnable start;
    private ResizableCanvas resizableCanvas;

    public void loadRom(ActionEvent actionEvent) {
        ownerWindow = romLoaderButton.getScene().getWindow();
        romFile = fileChooser.showOpenDialog(ownerWindow);

        if(romFile != null) {
            cpu.stopCPU();
            cpu.clearMemory();
            cpu.reset();
            romLoader.loadRom(romFile, memory);
            cpu.continueCPU();
            start.run();
        }
    }

    public void setColorSprite(ActionEvent actionEvent) {
        resizableCanvas.setPaintOn(colorPickerSprite.getValue());
    }

    public void setColorBackground(ActionEvent actionEvent) {
        resizableCanvas.setPaintOff(colorPickerBackground.getValue());
    }

    public void setup(Runnable start, Chip8CentralProcessingUnit cpu, ResizableCanvas resizableCanvas){
        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a ROM to load");

        romLoader = new RomLoader();

        this.start = start;
        this.cpu = cpu;
        this.memory = cpu.getMemory();
        this.resizableCanvas = resizableCanvas;

        colorPickerSprite.setPromptText("Set sprite color");
        colorPickerBackground.setPromptText("Set background color");
    }
}
