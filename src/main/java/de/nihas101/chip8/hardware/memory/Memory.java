package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.debug.Debuggable;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
        CountDownLatch latch = new CountDownLatch(16);

        createThread(this::setup0, latch).start();
        createThread(this::setup1, latch).start();
        createThread(this::setup2, latch).start();
        createThread(this::setup3, latch).start();
        createThread(this::setup4, latch).start();
        createThread(this::setup5, latch).start();
        createThread(this::setup6, latch).start();
        createThread(this::setup7, latch).start();
        createThread(this::setup8, latch).start();
        createThread(this::setup9, latch).start();
        createThread(this::setupA, latch).start();
        createThread(this::setupB, latch).start();
        createThread(this::setupC, latch).start();
        createThread(this::setupD, latch).start();
        createThread(this::setupE, latch).start();
        createThread(this::setupF, latch).start();

        try {
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Thread createThread(Runnable target, CountDownLatch latch) {
        return new Thread(() -> {
            target.run();
            latch.countDown();
        });
    }

    private void setup0() {
        UnsignedByte[] zero = setupCharacter(
                0b11110000,
                0b10010000,
                0b10010000,
                0b10010000,
                0b11110000
        );

        writeCharacter(0, zero);
    }

    private void setup1() {
        UnsignedByte[] one = setupCharacter(
                0b00100000,
                0b01100000,
                0b00100000,
                0b00100000,
                0b01110000
        );

        writeCharacter(5, one);
    }

    private void setup2() {
        UnsignedByte[] two = setupCharacter(
                0b11110000,
                0b00010000,
                0b11110000,
                0b10000000,
                0b11110000
        );

        writeCharacter(10, two);
    }

    private void setup3() {
        UnsignedByte[] three = setupCharacter(
                0b11110000,
                0b00010000,
                0b11110000,
                0b00010000,
                0b11110000
        );

        writeCharacter(15, three);
    }

    private void setup4() {
        UnsignedByte[] four = setupCharacter(
                0b10010000,
                0b10010000,
                0b11110000,
                0b00010000,
                0b00010000
        );

        writeCharacter(20, four);
    }

    private void setup5() {
        UnsignedByte[] five = setupCharacter(
                0b11110000,
                0b10000000,
                0b11110000,
                0b00010000,
                0b11110000
        );

        writeCharacter(25, five);
    }

    private void setup6() {
        UnsignedByte[] six = setupCharacter(
                0b11110000,
                0b10000000,
                0b11110000,
                0b10010000,
                0b11110000
        );

        writeCharacter(30, six);
    }

    private void setup7() {
        UnsignedByte[] seven = setupCharacter(
                0b11110000,
                0b00010000,
                0b00100000,
                0b01000000,
                0b01000000
        );

        writeCharacter(35, seven);
    }

    private void setup8() {
        UnsignedByte[] eight = setupCharacter(
                0b11110000,
                0b10010000,
                0b11110000,
                0b10010000,
                0b11110000
        );

        writeCharacter(40, eight);
    }

    private void setup9() {
        UnsignedByte[] nine = setupCharacter(
                0b11110000,
                0b10010000,
                0b11110000,
                0b00010000,
                0b11110000
        );

        writeCharacter(45, nine);
    }

    private void setupA() {
        UnsignedByte[] a = setupCharacter(
                0b11110000,
                0b10010000,
                0b11110000,
                0b10010000,
                0b10010000
        );

        writeCharacter(50, a);
    }

    private void setupB() {
        UnsignedByte[] b = setupCharacter(
                0b11100000,
                0b10010000,
                0b11100000,
                0b10010000,
                0b11100000
        );

        writeCharacter(55, b);
    }

    private void setupC() {
        UnsignedByte[] c = setupCharacter(
                0b11110000,
                0b10000000,
                0b10000000,
                0b10000000,
                0b11110000
        );

        writeCharacter(60, c);
    }

    private void setupD() {
        UnsignedByte[] d = setupCharacter(
                0b11100000,
                0b10010000,
                0b10010000,
                0b10010000,
                0b11100000
        );

        writeCharacter(65, d);
    }

    private void setupE() {
        UnsignedByte[] e = setupCharacter(
                0b11110000,
                0b10000000,
                0b11110000,
                0b10000000,
                0b11110000
        );

        writeCharacter(70, e);
    }

    private void setupF() {
        UnsignedByte[] f = setupCharacter(
                0b11110000,
                0b10000000,
                0b11110000,
                0b10000000,
                0b10000000
        );
        writeCharacter(75, f);
    }

    private UnsignedByte[] setupCharacter(int one, int two, int three, int four, int five) {
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
