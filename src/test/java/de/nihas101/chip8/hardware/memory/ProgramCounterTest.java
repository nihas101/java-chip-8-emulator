package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.unsignedDataTypes.UnsignedShort;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProgramCounterTest {

    @Test
    public void incrementCounter() {
        ProgramCounter programCounter = new ProgramCounter();

        programCounter.incrementCounter();
        programCounter.incrementCounter();

        assertEquals(2, programCounter.getCounter().unsignedDataType);
    }

    @Test
    public void incrementCounterN() {
        ProgramCounter programCounter = new ProgramCounter();

        programCounter.incrementCounterN(10);

        assertEquals(10, programCounter.getCounter().unsignedDataType);
    }

    @Test
    public void getCounter() {
        assertEquals(0, new ProgramCounter().getCounter().unsignedDataType);
    }

    @Test
    public void getState() {
        assertEquals("PC:\t0", new ProgramCounter().getState());
    }

    @Test
    public void jumpTo() {
        ProgramCounter programCounter = new ProgramCounter();

        programCounter.jumpTo(new UnsignedShort(200));

        assertEquals(200, programCounter.getCounter().unsignedDataType);
    }
}