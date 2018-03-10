package de.nihas101.chip8.debug;

import de.nihas101.chip8.hardware.Emulator;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static javafx.application.Platform.runLater;
import static org.junit.Assert.assertEquals;

public class DebuggerTest extends ApplicationTest {
    private Debugger debugger;

    @Override
    public void start(Stage primaryStage) {
        debugger = new Debugger();
    }

    @Test
    public void setDebuggable() throws InterruptedException {
        setupDebugger();
        Thread.sleep(2000);
        assertEquals(true, debugger.isDebugging());
        closeCurrentWindow();
    }

    @Test
    public void setStepByStep() throws InterruptedException {
        setupDebugger();
        Thread.sleep(2000);
        debugger.setStepByStep(true);

        assertEquals(true, debugger.isDebugging());
        closeCurrentWindow();
    }

    @Test
    public void stop() {
        setupDebugger();

        assertDebuggerStops();
    }

    private void assertDebuggerStops() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        runLater(() -> {
            debugger.stop();
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(false, debugger.isDebugging());
    }

    private void setupDebugger() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        runLater(() -> {
            try {
                debugger.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Emulator emulator = Emulator.createEmulator();
            debugger.setDebuggable(emulator);
            emulator.executeCPUCycles(1);
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}