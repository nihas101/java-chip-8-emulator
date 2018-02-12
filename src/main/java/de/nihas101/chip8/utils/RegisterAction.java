package de.nihas101.chip8.utils;

public interface RegisterAction {
    void execute(int registerAddress, int memoryAddress);
}
