package de.nihas101.chip8.hardware;

import de.nihas101.chip8.hardware.keys.EmulatorKey;
import de.nihas101.chip8.utils.keyConfiguration.KeyConfiguration;
import javafx.scene.input.KeyCode;
import org.junit.Test;

import java.util.HashMap;

import static de.nihas101.chip8.hardware.keys.EmulatorKey.createEmulatorKey;
import static javafx.scene.input.KeyCode.DIGIT0;
import static javafx.scene.input.KeyCode.DIGIT1;
import static org.junit.Assert.assertEquals;

public class EmulatorTest {

    @Test
    public void createEmulator() {
        Emulator emulator = Emulator.createEmulator();

        assertEquals("State:\n" +
                "Cycles executed: 0\n" +
                "OpCode: \n" +
                "V0: 0   V1: 0   V2: 0   V3: 0   V4: 0   V5: 0   V6: 0   V7: 0\n" +
                "V8: 0   V9: 0   VA: 0   VB: 0   VC: 0   VD: 0   VE: 0   VF: 0\n" +
                "I:\t0\tPC:\t200\n" +
                "Chip8Stack:\t[]\n" +
                "DelayTimer: 0\tSoundTimer: 0\n", emulator.getState());
    }

    @Test
    public void setStandardKeyConfiguration() {
        Emulator emulator = Emulator.createEmulator();

        emulator.setStandardKeyConfiguration();

        assertEquals(true, emulator.getKeyConfiguration().contains(DIGIT1));
    }

    @Test
    public void setKeyConfiguration() {
        Emulator emulator = Emulator.createEmulator();

        HashMap<KeyCode, EmulatorKey> hashMap = new HashMap<>();
        hashMap.put(DIGIT0, createEmulatorKey("test", () -> {
                })
        );

        hashMap.get(DIGIT0).trigger();

        emulator.setKeyConfiguration(KeyConfiguration.createKeyConfiguration(hashMap));

        assertEquals(true, emulator.getKeyConfiguration().contains(DIGIT0));
    }

    @Test
    public void executeCPUCycles() {
        Emulator emulator = Emulator.createEmulator();

        emulator.executeCPUCycles(100);

        assertEquals(100, emulator.getCentralProcessingUnit().getCycles());
    }

    @Test
    public void stop() {
        Emulator emulator = Emulator.createEmulator();

        emulator.stop();
        emulator.executeCPUCycles(100);

        assertEquals(0, emulator.getCentralProcessingUnit().getCycles());
    }

    @Test
    public void getCentralProcessingUnit() {
        Emulator emulator = Emulator.createEmulator();

        assertEquals("State:\n" +
                "Cycles executed: 0\n" +
                "OpCode: \n" +
                "V0: 0   V1: 0   V2: 0   V3: 0   V4: 0   V5: 0   V6: 0   V7: 0\n" +
                "V8: 0   V9: 0   VA: 0   VB: 0   VC: 0   VD: 0   VE: 0   VF: 0\n" +
                "I:\t0\tPC:\t200\n" +
                "Chip8Stack:\t[]\n" +
                "DelayTimer: 0\tSoundTimer: 0\n", emulator.getCentralProcessingUnit().getState());
    }

    @Test
    public void setCentralProcessingUnit() {
        Emulator emulator = Emulator.createEmulator();
        Emulator emulator1 = Emulator.createEmulator();

        emulator.setCentralProcessingUnit(emulator1.getCentralProcessingUnit());

        emulator1.executeCPUCycles(100);

        assertEquals(
                emulator1.getCentralProcessingUnit().getState(),
                emulator.getCentralProcessingUnit().getState()
        );
    }
}