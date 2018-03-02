package de.nihas101.chip8.utils;

import de.nihas101.chip8.hardware.Emulator;

import java.io.*;

import static de.nihas101.chip8.utils.KeyConfiguration.createKeyConfiguration;

public class KeyConfigurationManager {
    private static final String KEY_CONFIG_STANDARD_SAVE_LOCATION = "../config/controls.dat";

    public static void saveKeyConfiguration(KeyConfiguration keyConfiguration) {
        saveKeyConfiguration(new File(KEY_CONFIG_STANDARD_SAVE_LOCATION), keyConfiguration);
    }

    public static void saveKeyConfiguration(File saveTo, KeyConfiguration keyConfiguration) {
        /* TODO: TEST */
        String data = "config{\n" + keyConfiguration.toString() + "}";

        try (FileWriter fileWriter = new FileWriter(saveTo)) {
            fileWriter.write(data);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static KeyConfiguration loadKeyConfiguration(Emulator emulator) {
        /* TODO */
        return loadKeyConfiguration(emulator, new File(KEY_CONFIG_STANDARD_SAVE_LOCATION));
    }

    public static KeyConfiguration loadKeyConfiguration(Emulator emulator, File loadFrom) {
        KeyConfiguration keyConfiguration = createKeyConfiguration(emulator);

        try{
            keyConfiguration = tryToLoadKeyConfiguration(emulator, loadFrom);
        }catch(IOException exception){
            /* DO NOTHING AND CONTINUE WITH STANDARD KEY CONFIGURATION */
        }


        return keyConfiguration;
    }

    private static KeyConfiguration tryToLoadKeyConfiguration(Emulator emulator, File loadFrom) throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(loadFrom));
        StringBuilder stringBuilder = new StringBuilder();
        String readLine = "";
        while(readLine != null){
            readLine = lineNumberReader.readLine();
        }
        /* TODO: Read file and give string to KeyConfiguration */
    }
}
