package de.nihas101.chip8.utils;

import de.nihas101.chip8.hardware.memory.Memory;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import static de.nihas101.chip8.utils.Constants.MEMORY_LENGTH;
import static de.nihas101.chip8.utils.Constants.PROGRAM_COUNTER_START;

/**
 * Loads ROMs from a hard drive
 */
public class RomLoader {

    private Logger logger = Logger.getLogger(RomLoader.class.getName());

    /**
     * Loads a ROM into memory
     *
     * @param romFile The file of the ROM
     * @param memory  The memory to load the ROM into
     */
    public void loadRom(File romFile, Memory memory) {
        InputStream inputStream = null;
        byte[] loadedRom = new byte[MEMORY_LENGTH - PROGRAM_COUNTER_START];
        int readBytes = -1;

        try {
            inputStream = new FileInputStream(romFile);
            readBytes = inputStream.read(loadedRom);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        } finally {
            closeFileStream(inputStream);
        }

        if (readBytes > 0) loadIntoMemory(memory, loadedRom);
        else logger.info("No bytes were read");
    }

    /**
     * Closes the {@link InputStream}
     *
     * @param inputStream The {@link InputStream} to close
     */
    private void closeFileStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    /**
     * Loads a ROM into memory
     *
     * @param memory    The memory to load the rom into
     * @param loadedRom The ROM to load
     */
    private void loadIntoMemory(Memory memory, byte[] loadedRom) {
        int MAX_ROM_LENGTH = MEMORY_LENGTH - PROGRAM_COUNTER_START;
        for (int i = 0; i < MAX_ROM_LENGTH; i++) {
            memory.write(PROGRAM_COUNTER_START + i, new UnsignedByte(loadedRom[i]));
        }
    }
}
