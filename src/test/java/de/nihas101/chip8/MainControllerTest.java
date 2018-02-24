package de.nihas101.chip8;

import de.nihas101.chip8.hardware.Emulator;
import de.nihas101.chip8.hardware.memory.ScreenMemory;
import de.nihas101.chip8.utils.ResizableCanvas;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.TestCase.assertEquals;

public class MainControllerTest {
    @Test
    public void startEmulation() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        MainTest mainTest = new MainTest();
        mainTest.testInit();

        MainController mainController = new MainController();
        ResizableCanvas resizableCanvas = new ResizableCanvas(new ScreenMemory());

        mainController.setup(() -> atomicBoolean.set(true), mainTest, resizableCanvas);

        mainController.startEmulation();

        assertEquals(true, atomicBoolean.get());
    }

    class MainTest extends Main {
        void testInit() {
            this.emulator = Emulator.createEmulator();
        }
    }
}