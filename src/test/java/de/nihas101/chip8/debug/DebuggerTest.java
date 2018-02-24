package de.nihas101.chip8.debug;

import de.nihas101.chip8.hardware.memory.Memory;
import org.junit.Test;

import static org.junit.Assert.*;

public class DebuggerTest {
    @Test
    public void isDebugging() {
        Debugger debugger = new Debugger();
        debugger.setDebuggable(new Memory());

        assertEquals(true, debugger.isDebugging());

        debugger.stop();
        assertEquals(false, debugger.isDebugging());
    }
}