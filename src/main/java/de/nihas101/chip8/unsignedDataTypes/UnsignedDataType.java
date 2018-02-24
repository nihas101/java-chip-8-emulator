package de.nihas101.chip8.unsignedDataTypes;

/**
 * Represents a data type with no sign
 */
public abstract class UnsignedDataType implements Comparable {
    public final int unsignedDataType;
    protected boolean overflow = false;

    protected UnsignedDataType(int unsignedDataType) {
        this.unsignedDataType = unsignedDataType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnsignedDataType)) return false;

        UnsignedDataType that = (UnsignedDataType) o;

        return unsignedDataType == that.unsignedDataType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.unsignedDataType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Object o) {
        return unsignedDataType - ((UnsignedDataType) o).unsignedDataType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.valueOf(unsignedDataType);
    }

    /**
     * Applies a binary operation on this data type
     *
     * @param binaryOperation The operation to apply
     * @param unsignedByte    The second operand
     * @return The result of the operation
     */
    public UnsignedByte apply(BinaryOperation binaryOperation, UnsignedByte unsignedByte) {
        return new UnsignedByte(binaryOperation.operation(this.unsignedDataType, unsignedByte.unsignedDataType));
    }

    /**
     * Applies a binary operation on this data type
     *
     * @param binaryOperation The operation to apply
     * @param unsignedShort   The second operand
     * @return The result of the operation
     */
    public UnsignedShort apply(BinaryOperation binaryOperation, UnsignedShort unsignedShort) {
        return new UnsignedShort(binaryOperation.operation(this.unsignedDataType, unsignedShort.unsignedDataType));
    }

    public boolean lastOperationLeadToOverflow() {
        return overflow;
    }
}
