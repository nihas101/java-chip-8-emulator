package de.nihas101.chip8.unsignedDataTypes;

/**
 * Represents a unary operation on an operand
 */
public interface UnaryOperation {
    /**
     * Executes an operation on the operand
     *
     * @param x The  operand
     * @return The result of the operation
     */
    int operation(int x);
}
