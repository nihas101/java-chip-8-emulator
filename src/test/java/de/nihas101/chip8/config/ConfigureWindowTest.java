package de.nihas101.chip8.config;

import de.nihas101.chip8.hardware.Emulator;
import de.nihas101.chip8.utils.keyConfiguration.KeyConfiguration;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static javafx.application.Platform.runLater;
import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.DIGIT1;
import static javafx.scene.input.KeyCode.Y;
import static org.junit.Assert.assertEquals;

public class ConfigureWindowTest extends ApplicationTest {
    private KeyConfiguration keyConfiguration;

    @Override
    public void start(Stage primaryStage) {
        Emulator emulator = Emulator.createEmulator();
        emulator.setKeyConfiguration(KeyConfiguration.createKeyConfiguration(emulator));
        runLater(() -> keyConfiguration = ConfigureWindow.configureControls(emulator));
    }

    @Test
    public void configureControls() {
        clickOn("#key0TextField");
        push(Y);
        closeCurrentWindow();
        assertEquals("0", keyConfiguration.getOrNOP(Y).getKeyName());
    }

    @Test
    public void configureControlsReset() {
        clickOn("#key0TextField");
        push(Y);
        clickOn("#resetButton");
        closeCurrentWindow();
        assertEquals("NOP", keyConfiguration.getOrNOP(Y).getKeyName());
        assertEquals("1", keyConfiguration.getOrNOP(DIGIT1).getKeyName());
    }

    @Test
    public void configureControlsAlreadyAssigned() {
        clickOn("#key0TextField");
        push(A);
        closeCurrentWindow();
        assertEquals("1", keyConfiguration.getOrNOP(DIGIT1).getKeyName());
        assertEquals("7", keyConfiguration.getOrNOP(A).getKeyName());
    }
}