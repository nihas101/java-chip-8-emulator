package de.nihas101.chip8.utils;

public class Constants {
    public static final int MEMORY_LENGTH = 0xfff; // 4095
    public static final int REGISTER_LENGTH = 16;

    /* en.wikipedia.org/wiki/CHIP-8: "Most programs written for the original system begin at hardware location 512 (0x200)" */
    public static final short PROGRAM_COUNTER_START = (short) 0x200; // 512

    /* 60 Hertz are about 17 ms */
    public static final long HERTZ_60 = 17;

    public static final long CYCLE_WAIT_TIME = 2;
    public static final long STEP_WAIT_TIME = 100;

    public static final char CYCLES_CHAR = 'c';
    public static final char OPCODE_CHAR = 'o';
    public static final char MEMORY_CHAR = 'm';
    public static final char SCREEN_MEMORY_CHAR = 's';
    public static final char REGISTERS_CHAR = 'r';
    public static final char ADDRESS_CHAR = 'a';
    public static final char PROGRAM_COUNTER_CHAR = 'p';
    public static final char STACK_CHAR = 'k';
    public static final char DELAY_TIMER_CHAR = 'd';
    public static final char SOUND_TIMER_CHAR = 't';
    public static final char RANDOM_CHAR = 'i';

    public static final int DRAW_INTERVAL_MULTIPLIER = 35;
}
