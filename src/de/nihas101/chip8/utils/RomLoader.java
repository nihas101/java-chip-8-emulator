package de.nihas101.chip8.utils;

import de.nihas101.chip8.hardware.memory.Chip8Memory;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;

import java.io.*;

import static de.nihas101.chip8.utils.Constants.MEMORY_LENGTH;
import static de.nihas101.chip8.utils.Constants.PROGRAM_COUNTER_START;

/**
 * Loads ROMs from a hard drive
 */
public class RomLoader {
    /**
     * Loads a ROM into memory
     * @param romFile The file of the ROM
     * @param memory The memory to load the ROM into
     */
    public void loadRom(File romFile, Chip8Memory memory) {
        System.out.println(romFile.toPath().toString());
        //File romFile = new File(romPath);
        InputStream inputStream = null;
        byte[] loadedRom = new byte[MEMORY_LENGTH - PROGRAM_COUNTER_START];
        int readBytes = -1;

        try {
            inputStream = new FileInputStream(romFile);
            readBytes = inputStream.read(loadedRom);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFileStream(inputStream);
        }

        if(readBytes > 0) loadIntoMemory(memory, loadedRom);
        else System.out.println("No bytes were read");
    }

    /**
     * Closes the {@link InputStream}
     * @param inputStream The {@link InputStream} to close
     */
    private void closeFileStream(InputStream inputStream){
        if (inputStream != null) {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads a ROM into memory
     * @param memory The memory to load the rom into
     * @param loadedRom The ROM to load
     */
    private void loadIntoMemory(Chip8Memory memory, byte[] loadedRom){
        int MAX_ROM_LENGTH = MEMORY_LENGTH - PROGRAM_COUNTER_START;
        for(int i = 0 ; i < MAX_ROM_LENGTH ; i++){
            memory.write(PROGRAM_COUNTER_START+i, new UnsignedByte(loadedRom[i]));
        }
    }
}
