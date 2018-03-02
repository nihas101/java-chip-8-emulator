package de.nihas101.chip8.hardware.keys;

import de.nihas101.chip8.hardware.CentralProcessingUnit;
import de.nihas101.chip8.utils.keyConfiguration.Keys;
import de.nihas101.chip8.utils.keyConfiguration.UnknownEmulatorKeyException;

public class EmulatorKey {
    private final String keyName;
    private final EmulatorKeyEvent emulatorKeyEvent;

    private EmulatorKey(String keyName, EmulatorKeyEvent emulatorKeyEvent) {
        this.keyName = keyName;
        this.emulatorKeyEvent = emulatorKeyEvent;
    }

    public static EmulatorKey createEmulatorKey(String keyName, EmulatorKeyEvent emulatorKeyEvent) {
        return new EmulatorKey(keyName, emulatorKeyEvent);
    }

    public String getKeyName() {
        return keyName;
    }

    public void trigger() {
        emulatorKeyEvent.trigger();
    }

    @Override
    public String toString() {
        return keyName;
    }

    public static EmulatorKey createEmulatorKey(String keyName, CentralProcessingUnit centralProcessingUnit) throws UnknownEmulatorKeyException {
        int keyCode = Keys.valueOf(keyName);
        return new EmulatorKey(keyName, () -> centralProcessingUnit.setKeyCode(keyCode));
    }
}
