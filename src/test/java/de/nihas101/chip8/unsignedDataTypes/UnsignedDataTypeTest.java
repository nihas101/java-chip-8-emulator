package de.nihas101.chip8.unsignedDataTypes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnsignedDataTypeTest {

    @Test
    public void equals() {
        UnsignedByte unsignedByte1 = new UnsignedByte((byte) 10);
        UnsignedByte unsignedByte2 = new UnsignedByte((byte) 10);

        assertEquals(true, unsignedByte1.equals(unsignedByte2));
    }

    @Test
    public void compareTo() {
        UnsignedByte unsignedByte1 = new UnsignedByte((byte) 10);
        UnsignedByte unsignedByte2 = new UnsignedByte((byte) 15);

        assertEquals(5, unsignedByte2.compareTo(unsignedByte1));
    }

    @Test
    public void apply() {
        UnsignedByte unsignedByte = new UnsignedByte((byte) 10);

        assertEquals(5, unsignedByte.apply((x, y) -> x - y, new UnsignedByte((byte) 5)).unsignedDataType);
    }

    @Test
    public void apply1() {
        UnsignedShort unsignedShort = new UnsignedShort((byte) 10);

        assertEquals(5, unsignedShort.apply((x, y) -> x - y, new UnsignedShort((byte) 5)).unsignedDataType);
    }
}