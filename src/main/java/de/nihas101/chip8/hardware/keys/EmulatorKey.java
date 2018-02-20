package de.nihas101.chip8.hardware.keys;

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
}
