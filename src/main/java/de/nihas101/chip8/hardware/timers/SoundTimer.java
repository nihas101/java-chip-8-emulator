package de.nihas101.chip8.hardware.timers;

/**
 * A class represents a sound timer of the Chip-8
 */
public class SoundTimer extends Timer {
    private Interrupt onValue;

    public SoundTimer() {
        super();
    }

    public SoundTimer(int value) {
        super(value);
    }

    /**
     * {@inheritDoc}.
     * Additionally executes an interrupt
     */
    @Override
    public void setValue(int value) {
        super.setValue(value);
        onValue.interrupt();
    }

    /**
     * Sets an interrupt to be called on setting a value
     *
     * @param onValue The interrupt to be called
     */
    public void setOnValue(Interrupt onValue) {
        this.onValue = onValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return "SoundTimer: " + this.value;
    }
}
