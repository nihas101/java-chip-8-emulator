package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.unsignedDataTypes.UnsignedShort;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.assertEquals;

public class Chip8StackTest {

    @Test
    public void getState() {
        assertEquals("Chip8Stack:\t[]", new Chip8Stack(new Stack<>()).getState());
    }

    @Test
    public void push() {
        Chip8Stack chip8Stack = new Chip8Stack(new Stack<>());

        chip8Stack.push(new UnsignedShort(1));
        chip8Stack.push(new UnsignedShort(2));
        chip8Stack.push(new UnsignedShort(3));

        assertEquals("[1, 2, 3]", chip8Stack.getValues());
    }

    @Test
    public void pop() {
        Chip8Stack chip8Stack = new Chip8Stack(new Stack<>());

        chip8Stack.push(new UnsignedShort(1));
        chip8Stack.push(new UnsignedShort(2));
        chip8Stack.push(new UnsignedShort(3));

        assertEquals(3, chip8Stack.pop().unsignedDataType);
    }

    @Test
    public void getSize() {
        Chip8Stack chip8Stack = new Chip8Stack(new Stack<>());

        chip8Stack.push(new UnsignedShort(1));
        chip8Stack.push(new UnsignedShort(2));
        chip8Stack.push(new UnsignedShort(3));

        assertEquals(3, chip8Stack.getSize());
    }

    @Test
    public void clear() {
        Chip8Stack chip8Stack = new Chip8Stack(new Stack<>());

        chip8Stack.push(new UnsignedShort(1));
        chip8Stack.push(new UnsignedShort(2));
        chip8Stack.push(new UnsignedShort(3));

        chip8Stack.clear();

        assertEquals(0, chip8Stack.getSize());
    }

    @Test
    public void getValues() {
        Chip8Stack chip8Stack = new Chip8Stack(new Stack<>());

        assertEquals("[]", chip8Stack.getValues());
    }
}