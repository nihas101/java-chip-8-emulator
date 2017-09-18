package de.nihas101.chip8.opcodes;

public class UnknownOPCodeException extends Exception {
    public UnknownOPCodeException(String message){
        super(message);
    }

    public UnknownOPCodeException(OPCode opCode){
        super("Unknown OpCode encountered: " + Integer.toHexString(opCode.getOpCode()));
    }
}
