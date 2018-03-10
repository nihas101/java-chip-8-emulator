package de.nihas101.chip8;

import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static javafx.application.Platform.runLater;
import static javafx.scene.input.KeyCode.*;

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
    public void openConfiguration() throws InterruptedException {
        Thread.sleep(2000);
        clickOn(main.mainController.configureControlsButton);
        closeCurrentWindow();
    }
}