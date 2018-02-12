package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.debug.Debuggable;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;

import java.util.Arrays;

import static de.nihas101.chip8.utils.Constants.REGISTER_LENGTH;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.toHexString;

/**
 * A class representing the registers of a Chip-8
 */
public class Registers implements Debuggable {
    private UnsignedByte ZERO = new UnsignedByte((byte) 0);
    /**
     * 16 registers, 1 byte each
     */
    private final UnsignedByte[] registers = new UnsignedByte[REGISTER_LENGTH];

    public Registers(){
        for(int i=0 ; i < registers.length ; i++)
            registers[i] = ZERO;
    }

    public Registers(String[] registerStrings) {
        for(int i=0; i < registers.length; i++){
            registers[i] = new UnsignedByte((byte) parseInt(registerStrings[i]));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return
                "V0: " + toHexString(registers[0x0].unsignedDataType) + "   " +
                "V1: " + toHexString(registers[0x1].unsignedDataType) + "   " +
                "V2: " + toHexString(registers[0x2].unsignedDataType) + "   " +
                "V3: " + toHexString(registers[0x3].unsignedDataType) + "   " +
                "V4: " + toHexString(registers[0x4].unsignedDataType) + "   " +
                "V5: " + toHexString(registers[0x5].unsignedDataType) + "   " +
                "V6: " + toHexString(registers[0x6].unsignedDataType) + "   " +
                "V7: " + toHexString(registers[0x7].unsignedDataType) + "\n" +
                "V8: " + toHexString(registers[0x8].unsignedDataType) + "   " +
                "V9: " + toHexString(registers[0x9].unsignedDataType) + "   " +
                "VA: " + toHexString(registers[0xA].unsignedDataType) + "   " +
                "VB: " + toHexString(registers[0xB].unsignedDataType) + "   " +
                "VC: " + toHexString(registers[0xC].unsignedDataType) + "   " +
                "VD: " + toHexString(registers[0xD].unsignedDataType) + "   " +
                "VE: " + toHexString(registers[0xE].unsignedDataType) + "   " +
                "VF: " + toHexString(registers[0xF].unsignedDataType);
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

    public String getValues() {
        return Arrays.toString(registers);
    }
}
