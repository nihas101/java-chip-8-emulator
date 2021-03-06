package de.nihas101.chip8.hardware;

import de.nihas101.chip8.debug.Debuggable;
import de.nihas101.chip8.hardware.memory.*;
import de.nihas101.chip8.hardware.timers.DelayTimer;
import de.nihas101.chip8.hardware.timers.SoundTimer;
import de.nihas101.chip8.opcodes.OPCode;
import de.nihas101.chip8.opcodes.UnknownOPCodeException;
import de.nihas101.chip8.unsignedDataTypes.BinaryOperation;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;
import de.nihas101.chip8.unsignedDataTypes.UnsignedDataType;
import de.nihas101.chip8.unsignedDataTypes.UnsignedShort;
import de.nihas101.chip8.utils.RegisterAction;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Synthesizer;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import static de.nihas101.chip8.utils.Constants.HERTZ_60;
import static de.nihas101.chip8.utils.OpCodeStringFactory.*;
import static de.nihas101.chip8.utils.keyConfiguration.Keys.NO_KEY;
import static java.lang.Integer.toHexString;

/**
 * A class representing a central processing unit of a Chip-8
 */
public class CentralProcessingUnit implements Debuggable {
    private final Memory memory;
    private final Registers registers;
    private final AddressRegister addressRegister;
    private final ProgramCounter programCounter;
    private final Chip8Stack stack;
    private final ScreenMemory screenMemory;
    private final Synthesizer synthesizer;

    private final Random random;

    private int keyCode = NO_KEY;

    private Timer timer;

    private final DelayTimer delayTimer;
    private final SoundTimer soundTimer;

    private String opCodeString = "";
    private int cycles;
    private boolean stop = false;
    private boolean pause = false;

    private Logger logger = Logger.getLogger(CentralProcessingUnit.class.getName());

    public CentralProcessingUnit(Memory memory, ScreenMemory screenMemory, Registers registers,
                                 AddressRegister addressRegister, ProgramCounter programCounter,
                                 Chip8Stack chip8Stack, Timer timer, DelayTimer delayTimer, SoundTimer soundTimer,
                                 Random random, Synthesizer synthesizer) {
        this.memory = memory;
        this.screenMemory = screenMemory;
        this.registers = registers;
        this.addressRegister = addressRegister;

        this.programCounter = programCounter;
        this.stack = chip8Stack;
        this.random = random;

        this.synthesizer = synthesizer;

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

        if (synthesizer != null) setupSoundTimer();

        cycles = 0;
    }

    private void setupSoundTimer() {
        Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();
        MidiChannel midiChannel = synthesizer.getChannels()[0];
        synthesizer.loadInstrument(instruments[0]);
        soundTimer.setOnValue(() -> {
            if (midiChannel != null) midiChannel.noteOn(70, 40);
        });
        soundTimer.setOnZero(() -> {
            if (midiChannel != null) midiChannel.noteOff(70);
        });
    }

    public CentralProcessingUnit(Memory memory, ScreenMemory screenMemory, Registers registers,
                                 AddressRegister addressRegister, ProgramCounter programCounter,
                                 Chip8Stack chip8Stack, Timer timer, DelayTimer delayTimer, SoundTimer soundTimer,
                                 Random random) {
        this.memory = memory;
        this.screenMemory = screenMemory;
        this.registers = registers;
        this.addressRegister = addressRegister;

        this.programCounter = programCounter;
        this.stack = chip8Stack;
        this.random = random;
        this.synthesizer = null;

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
        soundTimer.setOnValue(() -> { /* DO NOTHING */ });
        soundTimer.setOnZero(() -> { /* DO NOTHING */ });

        cycles = 0;
    }

    /**
     * Changes the speed of both timers by the given factor
     *
     * @param factor The factor by which the timers' speed should increase/decrease
     */
    public void changeTimerSpeed(double factor) {
        long newSpeed = (long) (HERTZ_60 / factor);
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, newSpeed, newSpeed);
    }

    /**
     * Reset the {@link CentralProcessingUnit}
     */
    public void reset() {
        cycles = 0;
        registers.clear();
        programCounter.jumpTo(new UnsignedShort(0x200));
        screenMemory.reset();
        addressRegister.setAddress(new UnsignedShort(0));
        delayTimer.reset();
        soundTimer.reset();
        stack.clear();
    }

    public void clearMemory() {
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
     *
     * @throws UnknownOPCodeException Thrown if the given opcode is unknown
     */
    public void decodeNextOpCode() throws UnknownOPCodeException {
        OPCode opCode = getNextOpCode();
        opCodeString = toHexString(opCode.getOpCode()) + " -> ";
        decodeOpCode(opCode);
        if (!stop && !pause && cycles < Integer.MAX_VALUE) cycles++;
    }

    /**
     * Decodes an opcode and executes the appropriate instruction
     * See en.wikipedia.org/wiki/CHIP-8#Opcode_table for a breakdown of the codes
     *
     * @param opCode The opcode to be decoded
     * @throws UnknownOPCodeException Thrown if the given opcode is unknown
     */
    private void decodeOpCode(OPCode opCode) throws UnknownOPCodeException {
        switch (opCode.getByte(0)) {
            case 0x0:
                opCode0XYZ(opCode);
                break;
            case 0x1:
                gotoOp(opCode.applyMask(0x0FFF));
                break;
            case 0x2:
                callSubRoutine(opCode.applyMask(0x0FFF));
                break;
            case 0x3: {
                /* Extract X and NN from opCode */
                skipIfEqual(opCode.getByte(1), new UnsignedByte(opCode.applyMask(0x00FF)));
                break;
            }
            case 0x4: {
                /* Extract X and NN from opCode */
                skipIfNotEqual(opCode.getByte(1), new UnsignedByte(opCode.applyMask(0x00FF)));
                break;
            }
            case 0x5:
                skipIfEqualReg(opCode.getByte(1), opCode.getByte(2));
                break;
            case 0x6: {
                /* Extract X and NN from opCode */
                setRegister(opCode.getByte(1), new UnsignedByte(opCode.applyMask(0x00FF)));
                break;
            }
            case 0x7: {
                /* Extract X and NN from opCode */
                addRegister(opCode.getByte(1), new UnsignedByte(opCode.applyMask(0x00FF)));
                break;
            }
            case 0x8:
                opCode8XYN(opCode);
                break;
            case 0x9:
                skipIfNotEqualReg(opCode.getByte(1), opCode.getByte(2));
                break;
            case 0xA:
                setAddress(new UnsignedShort(opCode.applyMask(0x0FFF)));
                break;
            case 0xB:
                setPC(new UnsignedShort(opCode.applyMask(0x0FFF)));
                break;
            case 0xC:
                randomAND(opCode.getByte(1), new UnsignedByte(opCode.applyMask(0x00FF)));
                break;
            case 0xD:
                drawSprite(opCode.getByte(1), opCode.getByte(2), opCode.getByte(3));
                break;
            case 0xE:
                opCodeEXYZ(opCode);
                break;
            case 0xF:
                opCodeFXYZ(opCode);
                break;
            default:
                throw new UnknownOPCodeException(opCode);
        }
    }

    /**
     * Further decodes {@link OPCode} instances that begin with 0xF
     *
     * @param opCode The {@link OPCode} to be decoded
     * @throws UnknownOPCodeException If an unknown {@link OPCode} is encountered
     */
    private void opCodeFXYZ(OPCode opCode) throws UnknownOPCodeException {
        switch (opCode.getByte(2)) {
            case 0x0:
                opCodeFX0Y(opCode);
                break;
            case 0x1:
                opCodeFX1Y(opCode);
                break;
            case 0x2:
                gotoSpriteAddress(opCode.getByte(1));
                break;
            case 0x3:
                opCodeFX3Y(opCode);
                break;
            case 0x5:
                opCodeFX5Y(opCode);
                break;
            case 0x6:
                opCodeFX6Y(opCode);
                break;
            default:
                throw new UnknownOPCodeException(opCode);
        }
    }

    /**
     * Sets the address register to the sprite for the character
     * in VX
     *
     * @param Vx The register holding the character
     */
    private void gotoSpriteAddress(int Vx) {
        opCodeString += "I=sprite_addr[" + toHexString(Vx) + "]";
        int address = this.registers.peek(Vx).apply(x -> x * 5).unsignedDataType;
        this.addressRegister.setAddress(new UnsignedShort(address));
    }

    /**
     * Further decodes {@link OPCode} instances that in the form 0xF_6_
     *
     * @param opCode The {@link OPCode} to be decoded
     * @throws UnknownOPCodeException If an unknown {@link OPCode} is encountered
     */
    private void opCodeFX6Y(OPCode opCode) throws UnknownOPCodeException {
        switch (opCode.getByte(3)) {
            case 0x5:
                loadReg(opCode.getByte(1));
                break;
            default:
                throw new UnknownOPCodeException(opCode);
        }
    }

    /**
     * Further decodes {@link OPCode} instances that in the form 0xF_5_
     *
     * @param opCode The {@link OPCode} to be decoded
     * @throws UnknownOPCodeException If an unknown {@link OPCode} is encountered
     */
    private void opCodeFX5Y(OPCode opCode) throws UnknownOPCodeException {
        switch (opCode.getByte(3)) {
            case 0x5:
                dumpReg(opCode.getByte(1));
                break;
            default:
                throw new UnknownOPCodeException(opCode);
        }
    }

    /**
     * Dumps the registers V0 ... Vx into the memory starting at the location the {@link AddressRegister} points at
     *
     * @param Vx The index of the registers to be dumped up to
     */
    private void dumpReg(int Vx) {
        opCodeString += createRegOpCodeString("dump", Vx);
        interactWithReg(Vx, (memoryAddress, registerAddress) -> this.memory.write(memoryAddress, this.registers.peek(registerAddress)));
    }

    /**
     * Loads the registers V0 ... Vx into the memory starting at the location the {@link AddressRegister} points at
     *
     * @param Vx The index of the registers to be loaded up to
     */
    private void loadReg(int Vx) {
        opCodeString += createRegOpCodeString("load", Vx);
        interactWithReg(Vx, ((memoryAddress, registerAddress) -> this.registers.poke(registerAddress, this.memory.read(memoryAddress))));
    }

    private void interactWithReg(int Vx, RegisterAction registerAction) {
        for (int i = 0; i <= Vx; i++) {
            int address = this.addressRegister.getAddress().apply((x, y) -> x + y, new UnsignedShort(i)).unsignedDataType;
            registerAction.execute(address, i);
        }
    }

    /**
     * Further decodes {@link OPCode} instances that in the form 0xF_3_
     *
     * @param opCode The {@link OPCode} to be decoded
     * @throws UnknownOPCodeException If an unknown {@link OPCode} is encountered
     */
    private void opCodeFX3Y(OPCode opCode) throws UnknownOPCodeException {
        switch (opCode.getByte(3)) {
            case 0x3:
                storeBinaryCodedDecimals(opCode.getByte(1));
                break;
            default:
                throw new UnknownOPCodeException(opCode);
        }
    }

    /**
     * Stores a decimal in the memory
     *
     * @param Vx The index of the register from which the decimal should be read
     */
    private void storeBinaryCodedDecimals(int Vx) {
        opCodeString += "BCD V" + toHexString(Vx);

        int decimalValue = this.registers.peek(Vx).unsignedDataType;

        UnsignedByte hundreds = new UnsignedByte((decimalValue / 100));
        UnsignedByte tens = new UnsignedByte(((decimalValue % 100) / 10));
        UnsignedByte units = new UnsignedByte(((decimalValue % 100) % 10));

        this.memory.write(this.addressRegister.getAddress().unsignedDataType, hundreds);
        this.memory.write(this.addressRegister.getAddress().apply(x -> x + 1).unsignedDataType, tens);
        this.memory.write(this.addressRegister.getAddress().apply(x -> x + 2).unsignedDataType, units);
    }

    /**
     * Further decodes {@link OPCode} instances that in the form 0xF_0_
     *
     * @param opCode The {@link OPCode} to be decoded
     * @throws UnknownOPCodeException If an unknown {@link OPCode} is encountered
     */
    private void opCodeFX0Y(OPCode opCode) throws UnknownOPCodeException {
        switch (opCode.getByte(3)) {
            case 0x7:
                getDelayTimer(opCode.getByte(1));
                break;
            case 0xA:
                waitForInput(opCode.getByte(1));
                break;
            default:
                throw new UnknownOPCodeException(opCode);
        }
    }

    /**
     * Further decodes {@link OPCode} instances that in the form 0xF_1_
     *
     * @param opCode The {@link OPCode} to be decoded
     * @throws UnknownOPCodeException If an unknown {@link OPCode} is encountered
     */
    private void opCodeFX1Y(OPCode opCode) throws UnknownOPCodeException {
        switch (opCode.getByte(3)) {
            case 0x5:
                setDelayTimer(opCode.getByte(1));
                break;
            case 0x8:
                setSoundTimer(opCode.getByte(1));
                break;
            case 0xE:
                setAddressReg(opCode.getByte(1));
                break;
            default:
                throw new UnknownOPCodeException(opCode);
        }
    }

    /**
     * Sets the {@link AddressRegister} to the value of the register with index Vx
     *
     * @param Vx The index of the register
     */
    private void setAddressReg(int Vx) {
        opCodeString += "I +=V" + toHexString(Vx);

        UnsignedShort result = this.registers.peek(Vx).apply((x, y) -> x + y, this.addressRegister.getAddress());
        this.addressRegister.setAddress(result);
    }

    /**
     * Sets the {@link SoundTimer} to the value in register Vx
     *
     * @param Vx The index of the register
     */
    private void setSoundTimer(int Vx) {
        opCodeString += createTimerOpString("sound_timer", Vx);

        this.soundTimer.setValue(this.registers.peek(Vx).unsignedDataType);
    }

    /**
     * Sets the {@link DelayTimer} to the value in register Vx
     *
     * @param Vx The index of the register
     */
    private void setDelayTimer(int Vx) {
        opCodeString += createTimerOpString("delay_timer", Vx);

        this.delayTimer.setValue(this.registers.peek(Vx).unsignedDataType);
    }

    /**
     * Waits for input and writes it into register Vx
     *
     * @param Vx The index of the register
     */
    private void waitForInput(int Vx) {
        opCodeString += "V" + toHexString(Vx) + " = get_key()";

        /* Halt execution until a key is pressed */
        while (keyCode == NO_KEY && !stop) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                logger.severe(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        this.registers.poke(Vx, new UnsignedByte(keyCode));
    }

    /**
     * Further decodes {@link OPCode} instances beginning with 0x0
     *
     * @param opCode The {@link OPCode} to be decoded
     * @throws UnknownOPCodeException If an unknown {@link OPCode} is encountered
     */
    private void opCode0XYZ(OPCode opCode) throws UnknownOPCodeException {
        switch (opCode.getByte(1)) {
            case 0x0: {
                if (opCode.getByte(3) == 0) {
                    this.getScreenMemory().reset();
                    break;
                } else {
                    returnFromSubRoutine();
                    break;
                }
            }
            default: {
                /* Call RCA 1802 program at address NNN. Not necessary for most ROMs. */
                throw new UnknownOPCodeException("RCA 1802 is not  supported: " + toHexString(opCode.getOpCode()));
            }
        }
    }

    /**
     * Sets the {@link ProgramCounter} to a value popped from a stack
     */
    private void returnFromSubRoutine() {
        opCodeString += "return;";
        this.programCounter.jumpTo(this.stack.pop());
    }

    /**
     * Further decodes {@link OPCode} instances beginning with 0x8
     *
     * @param opCode The {@link OPCode} to be decoded
     * @throws UnknownOPCodeException If an unknown {@link OPCode} is encountered
     */
    private void opCode8XYN(OPCode opCode) throws UnknownOPCodeException {
        switch (opCode.getByte(3)) {
            case 0x0:
                assign(opCode.getByte(1), opCode.getByte(2));
                break;
            case 0x1:
                assignLogicOperation("|", opCode.getByte(1), opCode.getByte(2));
                break;
            case 0x2:
                assignLogicOperation("&", opCode.getByte(1), opCode.getByte(2));
                break;
            case 0x3:
                assignLogicOperation("^", opCode.getByte(1), opCode.getByte(2));
                break;
            case 0x4:
                assignArithmeticOperation("+", opCode.getByte(1), opCode.getByte(2), opCode.getByte(1));
                break;
            case 0x5:
                assignArithmeticOperation("-", opCode.getByte(1), opCode.getByte(2), opCode.getByte(1));
                break;
            case 0x6:
                shiftRight(opCode.getByte(1));
                break;
            case 0x7:
                assignArithmeticOperation("-", opCode.getByte(2), opCode.getByte(1), opCode.getByte(1));
                break;
            case 0xE:
                shiftLeft(opCode.getByte(1));
                break;
            default:
                throw new UnknownOPCodeException(opCode);
        }
    }

    /**
     * Further decodes {@link OPCode} instances beginning with 0xE
     *
     * @param opCode The {@link OPCode} to be decoded
     * @throws UnknownOPCodeException If an unknown {@link OPCode} is encountered
     */
    private void opCodeEXYZ(OPCode opCode) throws UnknownOPCodeException {
        switch (opCode.getByte(2)) {
            case 0x9:
                skipIfKeyPressed(opCode.getByte(1));
                break;
            case 0xA:
                skipIfKeyNotPressed(opCode.getByte(1));
                break;
            default:
                throw new UnknownOPCodeException(opCode);
        }
    }

    /**
     * Writes the {@link DelayTimer} into the register Vx
     *
     * @param Vx The index of the register
     */
    private void getDelayTimer(int Vx) {
        opCodeString += "V" + toHexString(Vx) + " = get_delay()";
        this.getRegisters().poke(Vx, new UnsignedByte(delayTimer.getValue()));
    }

    /**
     * Skips the next instruction if no key is pressed
     *
     * @param Vx The value of the register
     */
    private void skipIfKeyNotPressed(int Vx) {
        opCodeString += "if(key()!=V" + toHexString(Vx) + ")";

        if (!(this.registers.peek(Vx).unsignedDataType == keyCode))
            this.getProgramCounter().incrementCounterN(2);
    }

    /**
     * Skips the next instruction if a key is pressed
     *
     * @param Vx The value of the register
     */
    private void skipIfKeyPressed(int Vx) {
        opCodeString += "if(key()==V" + toHexString(Vx) + ")";

        if (this.registers.peek(Vx).unsignedDataType == keyCode)
            this.getProgramCounter().incrementCounterN(2);
    }

    /**
     * Draws 8-pixels-wide sprites into the {@link ScreenMemory}
     *
     * @param Vx     The horizontal index of the sprite
     * @param Vy     The lateral index of the sprite
     * @param height The height of the pixel
     */
    private void drawSprite(int Vx, int Vy, int height) {
        int mask = 0x80;
        opCodeString += "draw(V" + toHexString(Vx) + ",V" + toHexString(Vy) + "," + height + ")";

        /* Reset flag-register */
        this.registers.poke(0xF, new UnsignedByte(0));

        /* Get coordinates of sprite */
        int coordX = this.registers.peek(Vx).unsignedDataType;
        int coordY = this.registers.peek(Vy).unsignedDataType;

        UnsignedShort yLine = new UnsignedShort(0);
        for (; yLine.unsignedDataType < height; yLine = yLine.apply(v -> v + 1)) {
            /* Get memory at addressRegister + yLine */
            UnsignedByte data = this.memory.read(this.addressRegister.getAddress().apply((x1, y1) -> x1 + y1, yLine).unsignedDataType);
            drawLineOfSprite(coordX, coordY, data, mask, yLine);
        }
    }

    private void drawLineOfSprite(int xCoordinate, int yCoordinate, UnsignedByte data, int mask, UnsignedShort yLine) {
        for (int xPixel = 0; xPixel < 8; xPixel++) {
            if (0 != data.apply((x, y) -> x & y, new UnsignedByte(mask)).unsignedDataType) {
                int x = xCoordinate + xPixel;
                int y = yCoordinate + yLine.unsignedDataType;

                setCollisionRegister(x, y);

                /* Set the pixel */
                screenMemory.write(x, y, !screenMemory.read(x, y));
            }
            mask = mask >> 1;
        }
    }

    private void setCollisionRegister(int x, int y) {
        if (screenMemory.read(x, y)) this.registers.poke(0xF, new UnsignedByte(1));
    }

    /**
     * Uses the AND operation on the given number and a random number and writes it into Vx
     *
     * @param Vx           The register
     * @param unsignedByte The number
     */
    private void randomAND(int Vx, UnsignedByte unsignedByte) {
        opCodeString += "V" + toHexString(Vx) + "=rand()&" + unsignedByte.unsignedDataType;

        UnsignedByte result = unsignedByte.apply((x, y) -> x & y, new UnsignedByte(random.nextInt(255)));
        this.getRegisters().poke(Vx, result);
    }

    /**
     * Sets the {@link AddressRegister} to the given address
     *
     * @param address The new address of the I register
     */
    private void setAddress(UnsignedShort address) {
        opCodeString += "I = " + toHexString(address.unsignedDataType);

        this.addressRegister.setAddress(address);
    }

    /**
     * Sets the {@link ProgramCounter} to the given address
     *
     * @param address The new address of the {@link ProgramCounter}
     */
    private void setPC(UnsignedShort address) {
        opCodeString += "PC=V0+" + toHexString(address.unsignedDataType);

        UnsignedShort result = this.getRegisters().peek(0).apply((x, y) -> x + y, address);
        this.getProgramCounter().jumpTo(result);
    }

    /**
     * Sets the register Vx to the given unsignedByte
     *
     * @param Vx           The register which will hold the unsignedByte
     * @param unsignedByte The unsigned byte to be written into the register
     */
    private void setRegister(int Vx, UnsignedByte unsignedByte) {
        opCodeString += "V" + toHexString(Vx) + " = " + toHexString(unsignedByte.unsignedDataType);

        registers.poke(Vx, unsignedByte);
    }

    /**
     * Adds the unsigned byte to the register Vx
     *
     * @param Vx           The register which will hold the result of this operation
     * @param unsignedByte The unsigned byte to be added to the register
     */
    private void addRegister(int Vx, UnsignedByte unsignedByte) {
        opCodeString += "V" + toHexString(Vx) + " += " + toHexString(unsignedByte.unsignedDataType);

        registers.poke(Vx, registers.peek(Vx).apply((x, y) -> x + y, unsignedByte));
    }

    /**
     * Sets Vx to the value of Vy
     *
     * @param Vx The register which will hold the result of this operation
     * @param Vy The register which value will replace the one in Vx
     */
    private void assign(int Vx, int Vy) {
        opCodeString += "V" + toHexString(Vx) + "=V" + toHexString(Vy);

        registers.poke(Vx, registers.peek(Vy));
    }

    private void assignLogicOperation(String operation, int Vx, int Vy) {
        opCodeString += createLogicOpOpCodeString(operation, Vx, Vy);
        UnsignedByte result;

        switch (operation) {
            case "|":
                result = registers.peek(Vx).apply((x, y) -> x | y, registers.peek(Vy));
                break;
            case "&":
                result = registers.peek(Vx).apply((x, y) -> x & y, registers.peek(Vy));
                break;
            default: // "^"
                result = registers.peek(Vx).apply((x, y) -> x ^ y, registers.peek(Vy));
                break;
        }

        registers.poke(Vx, result);
    }

    private void assignArithmeticOperation(String operation, int Vx, int Vy, int Vz) {
        opCodeString += createArithmeticOpCodeString(operation, Vx, Vy);
        UnsignedByte result;

        switch (operation) {
            case "+":
                result = arithmeticOperation(Vx, Vy, ((x, y) -> x + y), 1, 0);
                break;
            default: // "-"
                result = arithmeticOperation(Vx, Vy, ((x, y) -> x - y), 0, 1);
                break;
        }

        registers.poke(Vz, result);
    }

    private UnsignedByte arithmeticOperation(int Vx, int Vy, BinaryOperation arithmeticOperation, int onOverFlow, int noOverFlow) {
        UnsignedByte unsignedByte = registers.peek(Vx).apply(arithmeticOperation, registers.peek(Vy));
        setBorrowCarryFlag(unsignedByte, onOverFlow, noOverFlow);
        return unsignedByte;
    }

    private void setBorrowCarryFlag(UnsignedDataType unsignedDataType, int onOverFlow, int noOverFlow) {
        if (unsignedDataType.lastOperationLeadToOverflow()) registers.poke(0xF, new UnsignedByte(onOverFlow));
        else registers.poke(0xF, new UnsignedByte(noOverFlow));
    }

    /**
     * Shifts the value stored in Vx to the right by one
     *
     * @param Vx The index of the register
     */
    private void shiftRight(int Vx) {
        opCodeString += "V" + toHexString(Vx) + " >> 1";

        /* Calc flag and result */
        UnsignedByte leastSignificantBit = registers.peek(Vx).apply(x -> x & 1);
        UnsignedByte result = registers.peek(Vx).apply(x -> x >> 1);

        registers.poke(Vx, result);
        registers.poke(0xF, leastSignificantBit);
    }

    /**
     * Shifts the value stored in Vx to the left by one
     *
     * @param Vx The index of the register
     */
    private void shiftLeft(int Vx) {
        opCodeString += "V" + toHexString(Vx) + " << 1";

        /* Calc flag and result */
        UnsignedByte mostSignificantBit = registers.peek(Vx).apply(x -> (x & 0x80) >> 7);
        UnsignedByte result = registers.peek(Vx).apply(x -> x << 1);

        registers.poke(Vx, result);
        registers.poke(0xF, mostSignificantBit);
    }

    /**
     * Skips the next instruction if Reg[Vx] == compValue
     *
     * @param Vx           The register used for comparison
     * @param unsignedByte The value to be compared
     */
    private void skipIfNotEqual(int Vx, UnsignedByte unsignedByte) {
        opCodeString += "if(V" + toHexString(Vx) + "!=" + toHexString(unsignedByte.unsignedDataType) + ")";

        UnsignedByte registerValue = registers.peek(Vx);

        if (!registerValue.equals(unsignedByte)) programCounter.incrementCounterN(2);
    }

    /**
     * Skips the next instruction if Reg[Vx] != compValue
     *
     * @param Vx           The register used for comparison
     * @param unsignedByte The value to be compared
     */
    private void skipIfEqual(int Vx, UnsignedByte unsignedByte) {
        opCodeString += "if(V" + toHexString(Vx) + "==" + toHexString(unsignedByte.unsignedDataType) + ")";

        UnsignedByte registerValue = registers.peek(Vx);

        if (registerValue.equals(unsignedByte)) programCounter.incrementCounterN(2);
    }

    /**
     * Skips the next instruction if the two given registers hold the same value
     *
     * @param Vx The index of the register
     * @param Vy The index of the register
     */
    private void skipIfEqualReg(int Vx, int Vy) {
        opCodeString += "if(V" + toHexString(Vx) + "==V" + toHexString(Vy) + ")";

        if (registers.peek(Vx).equals(registers.peek(Vy))) programCounter.incrementCounterN(2);
    }

    /**
     * Skips the next instruction if the two given registers do not hold the same value
     *
     * @param Vx The index of the register
     * @param Vy The index of the register
     */
    private void skipIfNotEqualReg(int Vx, int Vy) {
        opCodeString += "if(V" + toHexString(Vx) + "!=V" + toHexString(Vy) + ")";

        if (!registers.peek(Vx).equals(registers.peek(Vy))) programCounter.incrementCounterN(2);
    }

    /**
     * Saves the current {@link ProgramCounter} and calls the subroutine located at address
     *
     * @param address The address at which the subroutine to be called is located
     */
    private void callSubRoutine(int address) {
        opCodeString += "*(0x" + toHexString(address) + ")()";

        /* Save current PC on the stack */
        stack.push(programCounter.getCounter());

        /* Update PC */
        programCounter.jumpTo(new UnsignedShort(address));
    }

    /**
     * Writes the given address to the {@link AddressRegister}
     *
     * @param address The Address to go to
     */
    private void gotoOp(int address) {
        opCodeString += "goto " + toHexString(address) + ";";
        programCounter.jumpTo(new UnsignedShort(address));
    }

    /**
     * Fetches the next opcode from the hardware
     *
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
        String cycleString = Integer.toString(cycles);
        if (cycles == Integer.MAX_VALUE) cycleString += "+";

        return "State:\n"
                + "Cycles executed: " + cycleString + "\n"
                + "OpCode: " + opCodeString + "\n"
                + this.registers.getState() + "\n"
                + this.addressRegister.getState() + "\t"
                + this.programCounter.getState() + "\n"
                + this.stack.getState() + "\n"
                + this.delayTimer.getState() + "\t"
                + this.soundTimer.getState() + "\n";
    }

    public Memory getMemory() {
        return memory;
    }

    public ScreenMemory getScreenMemory() {
        return screenMemory;
    }

    public Registers getRegisters() {
        return registers;
    }

    public AddressRegister getAddressRegister() {
        return addressRegister;
    }

    public ProgramCounter getProgramCounter() {
        return programCounter;
    }

    public Chip8Stack getStack() {
        return stack;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public void stopTimer() {
        timer.cancel();
    }

    public DelayTimer getDelayTimer() {
        return delayTimer;
    }

    public SoundTimer getSoundTimer() {
        return soundTimer;
    }

    public void setStop(boolean isStop) {
        this.stop = isStop;
    }

    public void closeSynthesizer() {
        if (synthesizer != null)
            synthesizer.close();
    }

    public boolean isStop() {
        return stop;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isPause() {
        return pause;
    }

    public int getCycles() {
        return cycles;
    }

    public String getOpCodeString() {
        return opCodeString;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public void setOpCode(String opCodeString) {
        this.opCodeString = opCodeString;
    }

    public Random getRandom() {
        return random;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
