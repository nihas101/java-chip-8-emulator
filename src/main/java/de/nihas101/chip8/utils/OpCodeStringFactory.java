package de.nihas101.chip8.utils;

public class OpCodeStringFactory {
    public static String createRegOpCodeString(String op, int Vx){
        return "reg_" + op + "(V" + Integer.toHexString(Vx) + ", &I)";
    }
}
