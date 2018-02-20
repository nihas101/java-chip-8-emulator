package de.nihas101.chip8.hardware;

import de.nihas101.chip8.hardware.memory.Memory;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;
import org.junit.Test;

import static de.nihas101.chip8.utils.Constants.MEMORY_LENGTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class MemoryTest {
    @Test
    public void testPokePeek() {
        Memory memory = new Memory();
        memory.write(0, new UnsignedByte((byte) 255));
        memory.write(255, new UnsignedByte((byte) 5));
        memory.write(232, new UnsignedByte((byte) 25));
        memory.write(10, new UnsignedByte((byte) 12));

        assertEquals(new UnsignedByte((byte) 255), memory.read(0));
        assertEquals(new UnsignedByte((byte) 5), memory.read(255));
        assertEquals(new UnsignedByte((byte) 25), memory.read(232));
        assertEquals(new UnsignedByte((byte) 12), memory.read(10));
    }

    @Test
    public void testPokeNeg() {
        Memory memory = new Memory();
        memory.write(0, new UnsignedByte((byte) -1));

        assertEquals(255, memory.read(0).unsignedDataType);
    }

    @Test
    public void testPoke() {
        Memory memory = new Memory();
        memory.write(0, new UnsignedByte((byte) 256));

        assertEquals(0, memory.read(0).unsignedDataType);
    }

    @Test
    public void testIndexOutOfBoundsPokeNeg() {
        Memory memory = new Memory();
        try {
            memory.write(-1, new UnsignedByte((byte) 255));
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
}