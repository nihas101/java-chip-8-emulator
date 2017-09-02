package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;
import de.nihas101.chip8.debug.Debuggable;

import static de.nihas101.chip8.utils.Constants.REGISTER_LENGTH;

/**
 * A class representing the registers of a Chip-8
 */
public class Chip8Registers implements Debuggable {
    private UnsignedByte ZERO = new UnsignedByte((byte) 0);
    /**
     * 16 registers, 1 byte each
     */
    private final UnsignedByte[] registers = new UnsignedByte[REGISTER_LENGTH];

    public Chip8Registers(){
        for(int i=0 ; i < registers.length ; i++)
            registers[i] = ZERO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return
                "V0: " + Integer.toHexString(registers[0x0].unsignedDataType) + "   " +
                "V1: " + Integer.toHexString(registers[0x1].unsignedDataType) + "   " +
                "V2: " + Integer.toHexString(registers[0x2].unsignedDataType) + "   " +
                "V3: " + Integer.toHexString(registers[0x3].unsignedDataType) + "   " +
                "V4: " + Integer.toHexString(registers[0x4].unsignedDataType) + "   " +
                "V5: " + Integer.toHexString(registers[0x5].unsignedDataType) + "   " +
                "V6: " + Integer.toHexString(registers[0x6].unsignedDataType) + "   " +
                "V7: " + Integer.toHexString(registers[0x7].unsignedDataType) + "\n" +
                "V8: " + Integer.toHexString(registers[0x8].unsignedDataType) + "   " +
                "V9: " + Integer.toHexString(registers[0x9].unsignedDataType) + "   " +
                "VA: " + Integer.toHexString(registers[0xA].unsignedDataType) + "   " +
                "VB: " + Integer.toHexString(registers[0xB].unsignedDataType) + "   " +
                "VC: " + Integer.toHexString(registers[0xC].unsignedDataType) + "   " +
                "VD: " + Integer.toHexString(registers[0xD].unsignedDataType) + "   " +
                "VE: " + Integer.toHexString(registers[0xE].unsignedDataType) + "   " +
                "VF: " + Integer.toHexString(registers[0xF].unsignedDataType);
    }

    /**
     * Returns the value held by the register with the index Vx
     * @param Vx The index of the source register
     * @return The value inside the register
     */
    public UnsignedByte peek(int Vx){
        return registers[Vx];
    }

    /**
     * Writes a value to the register with the index Vx
     * @param Vx The index of the destination register
     * @param unsignedByte The value to be written into the register
     */
    public void poke(int Vx, UnsignedByte unsignedByte){
        registers[Vx] = unsignedByte;
    }

    public void clear() {
        for(int i=0 ; i < registers.length ; i++)
            registers[i] = ZERO;
    }
}
