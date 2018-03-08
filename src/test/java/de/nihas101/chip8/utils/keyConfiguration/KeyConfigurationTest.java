package de.nihas101.chip8.utils.keyConfiguration;

import de.nihas101.chip8.hardware.Emulator;
import org.junit.Test;

import static javafx.scene.input.KeyCode.Z;
import static org.junit.Assert.assertEquals;

public class KeyConfigurationTest {

    @Test
    public void createKeyConfiguration() {
        Emulator emulator = Emulator.createEmulator();

        KeyConfiguration keyConfiguration = KeyConfiguration.createKeyConfiguration(emulator, "config{\n" +
                "Z = A\n" +
                "}");

        assertEquals(true, keyConfiguration.contains(Z));
    }

    @Test
    public void createKeyConfigurationError() {
        Emulator emulator = Emulator.createEmulator();

        KeyConfiguration keyConfiguration = KeyConfiguration.createKeyConfiguration(emulator, "config{\n" +
                "Z = AA\n" +
                "}");

        assertEquals(false, keyConfiguration.contains(Z));
    }
}