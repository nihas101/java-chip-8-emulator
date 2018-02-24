package de.nihas101.chip8.savestates;

import java.io.*;
import java.util.logging.Logger;

import static de.nihas101.chip8.savestates.SaveState.createSaveState;

public class SaveStateHandler {
    private Logger logger = Logger.getLogger(SaveStateHandler.class.getName());

    public void writeState(File saveFile, SaveState state) throws IOException {
        if (!saveFile.exists()) saveFile.createNewFile();
        if (saveFile.canWrite()) {
            try (FileWriter fileWriter = new FileWriter(saveFile)) {
                fileWriter.write(state.toString());
                fileWriter.close();
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    public SaveState readState(File loadFile) throws FailedReadingStateException {
        String readState;

        if (loadFile.exists()) {
            try (LineNumberReader fileReader = new LineNumberReader(new FileReader(loadFile))) {
                readState = readFile(fileReader);
            } catch (IOException e) {
                logger.severe(e.getMessage());
                throw new FailedReadingStateException(loadFile);
            }
        } else throw new FailedReadingStateException(loadFile);

        return createSaveState(readState);
    }

    private String readFile(LineNumberReader fileReader) throws IOException {
        StringBuilder readState = new StringBuilder();
        String readLine = fileReader.readLine();

        while (readLine != null) {
            readState.append(readLine).append("\n");
            readLine = fileReader.readLine();
        }

        return readState.toString();
    }
}
