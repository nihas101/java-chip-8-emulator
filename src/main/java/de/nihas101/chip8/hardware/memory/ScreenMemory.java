package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.debug.Debuggable;

import java.util.Arrays;

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

    public ScreenMemory(String[] screenMemoryStrings) {
        for (int index = 0; index < screenMemoryStrings.length; index++) {
            fillScreen(
                    screenMemoryStrings[index]
                            .substring(1)
                            .split(", "),
                    index
            );
        }
    }

    private void fillScreen(String[] column, int index) {
        for (int j = 0; j < SCREEN_HEIGHT; j++) {
            memory[index][j] = Boolean.parseBoolean(column[j]);
        }
    }

    public boolean[][] getMemory() {
        return memory;
    }

    /**
     * Reads the memory at the specified location
     *
     * @param x The horizontal index
     * @param y The lateral index
     * @return The read value
     */
    public boolean read(int x, int y) {
        /* Make sure the values are in range */
        x = x % SCREEN_WIDTH;
        y = y % SCREEN_HEIGHT;

        return memory[x][y];
    }

    /**
     * Writes to the memory at the specified location
     *
     * @param x    The horizontal index
     * @param y    The lateral index
     * @param bool The boolean to write
     */
    public void write(int x, int y, boolean bool) {
        x = x % SCREEN_WIDTH;
        y = y % SCREEN_HEIGHT;

        memory[x][y] = bool;
    }

    /**
     * Resets the screen memory
     */
    public void reset() {
        for (int x = 0; x < SCREEN_WIDTH; x++)
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
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < SCREEN_HEIGHT; i++) columnToString(i, stringBuilder);
        return stringBuilder.toString();
    }

    private void columnToString(int row, StringBuilder stringBuilder) {
        for (int j = 0; j < SCREEN_WIDTH; j++) stringBuilder.append(" ").append(cellToString(j, row)).append(" ");
        stringBuilder.append("\n");
    }

    private String cellToString(int column, int row) {
        if (memory[column][row]) return "1";
        else return "0";
    }

    public String getValues() {
        return Arrays.deepToString(memory);
    }
}
