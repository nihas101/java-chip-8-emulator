package de.nihas101.chip8.hardware;

import de.nihas101.chip8.debug.Debuggable;
import de.nihas101.chip8.hardware.memory.*;
import de.nihas101.chip8.hardware.timers.DelayTimer;
import de.nihas101.chip8.hardware.timers.SoundTimer;
import de.nihas101.chip8.unsignedDataTypes.UnsignedShort;
import de.nihas101.chip8.utils.KeyConfiguration;
import de.nihas101.chip8.utils.SynthesizerFactory;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.Random;
import java.util.Timer;
import java.util.logging.Logger;

import static de.nihas101.chip8.utils.Constants.PROGRAM_COUNTER_START;

public class Emulator implements Debuggable {
    private CentralProcessingUnit centralProcessingUnit;
    private KeyConfiguration keyConfiguration;

    private Logger logger = Logger.getLogger(Emulator.class.getName());

    private Emulator(CentralProcessingUnit centralProcessingUnit){
        this.centralProcessingUnit = centralProcessingUnit;
    }

    public static Emulator createEmulator(){
        return new Emulator(setupCentralProcessingUnit());
    }

    public void setStandardKeyConfiguration(){
        keyConfiguration = KeyConfiguration.createKeyConfiguration(this);
    }

    public void setStandardKeyConfiguration(KeyConfiguration keyConfiguration) {
        this.keyConfiguration = keyConfiguration;
    }

    public KeyConfiguration getKeyConfiguration() {
        return keyConfiguration;
    }

    private static CentralProcessingUnit setupCentralProcessingUnit() {
        /* Build Hardware */
        Memory memory = new Memory();
        ScreenMemory screenMemory = new ScreenMemory();
        Registers registers = new Registers();
        AddressRegister addressRegister = new AddressRegister();
        ProgramCounter programCounter = new ProgramCounter(new UnsignedShort(PROGRAM_COUNTER_START));
        Chip8Stack chip8Stack = new Chip8Stack(new java.util.Stack<>());
        Timer timer = new Timer("Timer");
        DelayTimer delayTimer = new DelayTimer();
        SoundTimer soundTimer = new SoundTimer();
        Random random = new Random();
        Synthesizer synthesizer = null;

        try {
            synthesizer = SynthesizerFactory.createSynthesizer();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        return new CentralProcessingUnit(
                memory, screenMemory,
                registers, addressRegister, programCounter, chip8Stack,
                timer, delayTimer, soundTimer,
                random,
                synthesizer);
    }

    /**
     * Executes CPU cycles
     * @param cycles The number of cycles to execute
     * @return The part of the cycles that wasn't  a full cycle
     * e.g. if the cpu was instructed to calculate 1.2 cycles this
     * will return 0.2, so this debt can be accumulated and corrected once this debt
     * equals a full cycle
     */
    public double executeCPUCycles(double cycles) {
        for( ; cycles >= 1 ; cycles --) {
            try {
                centralProcessingUnit.decodeNextOpCode();
            } catch (Exception e) {
                centralProcessingUnit.stopCPU();
                logger.severe(centralProcessingUnit.getState());
                logger.severe(e.getMessage());
            }
        }
        return cycles;
    }


    public void stop(){
        /* Stop the thread that is used as timer */
        centralProcessingUnit.stopCPU();
        centralProcessingUnit.stopTimer();
        centralProcessingUnit.closeSynthesizer();
    }

    public CentralProcessingUnit getCentralProcessingUnit() {
        return centralProcessingUnit;
    }

    public void setCentralProcessingUnit(CentralProcessingUnit centralProcessingUnit){
        this.centralProcessingUnit = centralProcessingUnit;
    }

    @Override
    public String getState() {
        return centralProcessingUnit.getState();
    }
}
