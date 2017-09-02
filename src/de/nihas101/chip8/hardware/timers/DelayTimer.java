package de.nihas101.chip8.hardware.timers;

/**
 * Represents the delay timer of the Chip-8
 */
public class DelayTimer extends Chip8Timer {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return "DelayTimer: " + this.value;
    }
}
