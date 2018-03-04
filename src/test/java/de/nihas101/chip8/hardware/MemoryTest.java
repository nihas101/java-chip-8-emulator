package de.nihas101.chip8.hardware;

import de.nihas101.chip8.hardware.memory.Memory;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;
import org.junit.Test;

import static de.nihas101.chip8.utils.Constants.MEMORY_LENGTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class MemoryTest {
    @Test
    public void testPokePeek1() {
        Memory memory = new Memory();
        memory.write(0, new UnsignedByte(255));

        assertEquals(new UnsignedByte(255), memory.read(0));
    }

    @Test
    public void testPokePeek2() {
        Memory memory = new Memory();
        memory.write(255, new UnsignedByte(5));

        assertEquals(new UnsignedByte(5), memory.read(255));
    }

    @Test
    public void testPokePeek3() {
        Memory memory = new Memory();
        memory.write(232, new UnsignedByte(25));

        assertEquals(new UnsignedByte(25), memory.read(232));
    }

    @Test
    public void testPokePeek4() {
        Memory memory = new Memory();
        memory.write(10, new UnsignedByte(12));

        assertEquals(new UnsignedByte(12), memory.read(10));
    }

    @Test
    public void testPokeNeg() {
        Memory memory = new Memory();
        memory.write(0, new UnsignedByte(-1));

        assertEquals(255, memory.read(0).unsignedDataType);
    }

    @Test
    public void testPoke() {
        Memory memory = new Memory();
        memory.write(0, new UnsignedByte(256));

        assertEquals(0, memory.read(0).unsignedDataType);
    }

    @Test
    public void testIndexOutOfBoundsPokeNeg() {
        Memory memory = new Memory();
        try {
            memory.write(-1, new UnsignedByte(255));
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        fail("No Exception was thrown");
    }

    @Test
    public void testIndexOutOfBoundsPoke() {
        Memory memory = new Memory();
        try {
            memory.write(MEMORY_LENGTH, new UnsignedByte((byte) 255));
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        fail("No Exception was thrown");
    }

    @Test
    public void testIndexOutOfBoundsPeekNeg() {
        Memory memory = new Memory();
        try {
            memory.read(-1);
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        fail("No Exception was thrown");
    }

    @Test
    public void testIndexOutOfBoundsPeek() {
        Memory memory = new Memory();
        try {
            memory.read(MEMORY_LENGTH);
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        fail("No Exception was thrown");
    }

    @Test
    public void getState() {
        Memory memory = new Memory();
        Memory memory1 = new Memory();

        memory.write(0x205, new UnsignedByte(10));
        memory1.write(0x205, new UnsignedByte(10));

        memory.write(0x206, new UnsignedByte(11));
        memory1.write(0x206, new UnsignedByte(11));

        memory.write(0x205, new UnsignedByte(12));
        memory1.write(0x205, new UnsignedByte(12));

        memory.write(0x206, new UnsignedByte(110));
        memory1.write(0x206, new UnsignedByte(110));

        memory.write(0x207, new UnsignedByte(120));
        memory1.write(0x207, new UnsignedByte(120));

        memory.write(0x208, new UnsignedByte(20));
        memory1.write(0x208, new UnsignedByte(20));


        assertEquals(memory1.getState(), memory.getState());
    }
}