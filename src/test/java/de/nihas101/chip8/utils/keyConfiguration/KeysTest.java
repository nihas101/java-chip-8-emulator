package de.nihas101.chip8.utils.keyConfiguration;

import org.junit.Test;

import static de.nihas101.chip8.utils.keyConfiguration.Keys.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class KeysTest {

    @Test
    public void valueOf0() throws UnknownEmulatorKeyException {
        assertEquals(KEY_0, Keys.valueOf("0"));
    }

    @Test
    public void valueOf1() throws UnknownEmulatorKeyException {
        assertEquals(KEY_1, Keys.valueOf("1"));
    }

    @Test
    public void valueOf2() throws UnknownEmulatorKeyException {
        assertEquals(KEY_2, Keys.valueOf("2"));
    }

    @Test
    public void valueOf3() throws UnknownEmulatorKeyException {
        assertEquals(KEY_3, Keys.valueOf("3"));
    }

    @Test
    public void valueOf4() throws UnknownEmulatorKeyException {
        assertEquals(KEY_4, Keys.valueOf("4"));
    }

    @Test
    public void valueOf5() throws UnknownEmulatorKeyException {
        assertEquals(KEY_5, Keys.valueOf("5"));
    }

    @Test
    public void valueOf6() throws UnknownEmulatorKeyException {
        assertEquals(KEY_6, Keys.valueOf("6"));
    }

    @Test
    public void valueOf7() throws UnknownEmulatorKeyException {
        assertEquals(KEY_7, Keys.valueOf("7"));
    }

    @Test
    public void valueOf8() throws UnknownEmulatorKeyException {
        assertEquals(KEY_8, Keys.valueOf("8"));
    }

    @Test
    public void valueOf9() throws UnknownEmulatorKeyException {
        assertEquals(KEY_9, Keys.valueOf("9"));
    }

    @Test
    public void valueOfA() throws UnknownEmulatorKeyException {
        assertEquals(KEY_A, Keys.valueOf("A"));
    }

    @Test
    public void valueOfB() throws UnknownEmulatorKeyException {
        assertEquals(KEY_B, Keys.valueOf("B"));
    }

    @Test
    public void valueOfC() throws UnknownEmulatorKeyException {
        assertEquals(KEY_C, Keys.valueOf("C"));
    }

    @Test
    public void valueOfD() throws UnknownEmulatorKeyException {
        assertEquals(KEY_D, Keys.valueOf("D"));
    }

    @Test
    public void valueOfE() throws UnknownEmulatorKeyException {
        assertEquals(KEY_E, Keys.valueOf("E"));
    }

    @Test
    public void valueOfF() throws UnknownEmulatorKeyException {
        assertEquals(KEY_F, Keys.valueOf("F"));
    }

    @Test
    public void valueOfException() {
        try {
            Keys.valueOf("ERROR");
        } catch (UnknownEmulatorKeyException exception) {
            return;
        }

        fail("UnknownEmulatorKeyException was not thrown");
    }
}