package de.nihas101.chip8.hardware.memory;

import de.nihas101.chip8.debug.Debuggable;
import de.nihas101.chip8.unsignedDataTypes.UnsignedShort;

/**
 * A class representing an AddressRegister of a Chip-8
 */
public class AddressRegister implements Debuggable {
    /**
     * The 16-bit address register I
     */
    private UnsignedShort address;

    public AddressRegister(){
        this.address = new UnsignedShort((short) 0);
    }

    public AddressRegister(UnsignedShort address) {
        this.address = address;
    }

    /**
     * Returns the {@link UnsignedShort} representing the value of this register
     * @return The {@link UnsignedShort} representing the value of this register
     */
    public UnsignedShort getAddress() {
        return address;
    }

    /**
     * Sets the {@link UnsignedShort} representing the value of this register
     * @param unsignedShort The value to be set in this register
     */
    public void setAddress(UnsignedShort unsignedShort){
        this.address = unsignedShort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() {
        return "I:\t" + address;
    }
}
