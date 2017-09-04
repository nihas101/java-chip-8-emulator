package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.debug.Debuggable;
import de.nihas101.chip8.unsignedDataTypes.UnsignedShort;

/**
 * A class representing a Program Counter of a Chip-8
 */
public class Chip8ProgramCounter implements Debuggable {
    /**
     * The 16-bit program counter
     */
    private UnsignedShort count;

    public Chip8ProgramCounter(UnsignedShort count){
        this.count = count;
    }

    public Chip8ProgramCounter(){
        this.count = new UnsignedShort((short) 0);
    }

    /**
     * Increments the PC by one
     */
    public void incrementCounter(){
        this.count = new UnsignedShort((short) (this.count.unsignedDataType + 1));
    }

    /**
     * Increments the PC by N
     * @param N The value by which to increment the counter by
     */
    public void incrementCounterN(int N){
        this.count = new UnsignedShort((short) (this.count.unsignedDataType + N));
    }

    public UnsignedShort getCounter(){
        return this.count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return "PC:\t" + Integer.toHexString(count.unsignedDataType);
    }

    /**
     * Sets the PC to the provided address
     * @param address The new address the PC should point to
     */
    public void jumpTo(UnsignedShort address) {
        count = address;
    }
}
