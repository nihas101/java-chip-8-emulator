package de.nihas101.chip8.hardware.timers;

import de.nihas101.chip8.debug.Debuggable;

/**
 * Represents the delay timer of the Chip-8
 */
public class DelayTimer extends Chip8Timer implements Debuggable{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return "DelayTimer: " + this.value;
    }
}
