package de.nihas101.chip8.opcodes;

import de.nihas101.chip8.hardware.CentralProcessingUnit;
import de.nihas101.chip8.hardware.memory.*;
import de.nihas101.chip8.hardware.timers.DelayTimer;
import de.nihas101.chip8.hardware.timers.SoundTimer;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;
import de.nihas101.chip8.unsignedDataTypes.UnsignedShort;
import org.junit.Before;
import org.junit.Test;

import javax.sound.midi.MidiUnavailableException;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;

import static de.nihas101.chip8.hardware.memory.ScreenMemory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CentralProcessingUnitTest {
    private CentralProcessingUnit cpu;
    private Memory memory;
    private Random random;

    @Before
    public void setup() throws MidiUnavailableException {
        memory = new Memory();
        /* New random subclass for  testing, returns the same int every time */
        random = new Random(){
                    @Override
                    public int nextInt(int bound){
                        return 0x0011;
                    }
                };
        cpu = new CentralProcessingUnit(
                memory,
                new ScreenMemory(),
                new Registers(),
                new AddressRegister(),
                new ProgramCounter(new UnsignedShort((short) 0)),
                new Chip8Stack(new Stack<>()),
                new Timer("Timer"),
                new DelayTimer(){
                    @Override
                    public void decrementValue(){
                        /* DO NOTHING*/
                    }
                },
                new SoundTimer(){
                    @Override
                    public void decrementValue(){
                        /* DO NOTHING*/
                    }
                },
                random);
    }

    @Test
    public void test00E0(){   // clear screen
        int opcode = 0x00E0;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        this.cpu.getScreenMemory().write(0,0, true);
        this.cpu.getScreenMemory().write(1,1, true);
        this.cpu.getScreenMemory().write(2,2, true);
        this.cpu.getScreenMemory().write(3,3, true);

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        for (int x=0 ; x < SCREEN_WIDTH ; x++)
            for(int y=0 ; y < SCREEN_HEIGHT ; y++)
                assertEquals(false, this.cpu.getScreenMemory().read(x,y));
    }

    @Test
    public void test00EE(){   // return from subroutine
        int opcode = 0x00EE;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        this.cpu.getStack().push(new UnsignedShort((short) 0x321));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(0, this.cpu.getStack().getSize());
        assertEquals(0x321, this.cpu.getProgramCounter().getCounter().unsignedDataType);
    }

    @Test
    public void test1NNN(){   // jump to address NNN
        int opcode = 0x1123;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedShort((short) 0x123), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void test2NNN(){   // Call subroutine at NNN
        int opcode = 0x2123;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getProgramCounter().jumpTo(new UnsignedShort((short) 0x01FF));

        memory.write(0x01FF, new UnsignedByte(topByte));
        memory.write(0x0200, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // 0x01FF + 0x0002 = 0x0201
        assertEquals(new UnsignedShort((short) 0x0201), cpu.getStack().pop());
        assertEquals(new UnsignedShort((short) 0x0123), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void test3XNN(){   // Skip next instruction if Vx == NN
        int opcode = 0x3002;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        cpu.getRegisters().poke(0,new UnsignedByte((byte) 2));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedShort((short) 4), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void test4XNN(){   // Skip next instruction if Vx != NN
        int opcode = 0x4201;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        cpu.getRegisters().poke(2,new UnsignedByte((byte) 2));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        assertEquals(new UnsignedShort((short) 4), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void test5XY0(){      // Skip next instruction if Vx == Vy
        int opcode = 0x5230;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(2,new UnsignedByte((byte) 1));
        cpu.getRegisters().poke(3,new UnsignedByte((byte) 1));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedShort((short) 4), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void test6XNN(){   // 	Sets VX to NN
        int opcode = 0x6512;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(5,new UnsignedByte((byte) 100));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) 0x512), cpu.getRegisters().peek(5));
    }

    @Test
    public void test7XNN(){   // Adds NN to VX
        int opcode = 0x7701;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(7,new UnsignedByte((byte) 100));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) 101), cpu.getRegisters().peek(7));
    }

    @Test
    public void test8XY0(){   // Sets VX to the value of VY
        int opcode = 0x8780;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(7,new UnsignedByte((byte) 90));
        cpu.getRegisters().poke(8,new UnsignedByte((byte) 101));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) 101), cpu.getRegisters().peek(7));
    }

    @Test
    public void test8XY1(){   // 	Sets VX to VX or VY (Bitwise OR operation)
        int opcode = 0x89A1;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0x9,new UnsignedByte((byte) 0b110));
        cpu.getRegisters().poke(0xA,new UnsignedByte((byte) 0b001));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) 0b111), cpu.getRegisters().peek(9));
    }

    @Test
    public void test8XY2(){   // Sets VX to VX and VY (Bitwise AND operation)
        int opcode = 0x8BC2;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        cpu.getRegisters().poke(0xB,new UnsignedByte((byte) 0b110));
        cpu.getRegisters().poke(0xC,new UnsignedByte((byte) 0b101));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) 0b100), cpu.getRegisters().peek(0xB));
    }

    @Test
    public void test8XY3(){   // Sets VX to VX xor VY
        int opcode = 0x8DE3;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0xD,new UnsignedByte((byte) 0b110));
        cpu.getRegisters().poke(0xE,new UnsignedByte((byte) 0b101));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) 0b011), cpu.getRegisters().peek(0xD));
    }

    @Test
    public void test8XY4NoCarry(){   // Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't
        int opcode = 0x8DE4;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0xD,new UnsignedByte((byte) 0b001));
        cpu.getRegisters().poke(0xE,new UnsignedByte((byte) 0b001));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) 0b010), cpu.getRegisters().peek(0xD));
        assertEquals(new UnsignedByte((byte) 0), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XY4Carry(){   // Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't
        int opcode = 0x8DE4;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0xD,new UnsignedByte((byte) 0xFF));
        cpu.getRegisters().poke(0xE,new UnsignedByte((byte) 0x01));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) 0), cpu.getRegisters().peek(0xD));
        assertEquals(new UnsignedByte((byte) 1), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XY5Borrow(){   // VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
        int opcode = 0x8015;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0x0,new UnsignedByte((byte) 0x01));
        cpu.getRegisters().poke(0x1,new UnsignedByte((byte) 0x02));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) 0xFF), cpu.getRegisters().peek(0x0));
        assertEquals(new UnsignedByte((byte) 0), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XY5NoBorrow(){   // VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
        int opcode = 0x8015;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0x0,new UnsignedByte((byte) 0x01));
        cpu.getRegisters().poke(0x1,new UnsignedByte((byte) 0x01));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) 0), cpu.getRegisters().peek(0x0));
        assertEquals(new UnsignedByte((byte) 1), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XY6(){   // Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift.
        int opcode = 0x8126;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0x1,new UnsignedByte((byte) 0x0F));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) (0x0F >> 1)), cpu.getRegisters().peek(0x1));
        assertEquals(new UnsignedByte((byte) 0x1), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XY7Borrow(){   // Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
        int opcode = 0x8237;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0x3,new UnsignedByte((byte) 0x01));
        cpu.getRegisters().poke(0x2,new UnsignedByte((byte) 0x0F));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) (0x01 - 0x0F)), cpu.getRegisters().peek(0x2));
        assertEquals(new UnsignedByte((byte) 0x0), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XY7NoBorrow(){   // Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
        int opcode = 0x8237;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0x3,new UnsignedByte((byte) 0x0F));
        cpu.getRegisters().poke(0x2,new UnsignedByte((byte) 0x01));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) 0x0E), cpu.getRegisters().peek(0x2));
        assertEquals(new UnsignedByte((byte) 0x1), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test8XYE(){   // Shifts VX left by one. VF is set to the value of the most significant bit of VX before the shift.
        int opcode = 0x834E;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0x3,new UnsignedByte((byte) 0xFF));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) 0xFE), cpu.getRegisters().peek(0x3));
        assertEquals(new UnsignedByte((byte) 0x1), cpu.getRegisters().peek(0xF));
    }

    @Test
    public void test9XYE(){   // Skips the next instruction if VX doesn't equal VY. (Usually the next instruction is a jump to skip a code block)
        int opcode = 0x945E;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0x4,new UnsignedByte((byte) 0x0F));
        cpu.getRegisters().poke(0x5,new UnsignedByte((byte) 0x0E));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedShort((short) 4), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void testANNN(){   // Sets I to the address NNN.
        int opcode = 0xA123;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedShort((short) 0x123), cpu.getAddressRegister().getAddress());
    }

    @Test
    public void testBNNN(){   // Jumps to the address NNN plus V0.
        int opcode = 0xB123;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0, new UnsignedByte((byte) 0x03));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedShort((short) 0x126), cpu.getProgramCounter().getCounter());
    }

    @Test
    public void testCXNN(){   // Sets VX to the result of a bitwise and operation on a random number (Typically: 0 to 255) and NN.
        int opcode = 0xC512;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedByte((byte) (0x12 & random.nextInt(255))), cpu.getRegisters().peek(5));
    }

    @Test
    public void testDXYNNoCollision(){   // Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels
        int opcode = 0xD01A;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        cpu.getRegisters().poke(0x0, new UnsignedByte((byte) 0));
        cpu.getRegisters().poke(0x1, new UnsignedByte((byte) 0));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));
        /* Set memory for sprite to display */
        memory.write(0x200, new UnsignedByte((byte) 0xFF));
        memory.write(0x201, new UnsignedByte((byte) 0xF0));
        memory.write(0x202, new UnsignedByte((byte) 0xFF));
        memory.write(0x203, new UnsignedByte((byte) 0xF0));
        memory.write(0x204, new UnsignedByte((byte) 0xFF));
        memory.write(0x205, new UnsignedByte((byte) 0XF0));
        memory.write(0x206, new UnsignedByte((byte) 0xFF));
        memory.write(0x207, new UnsignedByte((byte) 0xF0));
        memory.write(0x208, new UnsignedByte((byte) 0xFF));
        memory.write(0x209, new UnsignedByte((byte) 0xF0));
        memory.write(0x20A, new UnsignedByte((byte) 0xFF));
        cpu.getAddressRegister().setAddress(new UnsignedShort((short) 0x200));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(true, cpu.getScreenMemory().read(0,0));
        assertEquals(new UnsignedByte((byte) 0), cpu.getRegisters().peek(0xF));
        assertEquals(new UnsignedShort((short) 0x200), cpu.getAddressRegister().getAddress());
    }

    @Test
    public void testDXYNCollision(){   // Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels
        int opcode = 0xD01A;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        /* Write into screenmemory */
        cpu.getScreenMemory().write(0,0,true);
        /* Set memory for sprite to display */
        memory.write(0x200, new UnsignedByte((byte) 0xFF));
        memory.write(0x201, new UnsignedByte((byte) 0xF0));
        memory.write(0x202, new UnsignedByte((byte) 0xFF));
        memory.write(0x203, new UnsignedByte((byte) 0xF0));
        memory.write(0x204, new UnsignedByte((byte) 0xFF));
        memory.write(0x205, new UnsignedByte((byte) 0XF0));
        memory.write(0x206, new UnsignedByte((byte) 0xFF));
        memory.write(0x207, new UnsignedByte((byte) 0xF0));
        memory.write(0x208, new UnsignedByte((byte) 0xFF));
        memory.write(0x209, new UnsignedByte((byte) 0xF0));
        memory.write(0x20A, new UnsignedByte((byte) 0xFF));
        cpu.getAddressRegister().setAddress(new UnsignedShort((short) 0x200));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(false, cpu.getScreenMemory().read(0,0));
        assertEquals(new UnsignedByte((byte) 1), cpu.getRegisters().peek(0xF));
        assertEquals(new UnsignedShort((short) 0x200), cpu.getAddressRegister().getAddress());
    }

    @Test
    public void testEX9E(){   // Skips the next instruction if the key stored in VX is pressed. (Usually the next instruction is a jump to skip a code block)
        int opcode = 0xEC9E;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        /* Set key and key to look for */
        this.cpu.setKeyCode(1);
        this.cpu.getRegisters().poke(0xC,new UnsignedByte((byte) 1));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedShort((short) 4), this.cpu.getProgramCounter().getCounter());
    }

    @Test
    public void testEXA1(){   // Skips the next instruction if the key stored in VX isn't pressed. (Usually the next instruction is a jump to skip a code block)
        int opcode = 0xEEA1;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        /* Set key and key to look for */
        this.cpu.setKeyCode(0);
        this.cpu.getRegisters().poke(0xE,new UnsignedByte((byte) 1));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(new UnsignedShort((short) 4), this.cpu.getProgramCounter().getCounter());
    }

    @Test
    public void testFX07(){   // Sets VX to the value of the delay timer.
        int opcode = 0xF107;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        this.cpu.getDelayTimer().setValue(10);

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(10, this.cpu.getRegisters().peek(1).unsignedDataType);
    }

    @Test
    public void testFX0A(){   // A key press is awaited, and then stored in VX. (Blocking BinaryOperation. All instruction halted until next key event)
        int opcode = 0xF30A;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        /* Set a keycode after waiting a little bit */
        new Thread(() -> {
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            /* Set a keycode */
            this.cpu.setKeyCode(1);
        }).start();

        /* Check if keycode was set in between */
        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        /* Check if correct keycode was set */
        assertEquals(1, this.cpu.getRegisters().peek(3).unsignedDataType);
    }

    @Test
    public void testFX15(){   // Sets the delay timer to VX.
        int opcode = 0xF815;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        this.cpu.getRegisters().poke(8, new UnsignedByte((byte) 10));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(10, this.cpu.getDelayTimer().getValue());
    }

    @Test
    public void testFX18(){   // Sets the sound timer to VX.
        int opcode = 0xFB18;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        this.cpu.getRegisters().poke(0xB, new UnsignedByte((byte) 10));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        assertEquals(10, this.cpu.getSoundTimer().getValue());
    }

    @Test
    public void testFX1E(){   // Adds VX to I
        int opcode = 0xF91E;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        /* Setup registers */
        this.cpu.getRegisters().poke(0x9, new UnsignedByte((byte) 10));
        this.cpu.getAddressRegister().setAddress(new UnsignedShort((short) 0x200));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(0x20A, this.cpu.getAddressRegister().getAddress().unsignedDataType);
    }

    @Test
    public void testFX29(){   // Sets I to the location of the sprite for the character in VX. Characters 0-F (in hexadecimal) are represented by a 4x5 font.
        int opcode = 0xF429;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testFX33(){   // Stores the binary-coded decimal representation of VX, with the most significant of three digits at the address in I, the middle digit at I plus 1, and the least significant digit at I plus 2. (In other words, take the decimal representation of VX, place the hundreds digit in hardware at location in I, the tens digit at location I+1, and the ones digit at location I+2.)
        int opcode = 0xF733;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        this.cpu.getRegisters().poke(0x7, new UnsignedByte((byte) 123));
        this.cpu.getAddressRegister().setAddress(new UnsignedShort((short) 0x200));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(1, this.memory.read(0x200).unsignedDataType);
        assertEquals(2, this.memory.read(0x201).unsignedDataType);
        assertEquals(3, this.memory.read(0x202).unsignedDataType);
    }

    @Test
    public void testFX55(){   // Stores V0 to VX (including VX) in hardware starting at address I.
        int opcode = 0xF555;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        this.cpu.getRegisters().poke(0, new UnsignedByte((byte) 1));
        this.cpu.getRegisters().poke(1, new UnsignedByte((byte) 2));
        this.cpu.getRegisters().poke(2, new UnsignedByte((byte) 3));
        this.cpu.getRegisters().poke(3, new UnsignedByte((byte) 4));
        this.cpu.getRegisters().poke(4, new UnsignedByte((byte) 5));
        this.cpu.getRegisters().poke(5, new UnsignedByte((byte) 6));

        this.cpu.getAddressRegister().setAddress(new UnsignedShort((short) 0x200));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(1, this.memory.read(0x200).unsignedDataType);
        assertEquals(2, this.memory.read(0x201).unsignedDataType);
        assertEquals(3, this.memory.read(0x202).unsignedDataType);
        assertEquals(4, this.memory.read(0x203).unsignedDataType);
        assertEquals(5, this.memory.read(0x204).unsignedDataType);
        assertEquals(6, this.memory.read(0x205).unsignedDataType);
    }

    @Test
    public void testFX65(){   // Fills V0 to VX (including VX) with values from hardware starting at address I
        int opcode = 0xF865;
        byte topByte = (byte) ((opcode & 0xff00) >> 8);
        byte bottomByte = (byte) (opcode & 0x00ff);

        this.memory.write(0x205, new UnsignedByte((byte) 1));
        this.memory.write(0x206, new UnsignedByte((byte) 2));
        this.memory.write(0x207, new UnsignedByte((byte) 3));
        this.memory.write(0x208, new UnsignedByte((byte) 4));
        this.memory.write(0x209, new UnsignedByte((byte) 5));
        this.memory.write(0x20A, new UnsignedByte((byte) 6));
        this.memory.write(0x20B, new UnsignedByte((byte) 7));
        this.memory.write(0x20C, new UnsignedByte((byte) 8));

        this.cpu.getAddressRegister().setAddress(new UnsignedShort((short) 0x205));

        memory.write(0, new UnsignedByte(topByte));
        memory.write(1, new UnsignedByte(bottomByte));

        try {
            cpu.decodeNextOpCode();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(1, this.cpu.getRegisters().peek(0).unsignedDataType);
        assertEquals(2, this.cpu.getRegisters().peek(1).unsignedDataType);
        assertEquals(3, this.cpu.getRegisters().peek(2).unsignedDataType);
        assertEquals(4, this.cpu.getRegisters().peek(3).unsignedDataType);
        assertEquals(5, this.cpu.getRegisters().peek(4).unsignedDataType);
        assertEquals(6, this.cpu.getRegisters().peek(5).unsignedDataType);
        assertEquals(7, this.cpu.getRegisters().peek(6).unsignedDataType);
        assertEquals(8, this.cpu.getRegisters().peek(7).unsignedDataType);
    }
}