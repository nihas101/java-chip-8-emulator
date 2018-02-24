package de.nihas101.chip8.savestates;

import de.nihas101.chip8.hardware.Emulator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static de.nihas101.chip8.hardware.Emulator.createEmulator;
import static de.nihas101.chip8.savestates.SaveState.createSaveState;
import static org.junit.Assert.assertEquals;

public class SaveStateHandlerTest {

    @Test
    public void readWriteState() throws IOException, FailedReadingStateException {
        Emulator emulator = createEmulator();
        SaveState saveState = createSaveState(emulator.getCentralProcessingUnit());
        SaveStateHandler saveStateHandler = new SaveStateHandler();

        File file = new File("src/test/resources/savestateTest.c8s");
        file.createNewFile();
        file.deleteOnExit();

        saveStateHandler.writeState(file, saveState);
        SaveState saveState1 = saveStateHandler.readState(file);

        assertEquals(saveState.toString(), saveState1.toString());
    }
}