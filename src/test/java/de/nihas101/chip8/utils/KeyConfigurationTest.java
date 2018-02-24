package de.nihas101.chip8.utils;

import de.nihas101.chip8.hardware.keys.EmulatorKey;
import javafx.scene.input.KeyCode;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static de.nihas101.chip8.hardware.Emulator.createEmulator;
import static de.nihas101.chip8.hardware.keys.EmulatorKey.createEmulatorKey;
import static javafx.scene.input.KeyCode.*;
import static org.junit.Assert.assertEquals;

public class KeyConfigurationTest {

    @Test
    public void createKeyConfiguration() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        HashMap<KeyCode, EmulatorKey> emulatorKeyHashMap = new HashMap<>();

        emulatorKeyHashMap.put(DIGIT0, createEmulatorKey("test", () -> atomicBoolean.set(false)));
        KeyConfiguration.createKeyConfiguration(emulatorKeyHashMap).getOrNOP(DIGIT0).trigger();

        assertEquals(false, atomicBoolean.get());
    }

    @Test
    public void entrySet() {
        HashMap<KeyCode, EmulatorKey> emulatorKeyHashMap = new HashMap<>();

        emulatorKeyHashMap.put(DIGIT0, createEmulatorKey("test", () -> {
        }));
        emulatorKeyHashMap.get(DIGIT0).trigger();

        Set<Map.Entry<KeyCode, EmulatorKey>> entries = KeyConfiguration.createKeyConfiguration(emulatorKeyHashMap).entrySet();

        assertEquals("[DIGIT0=test]", entries.toString());
    }

    @Test
    public void contains() {
        HashMap<KeyCode, EmulatorKey> emulatorKeyHashMap = new HashMap<>();

        emulatorKeyHashMap.put(DIGIT0, createEmulatorKey("test", () -> {
        }));

        emulatorKeyHashMap.get(DIGIT0).trigger();

        boolean contains = KeyConfiguration.createKeyConfiguration(emulatorKeyHashMap).contains(DIGIT0);

        assertEquals(true, contains);
    }

    @Test
    public void getOrNOP() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        HashMap<KeyCode, EmulatorKey> emulatorKeyHashMap = new HashMap<>();

        emulatorKeyHashMap.put(DIGIT0, createEmulatorKey("test", () -> atomicBoolean.set(false)));
        KeyConfiguration.createKeyConfiguration(emulatorKeyHashMap).getOrNOP(DIGIT1).trigger();

        assertEquals(true, atomicBoolean.get());
    }

    @Test
    public void createKeyConfigurationEmulator() {
        KeyConfiguration keyConfiguration = KeyConfiguration.createKeyConfiguration(createEmulator());

        assertEquals("1", keyConfiguration.getOrNOP(DIGIT1).getKeyName());
        assertEquals("2", keyConfiguration.getOrNOP(DIGIT2).getKeyName());
        assertEquals("3", keyConfiguration.getOrNOP(DIGIT3).getKeyName());
        assertEquals("C", keyConfiguration.getOrNOP(DIGIT4).getKeyName());
    }

    @Test
    public void setEmulatorKeyHashMap() {
        KeyConfiguration keyConfiguration = KeyConfiguration.createKeyConfiguration(createEmulator());

        keyConfiguration.setEmulatorKeyHashMap(new HashMap<>());

        assertEquals("NOP", keyConfiguration.getOrNOP(DIGIT1).getKeyName());
    }
}