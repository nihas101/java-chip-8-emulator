package de.nihas101.chip8.utils;

import de.nihas101.chip8.hardware.CentralProcessingUnit;
import de.nihas101.chip8.hardware.Emulator;
import de.nihas101.chip8.hardware.keys.EmulatorKey;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.nihas101.chip8.hardware.keys.EmulatorKey.createEmulatorKey;
import static de.nihas101.chip8.utils.Constants.*;
import static javafx.scene.input.KeyCode.*;

public class KeyConfiguration {
    private Map<KeyCode, EmulatorKey> emulatorKeyHashMap;
    private static EmulatorKey nOpEmulatorKey = EmulatorKey.createEmulatorKey("NOP", () -> {
    });

    private KeyConfiguration(Map<KeyCode, EmulatorKey> emulatorKeyHashMap) {
        this.emulatorKeyHashMap = emulatorKeyHashMap;
    }

    public static KeyConfiguration createKeyConfiguration(Emulator emulator) {
        CentralProcessingUnit centralProcessingUnit = emulator.getCentralProcessingUnit();
        HashMap<KeyCode, EmulatorKey> emulatorKeyHashMap = createStandardEmulatorKeyHashMap(centralProcessingUnit);


        return new KeyConfiguration(emulatorKeyHashMap);
    }

    public static KeyConfiguration createKeyConfiguration(Map<KeyCode, EmulatorKey> emulatorKeyHashMap) {
        return new KeyConfiguration(emulatorKeyHashMap);
    }

    public void setEmulatorKeyHashMap(Map<KeyCode, EmulatorKey> emulatorKeyHashMap) {
        this.emulatorKeyHashMap = emulatorKeyHashMap;
    }

    private static HashMap<KeyCode, EmulatorKey> createStandardEmulatorKeyHashMap(CentralProcessingUnit centralProcessingUnit) {
        HashMap<KeyCode, EmulatorKey> emulatorKeyHashMap = new HashMap<>();

        emulatorKeyHashMap.put(X, createEmulatorKey("0", () -> centralProcessingUnit.setKeyCode(KEY_0)));
        emulatorKeyHashMap.put(DIGIT1, createEmulatorKey("1", () -> centralProcessingUnit.setKeyCode(KEY_1)));
        emulatorKeyHashMap.put(DIGIT2, createEmulatorKey("2", () -> centralProcessingUnit.setKeyCode(KEY_2)));
        emulatorKeyHashMap.put(DIGIT3, createEmulatorKey("3", () -> centralProcessingUnit.setKeyCode(KEY_3)));
        emulatorKeyHashMap.put(Q, createEmulatorKey("4", () -> centralProcessingUnit.setKeyCode(KEY_4)));
        emulatorKeyHashMap.put(W, createEmulatorKey("5", () -> centralProcessingUnit.setKeyCode(KEY_5)));
        emulatorKeyHashMap.put(E, createEmulatorKey("6", () -> centralProcessingUnit.setKeyCode(KEY_6)));
        emulatorKeyHashMap.put(A, createEmulatorKey("7", () -> centralProcessingUnit.setKeyCode(KEY_7)));
        emulatorKeyHashMap.put(S, createEmulatorKey("8", () -> centralProcessingUnit.setKeyCode(KEY_8)));
        emulatorKeyHashMap.put(D, createEmulatorKey("9", () -> centralProcessingUnit.setKeyCode(KEY_9)));

        emulatorKeyHashMap.put(Z, createEmulatorKey("A", () -> centralProcessingUnit.setKeyCode(KEY_A)));
        emulatorKeyHashMap.put(C, createEmulatorKey("B", () -> centralProcessingUnit.setKeyCode(KEY_B)));
        emulatorKeyHashMap.put(DIGIT4, createEmulatorKey("C", () -> centralProcessingUnit.setKeyCode(KEY_C)));
        emulatorKeyHashMap.put(R, createEmulatorKey("D", () -> centralProcessingUnit.setKeyCode(KEY_D)));
        emulatorKeyHashMap.put(F, createEmulatorKey("E", () -> centralProcessingUnit.setKeyCode(KEY_E)));
        emulatorKeyHashMap.put(V, createEmulatorKey("F", () -> centralProcessingUnit.setKeyCode(KEY_F)));

        return emulatorKeyHashMap;
    }

    public Set<Map.Entry<KeyCode, EmulatorKey>> entrySet() {
        return emulatorKeyHashMap.entrySet();
    }

    public boolean contains(KeyCode keyCode) {
        return emulatorKeyHashMap.containsKey(keyCode);
    }

    public EmulatorKey getOrNOP(KeyCode keyCode) {
        return emulatorKeyHashMap.getOrDefault(keyCode, nOpEmulatorKey);
    }
}
