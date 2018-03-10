package de.nihas101.chip8;

import javafx.geometry.Bounds;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static javafx.application.Platform.runLater;
import static javafx.scene.input.KeyCode.*;
import static junit.framework.TestCase.assertEquals;

public class MainTest extends ApplicationTest {
    private Main main;

    @After
    public void closeWindow() {
        closeCurrentWindow();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        main = new Main();
        main.start(new Stage());
    }

    @Test
    public void loadCancelTest() throws InterruptedException {
        Thread.sleep(2000);
        clickOn(main.mainController.romLoaderButton);
        closeCurrentWindow();
    }

    @Test
    public void setSpriteColorTest() throws InterruptedException {
        Thread.sleep(2000);
        clickOn(main.mainController.colorPickerSprite);
        double[] colorCoordinates = colorCoordinates(main.mainController.colorPickerSprite);
        clickOn(colorCoordinates[0], colorCoordinates[1]);
        Thread.sleep(1000);
        assertEquals("0x999999ff", main.canvas.getPaintOn().toString());
    }

    @Test
    public void setBackgroundColorTest() throws InterruptedException {
        Thread.sleep(2000);
        clickOn(main.mainController.colorPickerBackground);
        double[] colorCoordinates = colorCoordinates(main.mainController.colorPickerBackground);
        clickOn(colorCoordinates[0], colorCoordinates[1]);
        Thread.sleep(1000);
        assertEquals("0x999999ff", main.canvas.getPaintOff().toString());
    }

    private double[] colorCoordinates(ColorPicker colorPicker) {
        Bounds bounds = colorPicker.localToScreen(colorPicker.getLayoutBounds());
        return new double[]{bounds.getMinX() + 100, bounds.getMinY() + 40};
    }

    @Test
    public void debugger() throws InterruptedException {
        Thread.sleep(2000);
        push(F1);
        runLater(() -> main.stop());
    }

    @Test
    public void saveShortCut() throws InterruptedException {
        Thread.sleep(2000);
        push(F5);
        closeCurrentWindow();
    }

    @Test
    public void loadShortCut() throws InterruptedException {
        Thread.sleep(2000);
        push(F6);
        closeCurrentWindow();
    }

    @Test
    public void setSpeed() throws InterruptedException {
        Thread.sleep(2000);
        clickOn(main.mainController.speedTextField);
        push(LEFT);
        push(LEFT);
        push(LEFT);
        push(DELETE);
        push(DELETE);
        push(DELETE);

        push(DIGIT2);
        push(KeyCode.PERIOD);
        push(DIGIT0);
        push(ENTER);
        assertEquals(2.0, main.mainController.getSpeed());
    }

    @Test
    public void openConfiguration() throws InterruptedException {
        Thread.sleep(2000);
        clickOn(main.mainController.configureControlsButton);
        closeCurrentWindow();
    }
}