package de.nihas101.chip8.unsignedDataTypes;

/**
 * Represents an unsigned short
 */
public class UnsignedShort extends UnsignedDataType {
    public UnsignedShort(short signedShort) {
        super(Short.toUnsignedInt(signedShort));
    }

    /**
     * Applies the given operation
     *
     * @param unaryOperation The operation to execute
     * @return The result of the operation
     */
    public UnsignedShort apply(UnaryOperation unaryOperation) {
        return new UnsignedShort((short) unaryOperation.operation(this.unsignedDataType));
    }
}
