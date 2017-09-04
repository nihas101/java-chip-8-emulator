package de.nihas101.chip8.unsignedDataTypes;

/**
 * Represents an unsigned byte
 */
public class UnsignedByte extends UnsignedDataType{
    public UnsignedByte(byte signedByte){
        super(Byte.toUnsignedInt(signedByte));
    }

    /**
     * Applies the given operation
     * @param unaryOperation The operation to execute
     * @return The result of the operation
     */
    public UnsignedByte apply(UnaryOperation unaryOperation){
        return new UnsignedByte((byte) unaryOperation.operation(this.unsignedDataType));
    }
}
