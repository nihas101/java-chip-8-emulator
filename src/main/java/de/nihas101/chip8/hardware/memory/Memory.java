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
        UnsignedByte[] zero = setupCharacter(
                (byte) 0b11110000,
                (byte) 0b10010000,
                (byte) 0b10010000,
                (byte) 0b10010000,
                (byte) 0b11110000
        );

        writeCharacter(0, zero);
    }

    private void setup1() {
        UnsignedByte[] one = setupCharacter(
                (byte) 0b00100000,
                (byte) 0b01100000,
                (byte) 0b00100000,
                (byte) 0b00100000,
                (byte) 0b01110000
        );

        writeCharacter(5, one);
    }

    private void setup2() {
        UnsignedByte[] two = setupCharacter(
                (byte) 0b11110000,
                (byte) 0b00010000,
                (byte) 0b11110000,
                (byte) 0b10000000,
                (byte) 0b11110000
        );

        writeCharacter(10, two);
    }

    private void setup3() {
        UnsignedByte[] three = setupCharacter(
                (byte) 0b11110000,
                (byte) 0b00010000,
                (byte) 0b11110000,
                (byte) 0b00010000,
                (byte) 0b11110000
        );

        writeCharacter(15, three);
    }

    private void setup4() {
        UnsignedByte[] four = setupCharacter(
                (byte) 0b10010000,
                (byte) 0b10010000,
                (byte) 0b11110000,
                (byte) 0b00010000,
                (byte) 0b00010000
        );

        writeCharacter(20, four);
    }

    private void setup5() {
        UnsignedByte[] five = setupCharacter(
                (byte) 0b11110000,
                (byte) 0b10000000,
                (byte) 0b11110000,
                (byte) 0b00010000,
                (byte) 0b11110000
        );

        writeCharacter(25, five);
    }

    private void setup6() {
        UnsignedByte[] six = setupCharacter(
                (byte) 0b11110000,
                (byte) 0b10000000,
                (byte) 0b11110000,
                (byte) 0b10010000,
                (byte) 0b11110000
        );

        writeCharacter(30, six);
    }

    private void setup7() {
        UnsignedByte[] seven = setupCharacter(
                (byte) 0b11110000,
                (byte) 0b00010000,
                (byte) 0b00100000,
                (byte) 0b01000000,
                (byte) 0b01000000
        );

        writeCharacter(35, seven);
    }

    private void setup8() {
        UnsignedByte[] eight = setupCharacter(
                (byte) 0b11110000,
                (byte) 0b10010000,
                (byte) 0b11110000,
                (byte) 0b10010000,
                (byte) 0b11110000
        );

        writeCharacter(40, eight);
    }

    private void setup9() {
        UnsignedByte[] nine = setupCharacter(
                (byte) 0b11110000,
                (byte) 0b10010000,
                (byte) 0b11110000,
                (byte) 0b00010000,
                (byte) 0b11110000
        );

        writeCharacter(45, nine);
    }

    private void setupA() {
        UnsignedByte[] a = setupCharacter(
                (byte) 0b11110000,
                (byte) 0b10010000,
                (byte) 0b11110000,
                (byte) 0b10010000,
                (byte) 0b10010000
        );

        writeCharacter(50, a);
    }

    private void setupB() {
        UnsignedByte[] b = setupCharacter(
                (byte) 0b11100000,
                (byte) 0b10010000,
                (byte) 0b11100000,
                (byte) 0b10010000,
                (byte) 0b11100000
        );

        writeCharacter(55, b);
    }

    private void setupC() {
        UnsignedByte[] c = setupCharacter(
                (byte) 0b11110000,
                (byte) 0b10000000,
                (byte) 0b10000000,
                (byte) 0b10000000,
                (byte) 0b11110000
        );

        writeCharacter(60, c);
    }

    private void setupD() {
        UnsignedByte[] d = setupCharacter(
                (byte) 0b11100000,
                (byte) 0b10010000,
                (byte) 0b10010000,
                (byte) 0b10010000,
                (byte) 0b11100000
        );

        writeCharacter(65, d);
    }

    private void setupE() {
        UnsignedByte[] e = setupCharacter(
                (byte) 0b11110000,
                (byte) 0b10000000,
                (byte) 0b11110000,
                (byte) 0b10000000,
                (byte) 0b11110000
        );

        writeCharacter(70, e);
    }

    private void setupF() {
        UnsignedByte[] f = setupCharacter(
                (byte) 0b11110000,
                (byte) 0b10000000,
                (byte) 0b11110000,
                (byte) 0b10000000,
                (byte) 0b10000000
        );
        writeCharacter(75, f);
    }

    private UnsignedByte[] setupCharacter(byte one, byte two, byte three, byte four, byte five){
        return new UnsignedByte[]{
                new UnsignedByte(one),
                new UnsignedByte(two),
                new UnsignedByte(three),
                new UnsignedByte(four),
                new UnsignedByte(five)
        };
    }

    private void writeCharacter(int startIndex, UnsignedByte[] unsignedBytes) {
        this.memory[startIndex++] = unsignedBytes[0];
        this.memory[startIndex++] = unsignedBytes[1];
        this.memory[startIndex++] = unsignedBytes[2];
        this.memory[startIndex++] = unsignedBytes[3];
        this.memory[startIndex] = unsignedBytes[4];
    }

    public String getValues() {
        return Arrays.toString(memory);
    }
}
