package de.nihas101.chip8.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class KeyConfigurationManager {
    private static final String KEY_CONFIG_STANDARD_SAVE_LOCATION = "../config/controls.dat";

    public void save(KeyConfiguration keyConfiguration) {
        save(new File(KEY_CONFIG_STANDARD_SAVE_LOCATION), keyConfiguration);
    }

    public void save(File saveTo, KeyConfiguration keyConfiguration) {
        /* TODO: TEST */
        String data = "config{\n" + keyConfiguration.toString() + "}";

        try (FileWriter fileWriter = new FileWriter(saveTo)) {
            fileWriter.write(data);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public KeyConfiguration load() {
        /* TODO */
        return load(new File(KEY_CONFIG_STANDARD_SAVE_LOCATION));
    }

    public KeyConfiguration load(File loadFrom) {
        /* TODO */
        return null;
    }
}
