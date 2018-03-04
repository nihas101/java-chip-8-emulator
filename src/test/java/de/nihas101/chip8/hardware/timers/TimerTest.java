package de.nihas101.chip8.hardware.timers;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;

public class TimerTest {

    @Test
    public void setValue() {
        Timer timer = createTestTimer(0);

        timer.setValue(10);

        assertEquals(10, timer.getValue());
    }

    @Test
    public void decrementValue() {
        Timer timer = createTestTimer(10);

        timer.decrementValue();

        assertEquals(9, timer.getValue());
    }

    @Test
    public void getValue() {
        Timer timer = createTestTimer(10);

        assertEquals(10, timer.getValue());
    }

    @Test
    public void setOnZeroBeforeDecrement() {
        AtomicBoolean zeroReached = new AtomicBoolean(false);
        Timer timer = createTestTimer(2);
        timer.setOnZero(() -> zeroReached.set(true));
        
        assertEquals(false, zeroReached.get());
    }

    @Test
    public void setOnZeroBeforeDecrementTwo() {
        AtomicBoolean zeroReached = new AtomicBoolean(false);
        Timer timer = createTestTimer(2);
        timer.setOnZero(() -> zeroReached.set(true));

        timer.decrementValue();
        assertEquals(false, zeroReached.get());
    }

    @Test
    public void setOnZeroDecrementToZero() {
        AtomicBoolean zeroReached = new AtomicBoolean(false);
        Timer timer = createTestTimer(2);
        timer.setOnZero(() -> zeroReached.set(true));

        timer.decrementValue();
        timer.decrementValue();
        assertEquals(true, zeroReached.get());
    }

    @Test
    public void reset() {
        Timer timer = createTestTimer(100);

        timer.reset();

        assertEquals(0, timer.getValue());
    }

    private Timer createTestTimer(int time){
        return new Timer(time) {
            @Override
            public String getState() {
                return "";
            }
        };
    }
}