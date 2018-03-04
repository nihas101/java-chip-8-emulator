package de.nihas101.chip8.opcodes;

import de.nihas101.chip8.hardware.CentralProcessingUnit;
import de.nihas101.chip8.hardware.memory.*;
import de.nihas101.chip8.hardware.timers.DelayTimer;
import de.nihas101.chip8.hardware.timers.SoundTimer;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;
import de.nihas101.chip8.unsignedDataTypes.UnsignedShort;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.Stack;
import java.util.Timer;

import static de.nihas101.chip8.hardware.memory.ScreenMemory.SCREEN_HEIGHT;
import static de.nihas101.chip8.hardware.memory.ScreenMemory.SCREEN_WIDTH;
import static de.nihas101.chip8.utils.Constants.PROGRAM_COUNTER_START;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CentralProcessingUnitTest {
    private CentralProcessingUnit cpu;
    private Memory memory;
    private Random random;

    @Before
    public void setup() {
        memory = new Memory();
        /* New random subclass for  testing, returns the same int every time */
        random = new Random() {
            @Override
            public int nextInt(int bound) {
                return 0x0011;
            }
        };
        cpu = new CentralProcessingUnit(
                memory,
                new ScreenMemory(),
                new Registers(),
                new AddressRegister(),
                new ProgramCounter(new UnsignedShort(0)),
                new Chip8Stack(new Stack<>()),
                new Timer("Timer"),
                new DelayTimer() {
                    @Override
                    public void decrementValue() {
                        /* DO NOTHING*/
                    }
                },
                new SoundTimer() {
                    @Override
                    public void decrementValue() {
                        /* DO NOTHING*/
                    }
                },
                random);
    }

    @Test
    public void test00E0() throws UnknownOPCodeException {   // clear screen
        int opcode = 0x00E0;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        this.cpu.getScreenMemory().write(0, 0, true);
        this.cpu.getScreenMemory().write(1, 1, true);
        this.cpu.getScreenMemory().write(2, 2, true);
        this.cpu.getScreenMemory().write(3, 3, true);

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        cpu.decodeNextOpCode();

        for (int x = 0; x < SCREEN_WIDTH; x++)
            for (int y = 0; y < SCREEN_HEIGHT; y++)
                assertEquals(false, this.cpu.getScreenMemory().read(x, y));
    }

    @Test
    public void test00EEStack() throws UnknownOPCodeException {   // return from subroutine
        int opcode = 0x00EE;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        this.cpu.getStack().push(new UnsignedShort(0x321));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        cpu.decodeNextOpCode();

        assertEquals(0, this.cpu.getStack().getSize());
    }

    @Test
    public void test00EEProgramCounter() throws UnknownOPCodeException {   // return from subroutine
        setOpCode(0x00EE);
        cpu.getStack().push(new UnsignedShort(0x321));

        cpu.decodeNextOpCode();

        assertEquals(0x321, this.cpu.getProgramCounter().getCounter().unsignedDataType);
    }

    @Test
    public void test1NNN() throws UnknownOPCodeException {   // jump to address NNN
        setOpCode(0x1123);

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedShort(0x123), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void test2NNNProgramCounter() throws UnknownOPCodeException {   // Call subroutine at NNN
        setOpCode(0x01FF, 0x2123);

        cpu.getProgramCounter().jumpTo(new UnsignedShort(0x01FF));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedShort(0x0123), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void test2NNNResult() throws UnknownOPCodeException {   // Call subroutine at NNN
        setOpCode(0x01FF, 0x2123);

        cpu.getProgramCounter().jumpTo(new UnsignedShort(0x01FF));

        cpu.decodeNextOpCode();

        // 0x01FF + 0x0002 = 0x0201
        assertEquals(new UnsignedShort(0x0201), cpu.getStack().pop());
    }

    @Test
    public void test3XNN() throws UnknownOPCodeException {   // Skip next instruction if Vx == NN
        setOpCode(0x3002);

        cpu.getRegisters().poke(0, new UnsignedByte(2));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedShort(4), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void test4XNN() throws UnknownOPCodeException {   // Skip next instruction if Vx != NN
        setOpCode(0x4201);

        cpu.getRegisters().poke(2, new UnsignedByte(2));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedShort(4), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void test5XY0() throws UnknownOPCodeException {      // Skip next instruction if Vx == Vy
        setOpCode(0x5230);

        cpu.getRegisters().poke(2, new UnsignedByte(1));
        cpu.getRegisters().poke(3, new UnsignedByte(1));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedShort(4), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void test6XNN() throws UnknownOPCodeException {   // 	Sets VX to NN
        setOpCode(0x6512);

        cpu.getRegisters().poke(5, new UnsignedByte(100));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte(0x512), cpu.getRegisters().peek(5));
    }

    @Test
    public void test7XNN() throws UnknownOPCodeException {   // Adds NN to VX
        setOpCode(0x7701);

        cpu.getRegisters().poke(7, new UnsignedByte(100));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte(101), cpu.getRegisters().peek(7));
    }

    @Test
    public void test8XY0() throws UnknownOPCodeException {   // Sets VX to the value of VY
        setOpCode(0x8780);

        cpu.getRegisters().poke(7, new UnsignedByte(90));
        cpu.getRegisters().poke(8, new UnsignedByte(101));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte(101), cpu.getRegisters().peek(7));
    }

    @Test
    public void test8XY1() throws UnknownOPCodeException {   // 	Sets VX to VX or VY (Bitwise OR operation)
        setOpCode(0x89A1);

        cpu.getRegisters().poke(0x9, new UnsignedByte(0b110));
        cpu.getRegisters().poke(0xA, new UnsignedByte(0b001));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte(0b111), cpu.getRegisters().peek(9));
    }

    @Test
    public void test8XY2() throws UnknownOPCodeException {   // Sets VX to VX and VY (Bitwise AND operation)
        setOpCode(0x8BC2);

        cpu.getRegisters().poke(0xB, new UnsignedByte(0b110));
        cpu.getRegisters().poke(0xC, new UnsignedByte(0b101));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte(0b100), cpu.getRegisters().peek(0xB));
    }

    @Test
    public void test8XY3() throws UnknownOPCodeException {   // Sets VX to VX xor VY
        setOpCode(0x8DE3);

        cpu.getRegisters().poke(0xD, new UnsignedByte(0b110));
        cpu.getRegisters().poke(0xE, new UnsignedByte(0b101));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte(0b011), cpu.getRegisters().peek(0xD));
    }

    @Test
    public void test8XY4NoCarry() throws UnknownOPCodeException {   // Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't
        setOpCode(0x8DE4);

        cpu.getRegisters().poke(0xD, new UnsignedByte((byte) 0b001));
        cpu.getRegisters().poke(0xE, new UnsignedByte((byte) 0b001));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte(0b010), cpu.getRegisters().peek(0xD));
        assertEquals(new UnsignedByte(0), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XY4Carry() throws UnknownOPCodeException {   // Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't
        setOpCode(0x8DE4);

        cpu.getRegisters().poke(0xD, new UnsignedByte(0xFF));
        cpu.getRegisters().poke(0xE, new UnsignedByte(0x01));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte(0), cpu.getRegisters().peek(0xD));
        assertEquals(new UnsignedByte(1), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XY5Borrow() throws UnknownOPCodeException {   // VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
        setOpCode(0x8015);

        cpu.getRegisters().poke(0x0, new UnsignedByte(0x01));
        cpu.getRegisters().poke(0x1, new UnsignedByte(0x02));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte(0xFF), cpu.getRegisters().peek(0x0));
        assertEquals(new UnsignedByte(0), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XY5NoBorrow() throws UnknownOPCodeException {   // VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
        setOpCode(0x8015);

        cpu.getRegisters().poke(0x0, new UnsignedByte(0x01));
        cpu.getRegisters().poke(0x1, new UnsignedByte(0x01));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte(0), cpu.getRegisters().peek(0x0));
        assertEquals(new UnsignedByte(1), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XY6() throws UnknownOPCodeException {   // Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift.
        setOpCode(0x8126);

        cpu.getRegisters().poke(0x1, new UnsignedByte(0x0F));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte((0x0F >> 1)), cpu.getRegisters().peek(0x1));
        assertEquals(new UnsignedByte(0x1), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XY7Borrow() throws UnknownOPCodeException {   // Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
        setOpCode(0x8237);

        cpu.getRegisters().poke(0x3, new UnsignedByte(0x01));
        cpu.getRegisters().poke(0x2, new UnsignedByte(0x0F));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte((0x01 - 0x0F)), cpu.getRegisters().peek(0x2));
        assertEquals(new UnsignedByte(0x0), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XY7NoBorrow() throws UnknownOPCodeException {   // Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
        setOpCode(0x8237);

        cpu.getRegisters().poke(0x3, new UnsignedByte(0x0F));
        cpu.getRegisters().poke(0x2, new UnsignedByte(0x01));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte(0x0E), cpu.getRegisters().peek(0x2));
        assertEquals(new UnsignedByte(0x1), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XYE() throws UnknownOPCodeException {   // Shifts VX left by one. VF is set to the value of the most significant bit of VX before the shift.
        setOpCode(0x834E);

        cpu.getRegisters().poke(0x3, new UnsignedByte(0xFF));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte(0xFE), cpu.getRegisters().peek(0x3));
        assertEquals(new UnsignedByte(0x1), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test9XYE() throws UnknownOPCodeException {   // Skips the next instruction if VX doesn't equal VY. (Usually the next instruction is a jump to skip a code block)
        setOpCode(0x945E);
        cpu.getRegisters().poke(0x4, new UnsignedByte(0x0F));
        cpu.getRegisters().poke(0x5, new UnsignedByte(0x0E));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedShort(4), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void testANNN() throws UnknownOPCodeException {   // Sets I to the address NNN.
        setOpCode(0xA123);

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedShort(0x123), cpu.getAddressRegister().getAddress());
    }

    @Test
    public void testBNNN() throws UnknownOPCodeException {   // Jumps to the address NNN plus V0.
        setOpCode(0xB123);

        cpu.getRegisters().poke(0, new UnsignedByte(0x03));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedShort(0x126), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void testCXNN() throws UnknownOPCodeException {   // Sets VX to the result of a bitwise and operation on a random number (Typically: 0 to 255) and NN.
        setOpCode(0xC512);

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedByte((0x12 & random.nextInt(255))), cpu.getRegisters().peek(5));
    }

    @Test
    public void testDXYNNoCollision() throws UnknownOPCodeException {   // Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels
        setOpCode(0xD01A);

        cpu.getRegisters().poke(0x0, new UnsignedByte(0));
        cpu.getRegisters().poke(0x1, new UnsignedByte(0));

        setMemoryForSpriteToDisplay();
        cpu.getAddressRegister().setAddress(new UnsignedShort(0x200));

        cpu.decodeNextOpCode();

        checkIfCollisionOccurred(true);
    }

    @Test
    public void testDXYNCollision() throws UnknownOPCodeException {   // Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels
        setOpCode(0xD01A);

        /* Write into screen memory */
        cpu.getScreenMemory().write(0, 0, true);
        setMemoryForSpriteToDisplay();
        cpu.getAddressRegister().setAddress(new UnsignedShort(0x200));

        cpu.decodeNextOpCode();

        checkIfCollisionOccurred(false);
    }

    private void setMemoryForSpriteToDisplay() {
        memory.write(0x200, new UnsignedByte(0xFF));
        memory.write(0x201, new UnsignedByte(0xF0));
        memory.write(0x202, new UnsignedByte(0xFF));
        memory.write(0x203, new UnsignedByte(0xF0));
        memory.write(0x204, new UnsignedByte(0xFF));
        memory.write(0x205, new UnsignedByte(0XF0));
        memory.write(0x206, new UnsignedByte(0xFF));
        memory.write(0x207, new UnsignedByte(0xF0));
        memory.write(0x208, new UnsignedByte(0xFF));
        memory.write(0x209, new UnsignedByte(0xF0));
        memory.write(0x20A, new UnsignedByte(0xFF));
    }

    private void checkIfCollisionOccurred(boolean expected) {
        assertEquals(expected, cpu.getScreenMemory().read(0, 0));

        if (expected) assertEquals(new UnsignedByte(0), cpu.getRegisters().peek(0xF));
        else assertEquals(new UnsignedByte(1), cpu.getRegisters().peek(0xF));

        assertEquals(new UnsignedShort(0x200), cpu.getAddressRegister().getAddress());
    }

    @Test
    public void testEX9E() throws UnknownOPCodeException {   // Skips the next instruction if the key stored in VX is pressed. (Usually the next instruction is a jump to skip a code block)
        setOpCode(0xEC9E);

        /* Set key and key to look for */
        cpu.setKeyCode(1);
        cpu.getRegisters().poke(0xC, new UnsignedByte(1));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedShort(4), this.cpu.getProgramCounter().getCounter());
    }

    @Test
    public void testEXA1() throws UnknownOPCodeException {   // Skips the next instruction if the key stored in VX isn't pressed. (Usually the next instruction is a jump to skip a code block)
        setOpCode(0xEEA1);

        /* Set key and key to look for */
        cpu.setKeyCode(0);
        cpu.getRegisters().poke(0xE, new UnsignedByte(1));

        cpu.decodeNextOpCode();

        assertEquals(new UnsignedShort(4), this.cpu.getProgramCounter().getCounter());
    }

    @Test
    public void testFX07() throws UnknownOPCodeException {   // Sets VX to the value of the delay timer.
        setOpCode(0xF107);
        cpu.getDelayTimer().setValue(10);

        cpu.decodeNextOpCode();

        assertEquals(10, cpu.getRegisters().peek(1).unsignedDataType);
    }

    @Test
    public void testFX0A() throws UnknownOPCodeException {   // A key press is awaited, and then stored in VX. (Blocking BinaryOperation. All instruction halted until next key event)
        setOpCode(0xF30A);
        cpu.setKeyCode(1);

        cpu.decodeNextOpCode();

        assertEquals(1, cpu.getRegisters().peek(3).unsignedDataType);
    }

    @Test
    public void testFX15() throws UnknownOPCodeException {   // Sets the delay timer to VX.
        setOpCode(0xF815);
        cpu.getRegisters().poke(8, new UnsignedByte(10));

        cpu.decodeNextOpCode();

        assertEquals(10, cpu.getDelayTimer().getValue());
    }

    @Test
    public void testFX18() throws UnknownOPCodeException {   // Sets the sound timer to VX.
        setOpCode(0xFB18);
        cpu.getRegisters().poke(0xB, new UnsignedByte(10));

        cpu.decodeNextOpCode();

        assertEquals(10, this.cpu.getSoundTimer().getValue());
    }

    @Test
    public void testFX1E() throws UnknownOPCodeException {   // Adds VX to I
        setOpCode(0xF91E);
        /* Setup registers */
        cpu.getRegisters().poke(0x9, new UnsignedByte(10));
        cpu.getAddressRegister().setAddress(new UnsignedShort(0x200));

        cpu.decodeNextOpCode();

        assertEquals(0x20A, this.cpu.getAddressRegister().getAddress().unsignedDataType);
    }

    @Test
    public void testFX29() throws UnknownOPCodeException {   // Sets I to the location of the sprite for the character in VX. Characters 0-F (in hexadecimal) are represented by a 4x5 font.
        setOpCode(0xF429);
        cpu.getRegisters().poke(4, new UnsignedByte(1));

        cpu.decodeNextOpCode();

        assertEquals(5, cpu.getAddressRegister().getAddress().unsignedDataType);
    }

    @Test
    public void testFX33() throws UnknownOPCodeException {   // Stores the binary-coded decimal representation of VX, with the most significant of three digits at the address in I, the middle digit at I plus 1, and the least significant digit at I plus 2. (In other words, take the decimal representation of VX, place the hundreds digit in hardware at location in I, the tens digit at location I+1, and the ones digit at location I+2.)
        setOpCode(0xF733);

        cpu.getRegisters().poke(0x7, new UnsignedByte(123));
        cpu.getAddressRegister().setAddress(new UnsignedShort(0x200));
        cpu.decodeNextOpCode();

        assertEquals(1, this.memory.read(0x200).unsignedDataType);
        assertEquals(2, this.memory.read(0x201).unsignedDataType);
        assertEquals(3, this.memory.read(0x202).unsignedDataType);
    }

    @Test
    public void testFX55() throws UnknownOPCodeException {   // Stores V0 to VX (including VX) in hardware starting at address I.
        setOpCode(0xF555);

        cpu.getRegisters().poke(0, new UnsignedByte(1));
        cpu.getRegisters().poke(1, new UnsignedByte(2));
        cpu.getRegisters().poke(2, new UnsignedByte(3));
        cpu.getRegisters().poke(3, new UnsignedByte(4));
        cpu.getRegisters().poke(4, new UnsignedByte(5));
        cpu.getRegisters().poke(5, new UnsignedByte(6));
        cpu.getAddressRegister().setAddress(new UnsignedShort(0x200));

        cpu.decodeNextOpCode();

        assertEquals(1, this.memory.read(0x200).unsignedDataType);
        assertEquals(2, this.memory.read(0x201).unsignedDataType);
        assertEquals(3, this.memory.read(0x202).unsignedDataType);
        assertEquals(4, this.memory.read(0x203).unsignedDataType);
        assertEquals(5, this.memory.read(0x204).unsignedDataType);
        assertEquals(6, this.memory.read(0x205).unsignedDataType);
    }

    @Test
    public void testFX65() throws UnknownOPCodeException {   // Fills V0 to VX (including VX) with values from hardware starting at address I
        setOpCode(0xF865);
        memory.write(0x205, new UnsignedByte(1));
        memory.write(0x206, new UnsignedByte(2));
        memory.write(0x207, new UnsignedByte(3));
        memory.write(0x208, new UnsignedByte(4));
        memory.write(0x209, new UnsignedByte(5));
        memory.write(0x20A, new UnsignedByte(6));
        memory.write(0x20B, new UnsignedByte(7));
        memory.write(0x20C, new UnsignedByte(8));
        cpu.getAddressRegister().setAddress(new UnsignedShort(0x205));

        cpu.decodeNextOpCode();

        assertEquals(1, this.cpu.getRegisters().peek(0).unsignedDataType);
        assertEquals(2, this.cpu.getRegisters().peek(1).unsignedDataType);
        assertEquals(3, this.cpu.getRegisters().peek(2).unsignedDataType);
        assertEquals(4, this.cpu.getRegisters().peek(3).unsignedDataType);
        assertEquals(5, this.cpu.getRegisters().peek(4).unsignedDataType);
        assertEquals(6, this.cpu.getRegisters().peek(5).unsignedDataType);
        assertEquals(7, this.cpu.getRegisters().peek(6).unsignedDataType);
        assertEquals(8, this.cpu.getRegisters().peek(7).unsignedDataType);
    }

    @Test
    public void changeTimerSpeed() {
        cpu.changeTimerSpeed(2.0);
    }

    @Test
    public void reset() {
        cpu.getRegisters().poke(0, new UnsignedByte(10));
        cpu.getProgramCounter().jumpTo(new UnsignedShort(0x210));
        cpu.getScreenMemory().write(0, 0, true);
        cpu.getAddressRegister().setAddress(new UnsignedShort(10));

        cpu.reset();

        assertEquals(0, cpu.getRegisters().peek(0).unsignedDataType);
        assertEquals(PROGRAM_COUNTER_START, cpu.getProgramCounter().getCounter().unsignedDataType);
        assertEquals(false, cpu.getScreenMemory().read(0, 0));
        assertEquals(0, cpu.getAddressRegister().getAddress().unsignedDataType);
    }

    @Test
    public void clearMemory() {
        cpu.getMemory().write(PROGRAM_COUNTER_START, new UnsignedByte(1));
        cpu.getMemory().write(PROGRAM_COUNTER_START + 1, new UnsignedByte(2));
        cpu.getMemory().write(PROGRAM_COUNTER_START + 2, new UnsignedByte(3));

        cpu.clearMemory();

        assertEquals(0, cpu.getMemory().read(PROGRAM_COUNTER_START).unsignedDataType);
        assertEquals(0, cpu.getMemory().read(PROGRAM_COUNTER_START + 1).unsignedDataType);
        assertEquals(0, cpu.getMemory().read(PROGRAM_COUNTER_START + 2).unsignedDataType);
    }

    @Test
    public void getState() {
        assertEquals("State:\n" +
                "Cycles executed: 0\n" +
                "OpCode: \n" +
                "V0: 0   V1: 0   V2: 0   V3: 0   V4: 0   V5: 0   V6: 0   V7: 0\n" +
                "V8: 0   V9: 0   VA: 0   VB: 0   VC: 0   VD: 0   VE: 0   VF: 0\n" +
                "I:\t0\tPC:\t0\n" +
                "Chip8Stack:\t[]\n" +
                "DelayTimer: 0\tSoundTimer: 0\n", cpu.getState());
    }

    @Test
    public void getMemory() {
        assertEquals(240, cpu.getMemory().read(0).unsignedDataType);
    }

    @Test
    public void getScreenMemory() {
        assertEquals(false, cpu.getScreenMemory().read(0, 0));
    }

    @Test
    public void getRegisters() {
        assertEquals("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]", cpu.getRegisters().getValues());
    }

    @Test
    public void getAddressRegister() {
        assertEquals(0, cpu.getAddressRegister().getAddress().unsignedDataType);
    }

    @Test
    public void getProgramCounter() {
        assertEquals(0, cpu.getProgramCounter().getCounter().unsignedDataType);
    }

    @Test
    public void getStack() {
        assertEquals("[]", cpu.getStack().getValues());
    }

    @Test
    public void setKeyCode() {
        cpu.setKeyCode(1);

        assertEquals(1, cpu.getKeyCode());
    }

    @Test
    public void getDelayTimer() {
        assertEquals("DelayTimer: 0", cpu.getDelayTimer().getState());
    }

    @Test
    public void getSoundTimer() {
        assertEquals("SoundTimer: 0", cpu.getSoundTimer().getState());
    }

    @Test
    public void stopCPU() {
        assertEquals(false, cpu.isStop());

        cpu.setStop(true);

        assertEquals(true, cpu.isStop());
    }

    @Test
    public void startCPU() {
        cpu.setStop(true);
        assertEquals(true, cpu.isStop());

        cpu.setStop(false);
        assertEquals(false, cpu.isStop());
    }

    @Test
    public void setPauseTrue() {
        cpu.setPause(true);
        assertEquals(true, cpu.isPause());
    }

    @Test
    public void setPauseFalse() {
        cpu.setPause(false);
        assertEquals(false, cpu.isPause());
    }

    @Test
    public void setPause() {
        cpu.setPause(true);
        assertEquals(true, cpu.isPause());

        cpu.setPause(false);
        assertEquals(false, cpu.isPause());
    }

    @Test
    public void getCycles() throws UnknownOPCodeException {
        setOpCode(0xF865);
        memory.write(0x205, new UnsignedByte((byte) 1));
        memory.write(0x206, new UnsignedByte((byte) 2));
        memory.write(0x207, new UnsignedByte((byte) 3));
        memory.write(0x208, new UnsignedByte((byte) 4));
        memory.write(0x209, new UnsignedByte((byte) 5));
        memory.write(0x20A, new UnsignedByte((byte) 6));
        memory.write(0x20B, new UnsignedByte((byte) 7));
        memory.write(0x20C, new UnsignedByte((byte) 8));
        cpu.getAddressRegister().setAddress(new UnsignedShort(0x205));

        cpu.decodeNextOpCode();

        assertEquals(1, cpu.getCycles());
    }

    @Test
    public void getOpCodeString() throws UnknownOPCodeException {
        setOpCode(0xF865);
        memory.write(0x205, new UnsignedByte((byte) 1));
        memory.write(0x206, new UnsignedByte((byte) 2));
        memory.write(0x207, new UnsignedByte((byte) 3));
        memory.write(0x208, new UnsignedByte((byte) 4));
        memory.write(0x209, new UnsignedByte((byte) 5));
        memory.write(0x20A, new UnsignedByte((byte) 6));
        memory.write(0x20B, new UnsignedByte((byte) 7));
        memory.write(0x20C, new UnsignedByte((byte) 8));
        cpu.getAddressRegister().setAddress(new UnsignedShort(0x205));

        cpu.decodeNextOpCode();

        assertEquals("f865 -> reg_load(V8, &I)", cpu.getOpCodeString());
    }

    @Test
    public void setCycles() {
        cpu.setCycles(200);

        assertEquals(200, cpu.getCycles());
    }

    @Test
    public void setOpCodeTest() {
        cpu.setOpCode("test");

        assertEquals("test", cpu.getOpCodeString());
    }

    @Test
    public void unknownOpCodeTest() {
        try {
            cpu.decodeNextOpCode();
        } catch (UnknownOPCodeException e) {
            return;
        }
        fail();
    }

    private void setOpCode(int index, int opCode) {
        int topByte = ((opCode & 0xff00) >> 8);
        int bottomByte = (opCode & 0x00ff);

        memory.write(index++, new UnsignedByte(topByte));
        memory.write(index, new UnsignedByte(bottomByte));
    }

    private void setOpCode(int opCode) {
        setOpCode(0, opCode);
    }
}