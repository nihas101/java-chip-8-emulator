package de.nihas101.chip8.hardware.timers;

import de.nihas101.chip8.debug.Debuggable;

/**
 * A class representing an abstract timer of the Chip-8
 */
public abstract class Timer implements Debuggable{
    protected int value;
    private Interrupt onZero = null;

    public Timer(){
    }

    public Timer(int value){
        this.value = value;
    }

    /**
     * Sets the value of the timer
     * @param value The value to be set
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Decrements the timer
     */
    public void decrementValue(){
        if(this.value != 0)
            this.value--;
        else if(onZero != null){
            onZero.interrupt();
        }
    }

    /**
     * Returns the value of the timer
     * @return The value of the timer
     */
    public int getValue(){
        return value;
    }

    /**
     * Sets the interrupt to be called on the timer hitting zero
     * @param interrupt The interrupt to be called
     */
    public void setOnZero(Interrupt interrupt){
        this.onZero = interrupt;
    }

    public void reset(){
        value = 0;
    }
}
