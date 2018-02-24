package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;
import org.junit.Test;

import static org.junit.Assert.*;

public class RegistersTest {

    @Test
    public void peek() {
        Registers registers = new Registers();
        registers.poke(0, new UnsignedByte(1));

        assertEquals(1, registers.peek(0).unsignedDataType);
    }

    @Test
    public void poke() {
        Registers registers = new Registers();
        registers.poke(10, new UnsignedByte(123));

        assertEquals(123, registers.peek(10).unsignedDataType);
    }

    @Test
    public void clear() {
        Registers registers = new Registers();
        registers.poke(10, new UnsignedByte(123));

        registers.clear();

        assertEquals(0, registers.peek(10).unsignedDataType);
    }

    @Test
    public void getState() {
        assertEquals("V0: 0   V1: 0   V2: 0   V3: 0   V4: 0   V5: 0   V6: 0   V7: 0\n"
                + "V8: 0   V9: 0   VA: 0   VB: 0   VC: 0   VD: 0   VE: 0   VF: 0", new Registers().getState());
    }

    @Test
    public void getValues() {
        assertEquals("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]", new Registers().getValues());
    }
}