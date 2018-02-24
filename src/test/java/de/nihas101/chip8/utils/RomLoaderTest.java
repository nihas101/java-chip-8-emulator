package de.nihas101.chip8.utils;

import de.nihas101.chip8.hardware.memory.Memory;
import org.junit.Test;

import java.io.File;

import static de.nihas101.chip8.utils.Constants.PROGRAM_COUNTER_START;
import static org.junit.Assert.assertEquals;

public class RomLoaderTest {

    @Test
    public void loadRom() {
        Memory memory = new Memory();
        RomLoader romLoader = new RomLoader();
        romLoader.loadRom(new File("src/test/resources/romloader"), memory);

        assertEquals("1".charAt(0), memory.read(PROGRAM_COUNTER_START).unsignedDataType);
        assertEquals("2".charAt(0), memory.read(PROGRAM_COUNTER_START + 1).unsignedDataType);
        assertEquals("3".charAt(0), memory.read(PROGRAM_COUNTER_START + 2).unsignedDataType);
        assertEquals("4".charAt(0), memory.read(PROGRAM_COUNTER_START + 3).unsignedDataType);
        assertEquals("5".charAt(0), memory.read(PROGRAM_COUNTER_START + 4).unsignedDataType);
        assertEquals("6".charAt(0), memory.read(PROGRAM_COUNTER_START + 5).unsignedDataType);
        assertEquals("7".charAt(0), memory.read(PROGRAM_COUNTER_START + 6).unsignedDataType);
        assertEquals("8".charAt(0), memory.read(PROGRAM_COUNTER_START + 7).unsignedDataType);
        assertEquals("9".charAt(0), memory.read(PROGRAM_COUNTER_START + 8).unsignedDataType);
        assertEquals(0, memory.read(PROGRAM_COUNTER_START + 9).unsignedDataType);
    }
}