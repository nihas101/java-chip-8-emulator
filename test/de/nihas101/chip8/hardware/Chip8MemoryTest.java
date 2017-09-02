package de.nihas101.chip8.hardware;

import de.nihas101.chip8.hardware.memory.Chip8Memory;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;
import org.junit.jupiter.api.Test;

import static de.nihas101.chip8.utils.Constants.MEMORY_LENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class Chip8MemoryTest {
    @Test
    void testPokePeek(){
        Chip8Memory memory = new Chip8Memory();
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
    void testPokeNeg(){
        Chip8Memory memory = new Chip8Memory();
        memory.write(0, new UnsignedByte((byte) -1));

        assertEquals(255, memory.read(0).unsignedDataType);
    }

    @Test
    void testPoke(){
        Chip8Memory memory = new Chip8Memory();
        memory.write(0, new UnsignedByte((byte) 256));

        assertEquals(0, memory.read(0).unsignedDataType);
    }

    @Test
    void testIndexOutOfBoundsPokeNeg(){
        Chip8Memory memory = new Chip8Memory();
        try {
            memory.write(-1, new UnsignedByte((byte) 255));
        }catch (IndexOutOfBoundsException e){
            return;
        }
        fail("No Exception was thrown");
    }

    @Test
    void testIndexOutOfBoundsPoke(){
        Chip8Memory memory = new Chip8Memory();
        try {
            memory.write(MEMORY_LENGTH, new UnsignedByte((byte) 255));
        }catch (IndexOutOfBoundsException e){
            return;
        }
        fail("No Exception was thrown");
    }

    @Test
    void testIndexOutOfBoundsPeekNeg(){
        Chip8Memory memory = new Chip8Memory();
        try {
            memory.read(-1);
        }catch (IndexOutOfBoundsException e){
            return;
        }
        fail("No Exception was thrown");
    }

    @Test
    void testIndexOutOfBoundsPeek(){
        Chip8Memory memory = new Chip8Memory();
        try {
            memory.read(MEMORY_LENGTH);
        }catch (IndexOutOfBoundsException e){
            return;
        }
        fail("No Exception was thrown");
    }
}