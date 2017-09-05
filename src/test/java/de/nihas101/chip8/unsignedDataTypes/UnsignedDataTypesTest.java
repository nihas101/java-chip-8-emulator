package de.nihas101.chip8.unsignedDataTypes;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnsignedDataTypesTest {
    @Test
    public void equalsTest0(){
        assertEquals(new UnsignedByte((byte) 0), new UnsignedByte((byte) 0));
    }

    @Test
    public void equalsTest1(){
        assertEquals(new UnsignedShort((byte) 0), new UnsignedByte((byte) 0));
    }

    @Test
    public void equalsTest2(){
        assertEquals(new UnsignedShort((byte) 0), new UnsignedShort((byte) 0));
    }
}
