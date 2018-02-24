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
        UnsignedByte[] zero = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b11110000)
        };

        setupCharacter(0, zero);
    }

    private void setup1() {
        UnsignedByte[] one = new UnsignedByte[]{
                new UnsignedByte((byte) 0b00100000),
                new UnsignedByte((byte) 0b01100000),
                new UnsignedByte((byte) 0b00100000),
                new UnsignedByte((byte) 0b00100000),
                new UnsignedByte((byte) 0b01110000)
        };

        setupCharacter(5, one);
    }

    private void setup2() {
        UnsignedByte[] two = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b00010000),
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10000000),
                new UnsignedByte((byte) 0b11110000)
        };

        setupCharacter(10, two);
    }

    private void setup3() {
        UnsignedByte[] three = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b00010000),
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b00010000),
                new UnsignedByte((byte) 0b11110000)
        };

        setupCharacter(15, three);
    }

    private void setup4() {
        UnsignedByte[] four = new UnsignedByte[]{
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b00010000),
                new UnsignedByte((byte) 0b00010000)
        };

        setupCharacter(20, four);
    }

    private void setup5() {
        UnsignedByte[] five = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10000000),
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b00010000),
                new UnsignedByte((byte) 0b11110000)
        };

        setupCharacter(25, five);
    }

    private void setup6() {
        UnsignedByte[] six = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10000000),
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b11110000)
        };

        setupCharacter(30, six);
    }

    private void setup7() {
        UnsignedByte[] seven = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b00010000),
                new UnsignedByte((byte) 0b00100000),
                new UnsignedByte((byte) 0b01000000),
                new UnsignedByte((byte) 0b01000000)
        };

        setupCharacter(35, seven);
    }

    private void setup8() {
        UnsignedByte[] eight = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b11110000)
        };

        setupCharacter(40, eight);
    }

    private void setup9() {
        UnsignedByte[] nine = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b00010000),
                new UnsignedByte((byte) 0b11110000)
        };

        setupCharacter(45, nine);
    }

    private void setupA() {
        UnsignedByte[] a = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b10010000)
        };

        setupCharacter(50, a);
    }

    private void setupB() {
        UnsignedByte[] b = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11100000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b11100000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b11100000)
        };

        setupCharacter(55, b);
    }

    private void setupC() {
        UnsignedByte[] c = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10000000),
                new UnsignedByte((byte) 0b10000000),
                new UnsignedByte((byte) 0b10000000),
                new UnsignedByte((byte) 0b11110000)
        };

        setupCharacter(60, c);
    }

    private void setupD() {
        UnsignedByte[] d = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11100000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b10010000),
                new UnsignedByte((byte) 0b11100000)
        };

        setupCharacter(65, d);
    }

    private void setupE() {
        UnsignedByte[] e = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10000000),
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10000000),
                new UnsignedByte((byte) 0b11110000)
        };

        setupCharacter(70, e);
    }

    private void setupF() {
        UnsignedByte[] f = new UnsignedByte[]{
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10000000),
                new UnsignedByte((byte) 0b11110000),
                new UnsignedByte((byte) 0b10000000),
                new UnsignedByte((byte) 0b10000000)
        };
        setupCharacter(75, f);
    }

    private void setupCharacter(int startIndex, UnsignedByte[] unsignedBytes) {
        this.memory[startIndex++] = unsignedBytes[0]; // F
        this.memory[startIndex++] = unsignedBytes[1];
        this.memory[startIndex++] = unsignedBytes[2];
        this.memory[startIndex++] = unsignedBytes[3];
        this.memory[startIndex] = unsignedBytes[4];
    }

    public String getValues() {
        return Arrays.toString(memory);
    }
}
