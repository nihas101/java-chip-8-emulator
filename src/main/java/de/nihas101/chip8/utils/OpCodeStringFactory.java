package de.nihas101.chip8.utils;

import static java.lang.Integer.toHexString;

public class OpCodeStringFactory {
    public static String createRegOpCodeString(String operation, int register) {
        return "reg_" + operation + "(V" + toHexString(register) + ", &I)";
    }

    public static String createArithmeticOpCodeString(String operation, int targetRegister, int sourceRegister){
        return "V" + toHexString(targetRegister) + " " + operation + "= V" + toHexString(sourceRegister);
    }

    public static String createLogicOpOpCodeString(String operation, int targetRegister, int sourceRegister){
        return "V" + toHexString(targetRegister) + "=V" + toHexString(targetRegister) +  operation + "V" + toHexString(sourceRegister);
    }

    public static String createTimerOpString(String timerName, int value){
        return timerName + "(V" + toHexString(value) + ")";
    }
}
