package de.nihas101.chip8.hardware.memory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScreenMemoryTest {

    @Test
    public void getMemory() {
        ScreenMemory screenMemory = new ScreenMemory();
        for (boolean[] booleans : screenMemory.getMemory())
            for (boolean aBoolean : booleans)
                assertEquals(false, aBoolean);
    }

    @Test
    public void read() {
        ScreenMemory screenMemory = new ScreenMemory();
        boolean readBoolean = screenMemory.read(0, 0);

        assertEquals(false, readBoolean);
    }

    @Test
    public void write() {
        ScreenMemory screenMemory = new ScreenMemory();

        screenMemory.write(10, 10, true);
        boolean readBoolean = screenMemory.read(10, 10);

        assertEquals(true, readBoolean);
    }

    @Test
    public void reset() {
        ScreenMemory screenMemory = new ScreenMemory();

        screenMemory.write(10, 10, true);
        screenMemory.reset();
        boolean readBoolean = screenMemory.read(10, 10);

        assertEquals(false, readBoolean);
    }
}