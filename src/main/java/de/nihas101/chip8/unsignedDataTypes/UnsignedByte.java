package de.nihas101.chip8.unsignedDataTypes;

import static java.lang.Byte.toUnsignedInt;

/**
 * Represents an unsigned byte
 */
public class UnsignedByte extends UnsignedDataType {
    public UnsignedByte(int signedByte) {
        super(toUnsignedInt((byte) signedByte));
        overflow = (signedByte < 0 || signedByte > 255);
    }

    /**
     * Applies the given operation
     *
     * @param unaryOperation The operation to execute
     * @return The result of the operation
     */
    public UnsignedByte apply(UnaryOperation unaryOperation) {
        return new UnsignedByte((byte) unaryOperation.operation(this.unsignedDataType));
    }
}
