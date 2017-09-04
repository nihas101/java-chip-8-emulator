package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.debug.Debuggable;

/**
 * A class used to represent a screen of a Chip-8
 */
public class ScreenMemory implements Debuggable {
    public static final int SCREEN_WIDTH = 64;
    public static final int SCREEN_HEIGHT = 32;
    private volatile boolean[][] memory = new boolean[SCREEN_WIDTH][SCREEN_HEIGHT];

    public ScreenMemory() {
        reset();
    }

    public boolean[][] getMemory(){
        return memory;
    }

    /**
     * Reads the memory at the specified location
     * @param x The horizontal index
     * @param y The lateral index
     * @return The read value
     */
    public boolean read(int x, int y) {
        /* Make sure the values are in range */
        x = x%SCREEN_WIDTH;
        y = y%SCREEN_HEIGHT;

        return memory[x][y];
    }

    /**
     * Writes to the memory at the specified location
     * @param x The horizontal index
     * @param y The lateral index
     * @param bool The boolean to write
     */
    public void write(int x, int y, boolean bool) {
        x = x%SCREEN_WIDTH;
        y = y%SCREEN_HEIGHT;

        memory[x][y] = bool;
    }

    /**
     * Resets the screen memory
     */
    public void reset(){
        for(int x=0 ; x < SCREEN_WIDTH ; x++)
            for (int y = 0; y < SCREEN_HEIGHT; y++)
                memory[x][y] = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return "\nScreenMemory:\n"
                + this.toString()
                + "\n";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0 ; i < SCREEN_HEIGHT ; i++) {
            for (int j = 0; j < SCREEN_WIDTH; j++) {
                if(memory[j][i])
                    stringBuilder.append(" 1 ");
                else
                    stringBuilder.append(" 0 ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
