package de.nihas101.chip8.unsignedDataTypes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnsignedDataTypeTest {

    @Test
    public void equals() {
        UnsignedByte unsignedByte1 = new UnsignedByte(10);
        UnsignedByte unsignedByte2 = new UnsignedByte(10);

        assertEquals(true, unsignedByte1.equals(unsignedByte2));
    }

    @Test
    public void compareTo() {
        UnsignedByte unsignedByte1 = new UnsignedByte(10);
        UnsignedByte unsignedByte2 = new UnsignedByte(15);

        assertEquals(5, unsignedByte2.compareTo(unsignedByte1));
    }

    @Test
    public void apply() {
        UnsignedByte unsignedByte = new UnsignedByte(10);

        assertEquals(5, unsignedByte.apply((x, y) -> x - y, new UnsignedByte(5)).unsignedDataType);
    }

    @Test
    public void apply1() {
        UnsignedShort unsignedShort = new UnsignedShort(10);

        assertEquals(5, unsignedShort.apply((x, y) -> x - y, new UnsignedShort(5)).unsignedDataType);
    }

    @Test
    public void hashCodeTest() {
        assertEquals(5, new UnsignedShort(5).hashCode());
    }

    @Test
    public void toStringTest() {
        assertEquals("5", new UnsignedShort(5).toString());
    }

    @Test
    public void lastOperationLeadToOverflow255() {
        assertEquals(true, new UnsignedByte(254).apply(x -> x + 1).lastOperationLeadToOverflow());
    }

    @Test
    public void lastOperationLeadToOverflow_1() {
        assertEquals(true, new UnsignedByte(0).apply(x -> x - 1).lastOperationLeadToOverflow());
    }
}