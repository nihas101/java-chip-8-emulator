package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.debug.Debuggable;
import de.nihas101.chip8.unsignedDataTypes.UnsignedShort;

import java.util.Stack;

import static java.lang.Integer.parseInt;

/**
 * A class representing a stack of a Chip-8
 */
public class Chip8Stack implements Debuggable {
    /**
     * The 16-bit stack
     */
    private final Stack<UnsignedShort> stack;

    public Chip8Stack(Stack<UnsignedShort> stack) {
        this.stack = stack;
    }

    public Chip8Stack(java.util.Stack<UnsignedShort> stack, String[] strings) {
        this.stack = stack;

        if (strings.length == 1 && "".equals(strings[0]))
            return;

        for (String string : strings) stack.push(new UnsignedShort(parseInt(string)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return "Chip8Stack:\t" + stack.toString();
    }

    /**
     * Pushes The value onto the stack
     *
     * @param unsignedShort The value to be pushed onto the stack
     */
    public void push(UnsignedShort unsignedShort) {
        stack.push(unsignedShort);
    }

    /**
     * Pops a value from the stack and returns it
     *
     * @return The retrieved value
     */
    public UnsignedShort pop() {
        return stack.pop();
    }

    /**
     * Returns the size of the stack
     *
     * @return The size of the stack
     */
    public int getSize() {
        return this.stack.size();
    }

    public void clear() {
        this.stack.clear();
    }

    public String getValues() {
        return stack.toString();
    }
}
