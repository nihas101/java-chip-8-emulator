package de.nihas101.chip8.utils.keyConfiguration;

public class UnknownEmulatorKeyException extends Exception {
    public UnknownEmulatorKeyException(String emulatorKey) {
        super("Unknown emulatorkey: " + emulatorKey);
    }
}
