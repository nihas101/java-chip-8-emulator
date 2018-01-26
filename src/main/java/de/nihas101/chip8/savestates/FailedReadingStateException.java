package de.nihas101.chip8.savestates;

import java.io.File;

public class FailedReadingStateException extends Throwable {
    public FailedReadingStateException(File file){
        super("Reading " + file.getAbsolutePath() + " failed.");
    }
}
