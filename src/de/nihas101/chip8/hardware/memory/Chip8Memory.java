package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.debug.Debuggable;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;

import static de.nihas101.chip8.utils.Constants.MEMORY_LENGTH;

/**
 * A class representing the Memory of a Chip-8
 */
public class Chip8Memory implements Debuggable {
    /**
     * 0xFFF (4096) bytes of hardware
     */
    private UnsignedByte[] memory = new UnsignedByte[MEMORY_LENGTH];

    public Chip8Memory(){
        // Set Registers to 0
        for (int i=0 ; i < memory.length ; i++) memory[i] = new UnsignedByte((byte) 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return  "\nMemory:\n"
                + this.memoryToString()
                + "\n";
    }

    /**
     * Converts the contents of the memory into a {@link String}
     * @return The {@link String} representing the contents of the memory
     */
    private String memoryToString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0 ; i < memory.length ; i++)
            stringBuilder.append(i).append(": ").append(memory[i].unsignedDataType).append("\n");

        return stringBuilder.toString();
    }

    /**
     * Writes to the memory
     * @param index The index of the cell to write to
     * @param unsignedByte The value to write into memory
     */
    public void write(int index, UnsignedByte unsignedByte){
        memory[index] = unsignedByte;
    }

    /**
     * Reads a value from the memory
     * @param index The index of the cell to read from
     * @return The read value
     */
    public UnsignedByte read(int index){
        if(index > -1 && index < MEMORY_LENGTH) return memory[index];
        else throw new IndexOutOfBoundsException("Index " + index + " is out of bounds (0 - " + MEMORY_LENGTH + ")");
    }

    public void clear() {
        for (int i=0 ; i < memory.length ; i++) memory[i] = new UnsignedByte((byte) 0);
    }
}
