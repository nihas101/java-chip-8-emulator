package de.nihas101.chip8.opcodes;

/**
 * Represents an opcode consisting of 4 bytes
 */
public class OPCode {
    private final int[] opcode = new int[4];
    private final int fullOpCode;

    public OPCode(int opcode) {
        fullOpCode = opcode;

        this.opcode[0] = (opcode & 0xF000) >> 12;
        this.opcode[1] = (opcode & 0x0F00) >> 8;
        this.opcode[2] = (opcode & 0x00F0) >> 4;
        this.opcode[3] = (opcode & 0x000F);
    }

    /**
     * Returns the byte according to the index
     *
     * @param index The index of the byte to be return (0-3)
     * @return The byte that corresponds to the index
     */
    public int getByte(int index) {
        return opcode[index];
    }

    /**
     * Returns the full opcode
     *
     * @return The full opcode
     */
    public int getOpCode() {
        return fullOpCode;
    }

    /**
     * Applies a bytemask to the opcode by using AND
     *
     * @param mask The mask to be applied
     * @return The opcode with the mask applied
     */
    public int applyMask(int mask) {
        return fullOpCode & mask;
    }
}
