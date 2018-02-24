package de.nihas101.chip8.hardware.timers;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;

public class TimerTest {

    @Test
    public void setValue() {
        Timer timer = new Timer(0) {
            @Override
            public String getState() {
                return "";
            }
        };

        timer.setValue(10);

        assertEquals(10, timer.getValue());
    }

    @Test
    public void decrementValue() {
        Timer timer = new Timer(10) {
            @Override
            public String getState() {
                return "";
            }
        };

        timer.decrementValue();

        assertEquals(9, timer.getValue());
    }

    @Test
    public void getValue() {
        Timer timer = new Timer(10) {
            @Override
            public String getState() {
                return "";
            }
        };

        assertEquals(10, timer.getValue());
    }

    @Test
    public void setOnZero() {
        AtomicBoolean zeroReached = new AtomicBoolean(false);
        Timer timer = new Timer(2) {
            @Override
            public String getState() {
                return "";
            }
        };

        timer.setOnZero(() -> zeroReached.set(true));
        assertEquals(false, zeroReached.get());
        timer.decrementValue();
        assertEquals(false, zeroReached.get());
        timer.decrementValue();

        assertEquals(true, zeroReached.get());
    }

    @Test
    public void reset() {
        AtomicBoolean zeroReached = new AtomicBoolean(false);
        Timer timer = new Timer(100) {
            @Override
            public String getState() {
                return "";
            }
        };

        timer.reset();

        assertEquals(0, timer.getValue());
    }
}