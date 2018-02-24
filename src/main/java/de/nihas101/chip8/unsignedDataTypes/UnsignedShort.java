package de.nihas101.chip8.unsignedDataTypes;

import static java.lang.Short.toUnsignedInt;

/**
 * Represents an unsigned short
 */
public class UnsignedShort extends UnsignedDataType {
    public UnsignedShort(int signedShort) {
        super(toUnsignedInt((short) signedShort));
        overflow = (signedShort < 0 || signedShort > 65535);
    }

    /**
     * Applies the given operation
     *
     * @param unaryOperation The operation to execute
     * @return The result of the operation
     */
    public UnsignedShort apply(UnaryOperation unaryOperation) {
        return new UnsignedShort(unaryOperation.operation(this.unsignedDataType));
    }
}
