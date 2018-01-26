package de.nihas101.chip8.hardware.timers;

/**
 * Represents the delay timer of the Chip-8
 */
public class DelayTimer extends Timer {

    public DelayTimer(){
        super();
    }

    public DelayTimer(int value) {
        super(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return "DelayTimer: " + this.value;
    }
}
