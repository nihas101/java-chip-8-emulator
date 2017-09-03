package de.nihas101.chip8.hardware;

import de.nihas101.chip8.hardware.memory.*;
import de.nihas101.chip8.hardware.timers.DelayTimer;
import de.nihas101.chip8.hardware.timers.SoundTimer;
import de.nihas101.chip8.opcodes.OPCode;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;
import de.nihas101.chip8.unsignedDataTypes.UnsignedShort;
import de.nihas101.chip8.debug.Debuggable;

import javax.sound.midi.MidiChannel;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static de.nihas101.chip8.utils.Constants.HERTZ_60;
import static de.nihas101.chip8.utils.Constants.NO_KEY;

/**
 * A class representing a central processing unit of a Chip-8
 */
public class Chip8CentralProcessingUnit implements Debuggable {

    private final Chip8Memory memory;
    private final Chip8Registers registers;
    private final Chip8AddressRegister addressRegister;
    private final Chip8ProgramCounter programCounter;
    private final Chip8Stack stack;
    private final ScreenMemory screenMemory;

    private final Random random;

    private int keyCode = NO_KEY;

    private final Timer timer;

    private final DelayTimer delayTimer;
    private final SoundTimer soundTimer;

    private String opCodeString = "";
    private int cycles;
    private boolean stop = false;

    public Chip8CentralProcessingUnit(Chip8Memory memory, ScreenMemory screenMemory, Chip8Registers registers,
                                      Chip8AddressRegister addressRegister, Chip8ProgramCounter programCounter,
                                      Chip8Stack stack, Timer timer, DelayTimer delayTimer, SoundTimer soundTimer,
                                      Random random, MidiChannel midiChannel){
        this.memory = memory;
        this.screenMemory = screenMemory;
        this.registers = registers;
        this.addressRegister = addressRegister;

        this.programCounter = programCounter;
        this.stack = stack;
        this.random = random;

        /* Set up timer */
        this.timer = timer;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, HERTZ_60, HERTZ_60);

        this.delayTimer = delayTimer;
        this.soundTimer = soundTimer;
        soundTimer.setOnValue(() -> { if(midiChannel != null) midiChannel.noteOn(60, 100); });
        soundTimer.setOnZero(() -> { if(midiChannel != null) midiChannel.noteOff(60); });

        cycles = 0;
    }

    /**
     * Reset the {@link Chip8CentralProcessingUnit}
     */
    public void reset(){
        cycles = 0;
        registers.clear();
        programCounter.jumpTo(new UnsignedShort((short) 0x200));
        screenMemory.reset();
        addressRegister.setAddress(new UnsignedShort((short) 0));
        delayTimer.reset();
        soundTimer.reset();
        stack.clear();
    }

    public void clearMemory(){
        memory.clear();
    }

    /**
     * Decrements both timers
     */
    private void updateTimer() {
        delayTimer.decrementValue();
        soundTimer.decrementValue();
    }

    /**
     * Fetches the next opcode from the hardware
     * @throws Exception Thrown if the given opcode is unknown
     */
    public void decodeNextOpCode() throws Exception {
        OPCode opCode = getNextOpCode();
        opCodeString = Integer.toHexString(opCode.getOpCode()) + " -> ";
        decodeOpCode(opCode);
        cycles++;
    }

    /**
     * Decodes an opcode and executes the appropriate instruction
     * See en.wikipedia.org/wiki/CHIP-8#Opcode_table for a breakdown of the codes
     * @param opCode The opcode to be decoded
     * @throws Exception Thrown if the given opcode is unknown
     */
    private void decodeOpCode(OPCode opCode) throws Exception {
        switch (opCode.getByte(0)){
            case 0x0: opCode0XYZ(opCode); break;
            case 0x1: gotoOp(opCode.applyMask(0x0FFF)); break;
            case 0x2: callSubRoutine(opCode.applyMask(0x0FFF)); break;
            case 0x3: {
                /* Extract X and NN from opCode */
                skipIfEqual(opCode.getByte(1), new UnsignedByte((byte) opCode.applyMask(0x00FF)));
                break;
            }
            case 0x4: {
                /* Extract X and NN from opCode */
                skipIfNotEqual(opCode.getByte(1), new UnsignedByte((byte) opCode.applyMask(0x00FF)));
                break;
            }
            case 0x5: skipIfEqualReg(opCode.getByte(1), opCode.getByte(2)); break;
            case 0x6: {
                /* Extract X and NN from opCode */
                setRegister(opCode.getByte(1), new UnsignedByte((byte) opCode.applyMask(0x00FF)));
                break;
            }
            case 0x7: {
                /* Extract X and NN from opCode */
                addRegister(opCode.getByte(1), new UnsignedByte((byte) opCode.applyMask(0x00FF)));
                break;
            }
            case 0x8: opCode8XYN(opCode); break;
            case 0x9: skipIfNotEqualReg(opCode.getByte(1), opCode.getByte(2)); break;
            case 0xA: setAddress(new UnsignedShort((short) opCode.applyMask(0x0FFF))); break;
            case 0xB: setPC(new UnsignedShort((short) opCode.applyMask(0x0FFF))); break;
            case 0xC: randomAND(opCode.getByte(1), new UnsignedByte((byte) opCode.applyMask(0x00FF))); break;
            case 0xD: drawSprite(opCode.getByte(1), opCode.getByte(2), opCode.getByte(3)); break;
            case 0xE: opCodeEXYZ(opCode); break;
            case 0xF: opCodeFXYZ(opCode); break;
            default: throw new Exception("Unknown OpCode encountered: " + Integer.toHexString(opCode.getOpCode()));
        }
    }

    /**
     * Further decodes {@link OPCode} instances that begin with 0xF
     * @param opCode The {@link OPCode} to be decoded
     * @throws Exception If an unknown {@link OPCode} is encountered
     */
    private void opCodeFXYZ(OPCode opCode) throws Exception {
        switch (opCode.getByte(2)){
            case 0x0: opCodeFX0Y(opCode); break;
            case 0x1: opCodeFX1Y(opCode); break;
            case 0x2: gotoSpriteAddress(opCode.getByte(1)); break;
            case 0x3: opCodeFX3Y(opCode); break;
            case 0x5: opCodeFX5Y(opCode); break;
            case 0x6: opCodeFX6Y(opCode); break;
            default: throw new Exception("Unknown OpCode encountered: " + Integer.toHexString(opCode.getOpCode()));
        }
    }

    /**
     * Sets the address register to the sprite for the character
     * in VX
     * @param Vx The register holding the character
     */
    private void gotoSpriteAddress(int Vx) {
        opCodeString += "I=sprite_addr[" + Integer.toHexString(Vx) + "]";
        int address = this.registers.peek(Vx).apply(x -> x*5).unsignedDataType;
        this.addressRegister.setAddress(new UnsignedShort((short) address));
    }

    /**
     * Further decodes {@link OPCode} instances that in the form 0xF_6_
     * @param opCode The {@link OPCode} to be decoded
     * @throws Exception If an unknown {@link OPCode} is encountered
     */
    private void opCodeFX6Y(OPCode opCode) throws Exception {
        switch (opCode.getByte(3)){
            case 0x5: loadReg(opCode.getByte(1));break;
            default: throw new Exception("Unknown OpCode encountered: " + Integer.toHexString(opCode.getOpCode()));
        }
    }

    /**
     * Further decodes {@link OPCode} instances that in the form 0xF_5_
     * @param opCode The {@link OPCode} to be decoded
     * @throws Exception If an unknown {@link OPCode} is encountered
     */
    private void opCodeFX5Y(OPCode opCode) throws Exception {
        switch (opCode.getByte(3)){
            case 0x5: dumpReg(opCode.getByte(1)); break;
            default: throw new Exception("Unknown OpCode encountered: " + Integer.toHexString(opCode.getOpCode()));
        }
    }

    /**
     * Dumps the registers V0 ... Vx into the memory starting at the location the {@link Chip8AddressRegister} points at
     * @param Vx The index of the registers to be dumped up to
     */
    private void dumpReg(int Vx) {
        opCodeString += "reg_dump(V" + Integer.toHexString(Vx) + ", &I)";
        for(int i=0 ; i <= Vx ; i++){
            int address = this.addressRegister.getAddress().apply((x,y) -> x+y, new UnsignedShort((short) i)).unsignedDataType;
            this.memory.write(address, this.registers.peek(i));
        }
    }

    /**
     * Loads the registers V0 ... Vx into the memory starting at the location the {@link Chip8AddressRegister} points at
     * @param Vx The index of the registers to be loaded up to
     */
    private void loadReg(int Vx){
        opCodeString += "reg_load(V" + Integer.toHexString(Vx) + ",&I)";
        for(int i=0 ; i <= Vx ; i++){
            int address = this.addressRegister.getAddress().apply((x,y) -> x+y, new UnsignedShort((short) i)).unsignedDataType;
            this.registers.poke(i,this.memory.read(address));
        }
    }

    /**
     * Further decodes {@link OPCode} instances that in the form 0xF_3_
     * @param opCode The {@link OPCode} to be decoded
     * @throws Exception If an unknown {@link OPCode} is encountered
     */
    private void opCodeFX3Y(OPCode opCode) throws Exception {
        switch (opCode.getByte(3)){
            case 0x3: storeBinaryCodedDecimals(opCode.getByte(1)); break;
            default: throw new Exception("Unknown OpCode encountered: " + Integer.toHexString(opCode.getOpCode()));
        }
    }

    /**
     * Stores a decimal in the memory
     * @param Vx The index of the register from which the decimal should be read
     */
    private void storeBinaryCodedDecimals(int Vx) {
        opCodeString += "BCD V" + Integer.toHexString(Vx);

        int decimalValue = this.registers.peek(Vx).unsignedDataType;

        UnsignedByte hundreds = new UnsignedByte((byte) (decimalValue / 100));
        UnsignedByte tens = new UnsignedByte((byte) ((decimalValue % 100) / 10));
        UnsignedByte units = new UnsignedByte((byte) ((decimalValue % 100) % 10));

        this.memory.write(this.addressRegister.getAddress().unsignedDataType, hundreds);
        this.memory.write(this.addressRegister.getAddress().apply(x -> x+1).unsignedDataType, tens);
        this.memory.write(this.addressRegister.getAddress().apply(x -> x+2).unsignedDataType, units);
    }

    /**
     * Further decodes {@link OPCode} instances that in the form 0xF_0_
     * @param opCode The {@link OPCode} to be decoded
     * @throws Exception If an unknown {@link OPCode} is encountered
     */
    private void opCodeFX0Y(OPCode opCode) throws Exception {
        switch (opCode.getByte(3)){
            case 0x7: getDelayTimer(opCode.getByte(1)); break;
            case 0xA: waitForInput(opCode.getByte(1)); break;
            default: throw new Exception("Unknown OpCode encountered: " + Integer.toHexString(opCode.getOpCode()));
        }
    }

    /**
     * Further decodes {@link OPCode} instances that in the form 0xF_1_
     * @param opCode The {@link OPCode} to be decoded
     * @throws Exception If an unknown {@link OPCode} is encountered
     */
    private void opCodeFX1Y(OPCode opCode) throws Exception {
        switch (opCode.getByte(3)){
            case 0x5: setDelayTimer(opCode.getByte(1)); break;
            case 0x8: setSoundTimer(opCode.getByte(1)); break;
            case 0xE: setAddressReg(opCode.getByte(1)); break;
            default: throw new Exception("Unknown OpCode encountered: " + Integer.toHexString(opCode.getOpCode()));
        }
    }

    /**
     * Sets the {@link Chip8AddressRegister} to the value of the register with index Vx
     * @param Vx The index of the register
     */
    private void setAddressReg(int Vx) {
        opCodeString += "I +=V" + Integer.toHexString(Vx);

        UnsignedShort result = this.registers.peek(Vx).apply((x,y) -> x+y, this.addressRegister.getAddress());
        this.addressRegister.setAddress(result);
    }

    /**
     * Sets the {@link SoundTimer} to the value in register Vx
     * @param Vx The index of the register
     */
    private void setSoundTimer(int Vx) {
        opCodeString += "sound_timer(V" + Integer.toHexString(Vx) + ")";

        this.soundTimer.setValue(this.registers.peek(Vx).unsignedDataType);
    }

    /**
     * Sets the {@link DelayTimer} to the value in register Vx
     * @param Vx The index of the register
     */
    private void setDelayTimer(int Vx) {
        opCodeString += "delay_timer(V" + Integer.toHexString(Vx) + ")";

        this.delayTimer.setValue(this.registers.peek(Vx).unsignedDataType);
    }

    /**
     * Waits for input and writes it into register Vx
     * @param Vx The index of the register
     */
    private void waitForInput(int Vx) {
        opCodeString += "V" + Integer.toHexString(Vx) + " = get_key()";

        /* Halt execution until a key is pressed */
        while(keyCode == NO_KEY && !stop){
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.registers.poke(Vx, new UnsignedByte((byte) keyCode));
    }

    /**
     * Further decodes {@link OPCode} instances beginning with 0x0
     * @param opCode The {@link OPCode} to be decoded
     * @throws Exception If an unknown {@link OPCode} is encountered
     */
    private void opCode0XYZ(OPCode opCode) throws Exception {
        switch (opCode.getByte(1)){
            case 0x0: {
                if(opCode.getByte(3) == 0) {
                    this.getScreenMemory().reset(); break;
                } else {
                    returnFromSubRoutine(); break;
                }
            }
            default:{
                /* Call RCA 1802 program at address NNN. Not necessary for most ROMs. */
                throw new Exception("RCA 1802 is not  supported: " + Integer.toHexString(opCode.getOpCode())); //break;
            }
        }
    }

    /**
     * Sets the {@link Chip8ProgramCounter} to a value popped from a stack
     */
    private void returnFromSubRoutine() {
        opCodeString += "return;";
        this.programCounter.jumpTo(this.stack.pop());
    }

    /**
     * Further decodes {@link OPCode} instances beginning with 0x8
     * @param opCode The {@link OPCode} to be decoded
     * @throws Exception If an unknown {@link OPCode} is encountered
     */
    private void opCode8XYN(OPCode opCode) throws Exception {
        switch (opCode.getByte(3)){
            case 0x0: assign(opCode.getByte(1), opCode.getByte(2)); break;
            case 0x1: assignOR(opCode.getByte(1), opCode.getByte(2)); break;
            case 0x2: assignAND(opCode.getByte(1), opCode.getByte(2)); break;
            case 0x3: assignXOR(opCode.getByte(1), opCode.getByte(2)); break;
            case 0x4: add(opCode.getByte(1), opCode.getByte(2)); break;
            case 0x5: sub(opCode.getByte(1), opCode.getByte(2), opCode.getByte(1)); break;
            case 0x6: shiftRight(opCode.getByte(1)); break;
            case 0x7: sub(opCode.getByte(2), opCode.getByte(1), opCode.getByte(1)); break;
            case 0xE: shiftLeft(opCode.getByte(1)); break;
            default: throw new Exception("Unknown OpCode encountered: " + Integer.toHexString(opCode.getOpCode()));
        }
    }

    /**
     * Further decodes {@link OPCode} instances beginning with 0xE
     * @param opCode The {@link OPCode} to be decoded
     * @throws Exception If an unknown {@link OPCode} is encountered
     */
    private void opCodeEXYZ(OPCode opCode) throws Exception {
        switch (opCode.getByte(2)){
            case 0x9: skipIfKeyPressed(opCode.getByte(1)); break;
            case 0xA: skipIfKeyNotPressed(opCode.getByte(1)); break;
            default: throw new Exception("Unknown OpCode encountered: " + Integer.toHexString(opCode.getOpCode()));
        }
    }

    /**
     * Writes the {@link DelayTimer} into the register Vx
     * @param Vx The index of the register
     */
    private void getDelayTimer(int Vx) {
        opCodeString += "V" + Integer.toHexString(Vx) + " = get_delay()";
        this.getRegisters().poke(Vx, new UnsignedByte((byte) delayTimer.getValue()));
    }

    /**
     * Skips the next instruction if no key is pressed
     * @param Vx The value of the register
     */
    private void skipIfKeyNotPressed(int Vx) {
        opCodeString += "if(key()!=V" + Integer.toHexString(Vx) + ")";

        if(!(this.registers.peek(Vx).unsignedDataType == keyCode))
            this.getProgramCounter().incrementCounterN(2);
    }

    /**
     * Skips the next instruction if a key is pressed
     * @param Vx The value of the register
     */
    private void skipIfKeyPressed(int Vx) {
        opCodeString += "if(key()==V" + Integer.toHexString(Vx) + ")";

        if(this.registers.peek(Vx).unsignedDataType == keyCode)
            this.getProgramCounter().incrementCounterN(2);
    }

    /**
     * Draws 8-pixels-wide sprites into the {@link ScreenMemory}
     * @param Vx The horizontal index of the sprite
     * @param Vy The lateral index of the sprite
     * @param height The height of the pixel
     */
    private void drawSprite(int Vx, int Vy, int height) {
        opCodeString += "draw(V" + Integer.toHexString(Vx) + ",V" + Integer.toHexString(Vy) + "," + height + ")";

        /* Reset flag-register */
        this.registers.poke(0xF, new UnsignedByte((byte) 0));

        /* Get coordinates of sprite */
        int coordX = this.registers.peek(Vx).unsignedDataType;
        int coordY = this.registers.peek(Vy).unsignedDataType;

        UnsignedShort yLine = new UnsignedShort((byte) 0);
        for( ; yLine.unsignedDataType < height ; yLine = yLine.apply(v -> v+1)){
            /* Get memory at addressRegister + yLine */
            UnsignedByte data = this.memory.read(this.addressRegister.getAddress().apply((x1,y1) -> x1+y1, yLine).unsignedDataType);
            /* Draw sprite */
            int mask = 0x80;
            for(int xPixel = 0 ; xPixel < 8 ; xPixel++){
                if(0 != data.apply((x,y) -> x&y, new UnsignedByte((byte) mask)).unsignedDataType){
                    int x = coordX + xPixel;
                    int y = coordY + yLine.unsignedDataType;
                    /* Check if a collision occurred */
                    if(screenMemory.read(x,y)) this.registers.poke(0xF, new UnsignedByte((byte) 1));
                    /* Set the pixel */
                    screenMemory.write(x, y, !screenMemory.read(x, y));
                }
                mask = mask >> 1;
            }
        }
    }

    /**
     * Uses the AND operation on the given number and a random number and writes it into Vx
     * @param Vx The register
     * @param unsignedByte The number
     */
    private void randomAND(int Vx, UnsignedByte unsignedByte) {
        opCodeString += "V" + Integer.toHexString(Vx) + "=rand()&" + unsignedByte.unsignedDataType;

        UnsignedByte result = unsignedByte.apply((x,y) -> x&y, new UnsignedByte((byte) random.nextInt(255)));
        this.getRegisters().poke(Vx, result);
    }

    /**
     * Sets the {@link Chip8AddressRegister} to the given address
     * @param address The new address of the I register
     */
    private void setAddress(UnsignedShort address) {
        opCodeString += "I = "+ Integer.toHexString(address.unsignedDataType);

        this.addressRegister.setAddress(address);
    }

    /**
     * Sets the {@link Chip8ProgramCounter} to the given address
     * @param address The new address of the {@link Chip8ProgramCounter}
     */
    private void setPC(UnsignedShort address) {
        opCodeString += "PC=V0+" + Integer.toHexString(address.unsignedDataType);

        UnsignedShort result = this.getRegisters().peek(0).apply((x, y) -> x+y, address);
        this.getProgramCounter().jumpTo(result);
    }

    /**
     * Sets the register Vx to the given unsignedByte
     * @param Vx The register which will hold the unsignedByte
     * @param unsignedByte The unsigned byte to be written into the register
     */
    private void setRegister(int Vx, UnsignedByte unsignedByte){
        opCodeString += "V" + Integer.toHexString(Vx) + " = " + Integer.toHexString(unsignedByte.unsignedDataType);

        registers.poke(Vx, unsignedByte);
    }

    /**
     * Adds the unsigned byte to the register Vx
     * @param Vx The register which will hold the result of this operation
     * @param unsignedByte The unsigned byte to be added to the register
     */
    private void addRegister(int Vx, UnsignedByte unsignedByte){
        opCodeString += "V" + Integer.toHexString(Vx) + " += " + Integer.toHexString(unsignedByte.unsignedDataType);

        registers.poke(Vx, registers.peek(Vx).apply((x, y) -> x+y, unsignedByte) );
    }

    /**
     * 	Sets Vx to the value of Vy
     * @param Vx The register which will hold the result of this operation
     * @param Vy The register which value will replace the one in Vx
     */
    private void assign(int Vx, int Vy) {
        opCodeString += "V" + Integer.toHexString(Vx) + "=V" + Integer.toHexString(Vy);

        registers.poke(Vx, registers.peek(Vy));
    }

    /**
     * 	Sets Vx to the value of Vx | Vy
     * @param Vx The register which will hold the result of this operation
     * @param Vy The register which value will replace the one in Vx
     */
    private void assignOR(int Vx, int Vy) {
        opCodeString += "V" + Integer.toHexString(Vx) + "=V" + Integer.toHexString(Vx) + "|V" + Integer.toHexString(Vy);

        registers.poke(Vx, registers.peek(Vx).apply((x, y) -> x|y, registers.peek(Vy)));
    }

    /**
     * 	Sets Vx to the value of Vx & Vy
     * @param Vx The register which will hold the result of this operation
     * @param Vy The register which value will replace the one in Vx
     */
    private void assignAND(int Vx, int Vy) {
        opCodeString += "V" + Integer.toHexString(Vx) + "=V" + Integer.toHexString(Vx) + "&V" + Integer.toHexString(Vy);

        registers.poke(Vx, registers.peek(Vx).apply((x, y) -> x&y, registers.peek(Vy)));
    }

    /**
     * 	Sets Vx to the value of Vx XOR Vy
     * @param Vx The register which will hold the result of this operation
     * @param Vy The register which value will replace the one in Vx
     */
    private void assignXOR(int Vx, int Vy) {
        opCodeString += "V" + Integer.toHexString(Vx) + "=V" + Integer.toHexString(Vx) + "^V" + Integer.toHexString(Vy);

        registers.poke(Vx, registers.peek(Vx).apply((x, y) -> x^y, registers.peek(Vy)));
    }

    /**
     * 	Adds Vy to Vx and saves the result in Vx, VF is set to 1 if a carry occurs
     * @param Vx The register which will hold the result of this operation
     * @param Vy The register which value will replace the one in Vx
     */
    private void add(int Vx, int Vy) {
        opCodeString += "V" + Integer.toHexString(Vx) + " += V" + Integer.toHexString(Vy);

        /* Set carry flag */
        if((registers.peek(Vx).unsignedDataType + registers.peek(Vy).unsignedDataType) > 255)
            registers.poke(0xF, new UnsignedByte((byte) 1));
        else registers.poke(0xF, new UnsignedByte((byte) 0));
        /* Perform operation */
        registers.poke(Vx, registers.peek(Vx).apply((x, y) -> x+y, registers.peek(Vy)));
    }

    /**
     * 	Subtracts Vy from Vx and saves the result in Vx, VF is set to 0 if a borrow occurs
     * @param Vx The first parameter/register
     * @param Vy The second parameter/register
     * @param Vz The register which will hold the result of the operation
     */
    private void sub(int Vx, int Vy, int Vz) {
        opCodeString += "V" + Integer.toHexString(Vx) + " -= V" + Integer.toHexString(Vy);

        /* Set borrow flag */
        if((registers.peek(Vx).unsignedDataType - registers.peek(Vy).unsignedDataType) < 0)
            registers.poke(0xF, new UnsignedByte((byte) 0));
        else registers.poke(0xF, new UnsignedByte((byte) 1));
        /* Perform operation */
        registers.poke(Vz, registers.peek(Vx).apply((x, y) -> x-y, registers.peek(Vy)));
    }

    /**
     * Shifts the value stored in Vx to the right by one
     * @param Vx The index of the register
     */
    private void shiftRight(int Vx){
        opCodeString += "V" + Integer.toHexString(Vx) + " >> 1";

        /* Calc flag and result */
        UnsignedByte leastSignificantBit = registers.peek(Vx).apply(x -> x & 1);
        UnsignedByte result = registers.peek(Vx).apply(x -> x >> 1);

        registers.poke(Vx, result);
        registers.poke(0xF, leastSignificantBit);
    }

    /**
     * Shifts the value stored in Vx to the left by one
     * @param Vx The index of the register
     */
    private void shiftLeft(int Vx) {
        opCodeString += "V" + Integer.toHexString(Vx) + " << 1";

        /* Calc flag and result */
        UnsignedByte mostSignificantBit = registers.peek(Vx).apply(x -> (x & 0x80) >> 7);
        UnsignedByte result = registers.peek(Vx).apply(x -> x << 1);

        registers.poke(Vx, result);
        registers.poke(0xF, mostSignificantBit);
    }

    /**
     * Skips the next instruction if Reg[Vx] == compValue
     * @param Vx The register used for comparison
     * @param unsignedByte The value to be compared
     */
    private void skipIfNotEqual(int Vx, UnsignedByte unsignedByte) {
        opCodeString += "if(V" + Integer.toHexString(Vx) + "!=" + Integer.toHexString(unsignedByte.unsignedDataType) + ")";

        UnsignedByte registerValue = registers.peek(Vx);

        if(!registerValue.equals(unsignedByte)) programCounter.incrementCounterN(2);
    }

    /**
     * Skips the next instruction if Reg[Vx] != compValue
     * @param Vx The register used for comparison
     * @param unsignedByte The value to be compared
     */
    private void skipIfEqual(int Vx, UnsignedByte unsignedByte) {
        opCodeString += "if(V" + Integer.toHexString(Vx) + "==" + Integer.toHexString(unsignedByte.unsignedDataType) + ")";

        UnsignedByte registerValue = registers.peek(Vx);

        if(registerValue.equals(unsignedByte)) programCounter.incrementCounterN(2);
    }

    /**
     * Skips the next instruction if the two given registers hold the same value
     * @param Vx The index of the register
     * @param Vy The index of the register
     */
    private void skipIfEqualReg(int Vx, int Vy) {
        opCodeString += "if(V" + Integer.toHexString(Vx) + "==V" + Integer.toHexString(Vy) + ")";

        if(registers.peek(Vx).equals(registers.peek(Vy))) programCounter.incrementCounterN(2);
    }

    /**
     * Skips the next instruction if the two given registers do not hold the same value
     * @param Vx The index of the register
     * @param Vy The index of the register
     */
    private void skipIfNotEqualReg(int Vx, int Vy) {
        opCodeString += "if(V" + Integer.toHexString(Vx) + "!=V" + Integer.toHexString(Vy) + ")";

        if(!registers.peek(Vx).equals(registers.peek(Vy))) programCounter.incrementCounterN(2);
    }

    /**
     * Saves the current {@link Chip8ProgramCounter} and calls the subroutine located at address
     * @param address The address at which the subroutine to be called is located
     */
    private void callSubRoutine(int address) {
        opCodeString += "*(0x" + Integer.toHexString(address) + ")()";

        /* Save current PC on the stack */
        stack.push(programCounter.getCounter());

        /* Update PC */
        programCounter.jumpTo(new UnsignedShort((short) address));
    }

    /**
     * Writes the given address to the {@link Chip8AddressRegister}
     * @param address The Address to go to
     */
    private void gotoOp(int address) {
        opCodeString += "goto " + Integer.toHexString(address) + ";";
        programCounter.jumpTo( new UnsignedShort((short) address) );
    }

    /**
     * Fetches the next opcode from the hardware
     * @return the read opcode
     */
    private OPCode getNextOpCode() {
        /* Get first part of opcode */
        int opCode = memory.read(programCounter.getCounter().unsignedDataType).unsignedDataType;
        /* Shift 8 bits to the left */
        opCode = opCode << 8;
        /* Build finished opcode by using OR */
        programCounter.incrementCounter();
        opCode |= memory.read(programCounter.getCounter().unsignedDataType).unsignedDataType;

        programCounter.incrementCounter();
        return new OPCode(opCode);
    }

    @Override
    public String getState() {
        return "State:\n"
                + "Cycles executed: " + cycles + "\n"
                + "OpCode: " + opCodeString + "\n"
                // + this.memory.getState()
                //+ this.screenMemory.getState()
                + this.registers.getState() + "\n"
                + this.addressRegister.getState() + "\t"
                + this.programCounter.getState() + "\n"
                + this.stack.getState() + "\n"
                + this.delayTimer.getState() + "\t"
                + this.soundTimer.getState() + "\n";
    }

    public Chip8Memory getMemory() {
        return memory;
    }

    public ScreenMemory getScreenMemory() {
        return screenMemory;
    }

    public Chip8Registers getRegisters() {
        return registers;
    }

    public Chip8AddressRegister getAddressRegister() {
        return addressRegister;
    }

    public Chip8ProgramCounter getProgramCounter() {
        return programCounter;
    }

    public Chip8Stack getStack() {
        return stack;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public void stopTimer(){
        timer.cancel();
    }

    public DelayTimer getDelayTimer() {
        return delayTimer;
    }

    public SoundTimer getSoundTimer() {
        return soundTimer;
    }

    /**
     * Stops the cpu if it is looking for input
     */
    public void stopCPU(){
        this.stop = true;
    }

    public boolean isStop(){
        return stop;
    }

    public void startCPU(){
        this.stop = false;
    }
}
