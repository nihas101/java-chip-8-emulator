package de.nihas101.chip8.utils;

public class Constants {
    public static final int MEMORY_LENGTH = 0xfff; // 4095
    public static final int REGISTER_LENGTH = 16;

    /* en.wikipedia.org/wiki/CHIP-8: "Most programs written for the original system begin at hardware location 512 (0x200)" */
    public static final short PROGRAM_COUNTER_START = (short) 0x200; // 512

    /* Key inputs */
    public static final int KEY_0 = 0x0;
    public static final int KEY_1 = 0x1;
    public static final int KEY_2 = 0x2;
    public static final int KEY_3 = 0x3;
    public static final int KEY_4 = 0x4;
    public static final int KEY_5 = 0x5;
    public static final int KEY_6 = 0x6;
    public static final int KEY_7 = 0x7;
    public static final int KEY_8 = 0x8;
    public static final int KEY_9 = 0x9;
    public static final int KEY_A = 0xA;
    public static final int KEY_B = 0xB;
    public static final int KEY_C = 0xC;
    public static final int KEY_D = 0xD;
    public static final int KEY_E = 0xE;
    public static final int KEY_F = 0xF;
    public static final int NO_KEY = 0xFF;

    /* 60 Hertz are about 17 ms */
    public static final long HERTZ_60 = 17;

    public static long CYCLE_WAIT_TIME = 2;
    public static long STEP_WAIT_TIME = 100;
}
