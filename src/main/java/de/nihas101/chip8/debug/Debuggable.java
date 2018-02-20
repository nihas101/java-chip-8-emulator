package de.nihas101.chip8.debug;

/**
 * Represents a debuggable {@link Object}
 */
public interface Debuggable {
    /**
     * Returns the current state of the {@link Object}
     *
     * @return The current state of the {@link Object}
     */
    String getState();
}
