package de.nihas101.chip8.unsignedDataTypes;

/**
 * Represents a Binary operation on two integers
 */
public interface BinaryOperation {
    /**
     * Executes an operation between two integers
     * @param x The first operand
     * @param y The second operand
     * @return The result of the operation
     */
    int operation(int x, int y);
}
