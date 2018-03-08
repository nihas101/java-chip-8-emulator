package de.nihas101.chip8.savestates;

import de.nihas101.chip8.hardware.Emulator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static de.nihas101.chip8.hardware.Emulator.createEmulator;
import static de.nihas101.chip8.savestates.SaveState.createSaveState;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class SaveStateHandlerTest {

    @Test
    public void readWriteState() throws IOException, FailedReadingStateException {
        SaveState saveState = createTestSaveState();
        SaveStateHandler saveStateHandler = new SaveStateHandler();
        File file = createSaveFile();

        saveStateHandler.writeState(file, saveState);
        SaveState saveState1 = saveStateHandler.readState(file);

        assertEquals(saveState.toString(), saveState1.toString());
    }

    private SaveState createTestSaveState() {
        Emulator emulator = createEmulator();
        return createSaveState(emulator.getCentralProcessingUnit());
    }

    private File createSaveFile() throws IOException {
        File file = new File("src/test/resources/savestateTest.c8s");
        file.createNewFile();
        file.deleteOnExit();

        return file;
    }

    @Test
    public void readStateFileError() {
        File file = new File("src/test/resources/doesNotExist");
        SaveStateHandler saveStateHandler = new SaveStateHandler();
        try {
            saveStateHandler.readState(file);
        } catch (FailedReadingStateException exception) {
            return;
        }

        fail("FailedReadingStateException was not thrown");
    }
}