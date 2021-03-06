package de.nihas101.chip8.savestates;

import de.nihas101.chip8.hardware.CentralProcessingUnit;
import de.nihas101.chip8.hardware.memory.*;
import de.nihas101.chip8.hardware.timers.DelayTimer;
import de.nihas101.chip8.hardware.timers.SoundTimer;
import de.nihas101.chip8.unsignedDataTypes.UnsignedShort;
import de.nihas101.chip8.utils.SynthesizerFactory;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;

import static de.nihas101.chip8.utils.Constants.*;
import static java.lang.Byte.parseByte;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

public class SaveState {
    public final CentralProcessingUnit cpu;

    private SaveState(CentralProcessingUnit cpu) {
        this.cpu = cpu;
    }

    public static SaveState createSaveState(CentralProcessingUnit cpu) {
        return new SaveState(cpu);
    }

    public static SaveState createSaveState(String stateString) {
        Memory memory = null;
        ScreenMemory screenMemory = null;
        Registers registers = null;
        AddressRegister addressRegister = null;
        ProgramCounter programCounter = null;
        Chip8Stack chip8Stack = null;
        DelayTimer delayTimer = null;
        SoundTimer soundTimer = null;
        Synthesizer synthesizer = null;
        Random random = null;
        int cycles = 0;
        String opCode = "";
        String[] strings = stateString.split("\n");

        for (String string : strings) {
            switch (string.charAt(0)) {
                case CYCLES_CHAR:
                    cycles = readCycles(string);
                    break;
                case OPCODE_CHAR:
                    opCode = readOpCode(string);
                    break;
                case MEMORY_CHAR:
                    memory = readMemory(string);
                    break;
                case SCREEN_MEMORY_CHAR:
                    screenMemory = readScreenMemory(string);
                    break;
                case REGISTERS_CHAR:
                    registers = readRegisters(string);
                    break;
                case ADDRESS_CHAR:
                    addressRegister = readAddressRegister(string);
                    break;
                case PROGRAM_COUNTER_CHAR:
                    programCounter = readProgramCounter(string);
                    break;
                case STACK_CHAR:
                    chip8Stack = readChip8Stack(string);
                    break;
                case DELAY_TIMER_CHAR:
                    delayTimer = readDelayTimer(string);
                    break;
                case SOUND_TIMER_CHAR:
                    soundTimer = readSoundTimer(string);
                    break;
                case RANDOM_CHAR:
                    random = readRandom(string);
            }
        }

        try {
            synthesizer = SynthesizerFactory.createSynthesizer();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        CentralProcessingUnit cpu = new CentralProcessingUnit(
                memory,
                screenMemory,
                registers,
                addressRegister,
                programCounter,
                chip8Stack,
                new Timer("Timer"),
                delayTimer, soundTimer,
                random,
                synthesizer
        );

        cpu.setCycles(cycles);
        cpu.setOpCode(opCode);

        return new SaveState(cpu);
    }

    private static Random readRandom(String string) {
        Random random = null;
        string = unwrap(string);
        String[] strings = string.replaceAll(" ", "")
                .replaceAll("\\[", "")
                .replaceAll("]", "")
                .replaceAll("\\s", "").split(",");
        byte[] bytes = new byte[strings.length];

        for (int i = 0; i < bytes.length; i++)
            bytes[i] = parseByte(strings[i]);

        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            random = (Random) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return random;
    }

    private static String readOpCode(String string) {
        return unwrap(string);
    }

    private static int readCycles(String string) {
        return parseInt(unwrap(string));
    }

    private static SoundTimer readSoundTimer(String string) {
        return new SoundTimer(parseInt(unwrap(string)));
    }

    private static DelayTimer readDelayTimer(String string) {
        return new DelayTimer(parseInt(unwrap(string)));
    }

    private static Chip8Stack readChip8Stack(String string) {
        string = unwrap(string);

        return new Chip8Stack(
                new java.util.Stack<>(),
                string.trim().substring(1, string.length() - 1).split(", ")
        );
    }

    private static ProgramCounter readProgramCounter(String string) {
        return new ProgramCounter(
                new UnsignedShort(parseInt(unwrap(string)))
        );
    }

    private static AddressRegister readAddressRegister(String string) {
        return new AddressRegister(
                new UnsignedShort(parseInt(unwrap(string)))
        );
    }

    private static Registers readRegisters(String string) {
        string = unwrap(string);

        return new Registers(
                string.trim()
                        .substring(1, string.length() - 1)
                        .split(", ")
        );
    }

    private static Memory readMemory(String string) {
        string = unwrap(string);

        return new Memory(
                string.trim()
                        .substring(1, string.length() - 1)
                        .split(", ")
        );
    }

    private static ScreenMemory readScreenMemory(String string) {
        string = unwrap(string);

        return new ScreenMemory(
                string.trim()
                        .substring(1, string.length() - 1)
                        .split("], ")
        );
    }

    @Override
    public String toString() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream)) {
            oos.writeObject(cpu.getRandom());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wrap(CYCLES_CHAR, valueOf(cpu.getCycles()))
                + wrap(OPCODE_CHAR, cpu.getOpCodeString())
                + wrap(MEMORY_CHAR, cpu.getMemory().getValues())
                + wrap(SCREEN_MEMORY_CHAR, cpu.getScreenMemory().getValues())
                + wrap(REGISTERS_CHAR, cpu.getRegisters().getValues())
                + wrap(ADDRESS_CHAR, valueOf(cpu.getAddressRegister().getAddress()))
                + wrap(PROGRAM_COUNTER_CHAR, valueOf(cpu.getProgramCounter().getCounter()))
                + wrap(STACK_CHAR, cpu.getStack().getValues())
                + wrap(DELAY_TIMER_CHAR, valueOf(cpu.getDelayTimer().getValue()))
                + wrap(SOUND_TIMER_CHAR, valueOf(cpu.getSoundTimer().getValue()))
                + wrap(RANDOM_CHAR, Arrays.toString(byteArrayOutputStream.toByteArray()));
    }

    private String wrap(char name, String toWrap) {
        return name + "{" + toWrap + "}\n";
    }

    private static String unwrap(String string) {
        return string.trim().substring(2, string.length() - 1);
    }
}
