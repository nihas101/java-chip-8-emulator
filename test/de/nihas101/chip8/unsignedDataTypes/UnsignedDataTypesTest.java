package de.nihas101.chip8.unsignedDataTypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnsignedDataTypesTest {
    @Test
    void equalsTest0(){
        assertEquals(new UnsignedByte((byte) 0), new UnsignedByte((byte) 0));
    }

    @Test
    void equalsTest1(){
        assertEquals(new UnsignedShort((byte) 0), new UnsignedByte((byte) 0));
    }

    @Test
    void equalsTest2(){
        assertEquals(new UnsignedShort((byte) 0), new UnsignedShort((byte) 0));
    }
}
