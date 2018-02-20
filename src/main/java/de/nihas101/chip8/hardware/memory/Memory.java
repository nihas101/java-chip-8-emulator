package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.debug.Debuggable;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;

import java.util.Arrays;

import static de.nihas101.chip8.utils.Constants.MEMORY_LENGTH;
import static java.lang.Integer.parseInt;

/**
 * A class representing the Memory of a Chip-8
 */
public class Memory implements Debuggable {
    /**
     * 0xFFF (4096) bytes of hardware
     */
    private UnsignedByte[] memory = new UnsignedByte[MEMORY_LENGTH];

    public Memory() {
        // Set Registers to 0
        for (int i = 0; i < memory.length; i++) memory[i] = new UnsignedByte((byte) 0);
        setupCharacterSprites();
    }

    public Memory(String[] memoryStrings) {
        for (int i = 0; i < memoryStrings.length; i++)
            memory[i] = new UnsignedByte((byte) parseInt(memoryStrings[i].trim()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return "\nMemory:\n"
                + this.memoryToString()
                + "\n";
    }

    /**
     * Converts the contents of the memory into a {@link String}
     *
     * @return The {@link String} representing the contents of the memory
     */
    private String memoryToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < memory.length; i++)
            stringBuilder.append(i).append(": ").append(memory[i].unsignedDataType).append("\n");

        return stringBuilder.toString();
    }

    /**
     * Writes to the memory
     *
     * @param index        The index of the cell to write to
     * @param unsignedByte The value to write into memory
     */
    public void write(int index, UnsignedByte unsignedByte) {
        memory[index] = unsignedByte;
    }

    /**
     * Reads a value from the memory
     *
     * @param index The index of the cell to read from
     * @return The read value
     */
    public UnsignedByte read(int index) {
        if (index > -1 && index < MEMORY_LENGTH) return memory[index];
        else throw new IndexOutOfBoundsException("Index " + index + " is out of bounds (0 - " + MEMORY_LENGTH + ")");
    }

    public void clear() {
        for (int i = 0x200; i < memory.length; i++) memory[i] = new UnsignedByte((byte) 0);
    }

    private void setupCharacterSprites() {
        new Thread(this::setup0).start();
        new Thread(this::setup1).start();
        new Thread(this::setup2).start();
        new Thread(this::setup3).start();
        new Thread(this::setup4).start();
        new Thread(this::setup5).start();
        new Thread(this::setup6).start();
        new Thread(this::setup7).start();
        new Thread(this::setup8).start();
        new Thread(this::setup9).start();
        new Thread(this::setupA).start();
        new Thread(this::setupB).start();
        new Thread(this::setupC).start();
        new Thread(this::setupD).start();
        new Thread(this::setupE).start();
        new Thread(this::setupF).start();
    }

    private void setup0() {
        this.memory[0] = new UnsignedByte((byte) 0b11110000); // 0
        this.memory[1] = new UnsignedByte((byte) 0b10010000);
        this.memory[2] = new UnsignedByte((byte) 0b10010000);
        this.memory[3] = new UnsignedByte((byte) 0b10010000);
        this.memory[4] = new UnsignedByte((byte) 0b11110000);
    }

    private void setup1() {
        this.memory[5] = new UnsignedByte((byte) 0b00100000); // 1
        this.memory[6] = new UnsignedByte((byte) 0b01100000);
        this.memory[7] = new UnsignedByte((byte) 0b00100000);
        this.memory[8] = new UnsignedByte((byte) 0b00100000);
        this.memory[9] = new UnsignedByte((byte) 0b01110000);
    }

    private void setup2() {
        this.memory[10] = new UnsignedByte((byte) 0b11110000); // 2
        this.memory[11] = new UnsignedByte((byte) 0b00010000);
        this.memory[12] = new UnsignedByte((byte) 0b11110000);
        this.memory[13] = new UnsignedByte((byte) 0b10000000);
        this.memory[14] = new UnsignedByte((byte) 0b11110000);
    }

    private void setup3() {
        this.memory[15] = new UnsignedByte((byte) 0b11110000); // 3
        this.memory[16] = new UnsignedByte((byte) 0b00010000);
        this.memory[17] = new UnsignedByte((byte) 0b11110000);
        this.memory[18] = new UnsignedByte((byte) 0b00010000);
        this.memory[19] = new UnsignedByte((byte) 0b11110000);
    }

    private void setup4() {
        this.memory[20] = new UnsignedByte((byte) 0b10010000); // 4
        this.memory[21] = new UnsignedByte((byte) 0b10010000);
        this.memory[22] = new UnsignedByte((byte) 0b11110000);
        this.memory[23] = new UnsignedByte((byte) 0b00010000);
        this.memory[24] = new UnsignedByte((byte) 0b00010000);
    }

    private void setup5() {
        this.memory[25] = new UnsignedByte((byte) 0b11110000); // 5
        this.memory[26] = new UnsignedByte((byte) 0b10000000);
        this.memory[27] = new UnsignedByte((byte) 0b11110000);
        this.memory[28] = new UnsignedByte((byte) 0b00010000);
        this.memory[29] = new UnsignedByte((byte) 0b11110000);
    }

    private void setup6() {
        this.memory[30] = new UnsignedByte((byte) 0b11110000); // 6
        this.memory[31] = new UnsignedByte((byte) 0b10000000);
        this.memory[32] = new UnsignedByte((byte) 0b11110000);
        this.memory[33] = new UnsignedByte((byte) 0b10010000);
        this.memory[34] = new UnsignedByte((byte) 0b11110000);
    }

    private void setup7() {
        this.memory[35] = new UnsignedByte((byte) 0b11110000); // 7
        this.memory[36] = new UnsignedByte((byte) 0b00010000);
        this.memory[37] = new UnsignedByte((byte) 0b00100000);
        this.memory[38] = new UnsignedByte((byte) 0b01000000);
        this.memory[39] = new UnsignedByte((byte) 0b01000000);
    }

    private void setup8() {
        this.memory[40] = new UnsignedByte((byte) 0b11110000); // 8
        this.memory[41] = new UnsignedByte((byte) 0b10010000);
        this.memory[42] = new UnsignedByte((byte) 0b11110000);
        this.memory[43] = new UnsignedByte((byte) 0b10010000);
        this.memory[44] = new UnsignedByte((byte) 0b11110000);
    }

    private void setup9() {
        this.memory[45] = new UnsignedByte((byte) 0b11110000); // 9
        this.memory[46] = new UnsignedByte((byte) 0b10010000);
        this.memory[47] = new UnsignedByte((byte) 0b11110000);
        this.memory[48] = new UnsignedByte((byte) 0b00010000);
        this.memory[49] = new UnsignedByte((byte) 0b11110000);
    }

    private void setupA() {
        this.memory[50] = new UnsignedByte((byte) 0b11110000); // A
        this.memory[51] = new UnsignedByte((byte) 0b10010000);
        this.memory[52] = new UnsignedByte((byte) 0b11110000);
        this.memory[53] = new UnsignedByte((byte) 0b10010000);
        this.memory[54] = new UnsignedByte((byte) 0b10010000);
    }

    private void setupB() {
        this.memory[55] = new UnsignedByte((byte) 0b11100000); // B
        this.memory[56] = new UnsignedByte((byte) 0b10010000);
        this.memory[57] = new UnsignedByte((byte) 0b11100000);
        this.memory[58] = new UnsignedByte((byte) 0b10010000);
        this.memory[59] = new UnsignedByte((byte) 0b11100000);
    }

    private void setupC() {
        this.memory[60] = new UnsignedByte((byte) 0b11110000); // C
        this.memory[61] = new UnsignedByte((byte) 0b10000000);
        this.memory[62] = new UnsignedByte((byte) 0b10000000);
        this.memory[63] = new UnsignedByte((byte) 0b10000000);
        this.memory[64] = new UnsignedByte((byte) 0b11110000);
    }

    private void setupD() {
        this.memory[60] = new UnsignedByte((byte) 0b11100000); // D
        this.memory[61] = new UnsignedByte((byte) 0b10010000);
        this.memory[62] = new UnsignedByte((byte) 0b10010000);
        this.memory[63] = new UnsignedByte((byte) 0b10010000);
        this.memory[64] = new UnsignedByte((byte) 0b11100000);
    }

    private void setupE() {
        this.memory[65] = new UnsignedByte((byte) 0b11110000); // E
        this.memory[66] = new UnsignedByte((byte) 0b10000000);
        this.memory[67] = new UnsignedByte((byte) 0b11110000);
        this.memory[68] = new UnsignedByte((byte) 0b10000000);
        this.memory[69] = new UnsignedByte((byte) 0b11110000);
    }

    private void setupF() {
        this.memory[70] = new UnsignedByte((byte) 0b11110000); // F
        this.memory[71] = new UnsignedByte((byte) 0b10000000);
        this.memory[72] = new UnsignedByte((byte) 0b11110000);
        this.memory[73] = new UnsignedByte((byte) 0b10000000);
        this.memory[74] = new UnsignedByte((byte) 0b10000000);
    }

    public String getValues() {
        return Arrays.toString(memory);
    }
}
