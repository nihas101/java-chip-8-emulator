package de.nihas101.chip8.utils;

import de.nihas101.chip8.hardware.keys.EmulatorKey;
import javafx.scene.input.KeyCode;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static de.nihas101.chip8.hardware.keys.EmulatorKey.createEmulatorKey;
import static javafx.scene.input.KeyCode.DIGIT0;
import static javafx.scene.input.KeyCode.DIGIT1;
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
        AtomicBoolean fail = new AtomicBoolean(true);
        HashMap<KeyCode, EmulatorKey> emulatorKeyHashMap = new HashMap<>();

        emulatorKeyHashMap.put(DIGIT0, createEmulatorKey("test", () -> {
        }));
        Set<Map.Entry<KeyCode, EmulatorKey>> entries = KeyConfiguration.createKeyConfiguration(emulatorKeyHashMap).entrySet();

        assertEquals("[DIGIT0=test]", entries.toString());
    }

    @Test
    public void contains() {
        HashMap<KeyCode, EmulatorKey> emulatorKeyHashMap = new HashMap<>();

        emulatorKeyHashMap.put(DIGIT0, createEmulatorKey("test", () -> {
        }));
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
}