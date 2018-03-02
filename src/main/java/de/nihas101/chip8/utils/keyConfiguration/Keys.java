package de.nihas101.chip8.utils.keyConfiguration;

public class Keys {
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

    public static int valueOf(String keyString) throws UnknownEmulatorKeyException {
        int key;

        switch (keyString) {
            case "0":
                key = KEY_0;
                break;
            case "1":
                key = KEY_1;
                break;
            case "2":
                key = KEY_2;
                break;
            case "3":
                key = KEY_3;
                break;
            case "4":
                key = KEY_4;
                break;
            case "5":
                key = KEY_5;
                break;
            case "6":
                key = KEY_6;
                break;
            case "7":
                key = KEY_7;
                break;
            case "8":
                key = KEY_8;
                break;
            case "9":
                key = KEY_9;
                break;
            case "A":
                key = KEY_A;
                break;
            case "B":
                key = KEY_B;
                break;
            case "C":
                key = KEY_C;
                break;
            case "D":
                key = KEY_D;
                break;
            case "E":
                key = KEY_E;
                break;
            case "F":
                key = KEY_F;
                break;
            default:
                throw new UnknownEmulatorKeyException(keyString);
        }

        return key;
    }
}
