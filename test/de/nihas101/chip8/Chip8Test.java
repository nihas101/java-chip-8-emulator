package de.nihas101.chip8;

import de.nihas101.chip8.hardware.*;
import de.nihas101.chip8.hardware.memory.*;
import de.nihas101.chip8.hardware.timers.DelayTimer;
import de.nihas101.chip8.hardware.timers.SoundTimer;
import de.nihas101.chip8.unsignedDataTypes.UnsignedByte;
import de.nihas101.chip8.utils.RomLoader;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Stack;
import java.util.Timer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Chip8Test {
    @Test
    void pongMemoryTest(){
        Chip8Memory chip8Memory = new Chip8Memory();
        Chip8CentralProcessingUnit cpu = new Chip8CentralProcessingUnit(
                                new Chip8Memory(),
                                new ScreenMemory(),
                                new Chip8Registers(),
                                new Chip8AddressRegister(),
                                new Chip8ProgramCounter(),
                                new Chip8Stack(new Stack<>()),
                                new Timer("Timer"),
                                new DelayTimer(),
                                new SoundTimer(),
                                new Random(),
                                null);

        new RomLoader().loadRom("resources/roms/PONG", chip8Memory);

        assertEquals(new UnsignedByte((byte) 106), chip8Memory.read(512));
    }
}