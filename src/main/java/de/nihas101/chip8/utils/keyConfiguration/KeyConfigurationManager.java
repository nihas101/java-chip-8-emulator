package de.nihas101.chip8.utils.keyConfiguration;

import de.nihas101.chip8.hardware.Emulator;

import java.io.*;

import static de.nihas101.chip8.utils.keyConfiguration.KeyConfiguration.createKeyConfiguration;

public class KeyConfigurationManager {
    private static final String KEY_CONFIG_STANDARD_SAVE_DIR = "../config";
    private static final String KEY_CONFIG_STANDARD_SAVE_LOCATION = "../config/controls.dat";

    public static void saveKeyConfiguration(KeyConfiguration keyConfiguration) {
        createSaveDirectory();
        saveKeyConfiguration(new File(KEY_CONFIG_STANDARD_SAVE_LOCATION), keyConfiguration);
    }

    private static void createSaveDirectory() {
        File saveDir = new File(KEY_CONFIG_STANDARD_SAVE_DIR);
        if (!saveDir.isDirectory()) saveDir.mkdir();
    }

    public static void saveKeyConfiguration(File saveTo, KeyConfiguration keyConfiguration) {
        String data = keyConfiguration.toString();

        try {
            saveTo.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        try (FileWriter fileWriter = new FileWriter(saveTo)) {
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static KeyConfiguration loadKeyConfiguration(Emulator emulator) {
        return loadKeyConfiguration(new File(KEY_CONFIG_STANDARD_SAVE_LOCATION), emulator);
    }

    public static KeyConfiguration loadKeyConfiguration(File loadFrom, Emulator emulator) {
        KeyConfiguration keyConfiguration = createKeyConfiguration(emulator);

        try {
            keyConfiguration = tryToLoadKeyConfiguration(loadFrom, emulator);
        } catch (IOException exception) {
            /* DO NOTHING AND CONTINUE WITH STANDARD KEY CONFIGURATION */
        }


        return keyConfiguration;
    }

    private static KeyConfiguration tryToLoadKeyConfiguration(File loadFrom, Emulator emulator) throws IOException {
        String readFile = readFile(loadFrom);
        return createKeyConfiguration(emulator, readFile);
    }

    private static String readFile(File loadFrom) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(loadFrom))) {
            String readLine = lineNumberReader.readLine();

            while (readLine != null) {
                stringBuilder.append(readLine).append("\n");
                readLine = lineNumberReader.readLine();
            }
        }

        return stringBuilder.toString().trim();
    }
}
