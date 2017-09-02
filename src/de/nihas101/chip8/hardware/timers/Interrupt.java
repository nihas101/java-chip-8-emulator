package de.nihas101.chip8.hardware.timers;

/**
 * An interface representing an interrupt
 */
public interface Interrupt {
    /**
     * Executes a interrupt
     */
    void interrupt();
}
