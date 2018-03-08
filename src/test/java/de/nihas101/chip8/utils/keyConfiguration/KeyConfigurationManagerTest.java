package de.nihas101.chip8.utils.keyConfiguration;

import de.nihas101.chip8.hardware.Emulator;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import static de.nihas101.chip8.hardware.Emulator.createEmulator;
import static de.nihas101.chip8.utils.keyConfiguration.KeyConfiguration.createKeyConfiguration;
import static javafx.scene.input.KeyCode.*;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class KeyConfigurationManagerTest {

    @Test
    public void saveKeyConfiguration() throws IOException {
        Emulator emulator = createEmulator();
        KeyConfiguration keyConfiguration = createKeyConfiguration(emulator);
        File file = new File("src/test/resources/keyConfigTestTemp.dat");
        file.deleteOnExit();
        KeyConfigurationManager.saveKeyConfiguration(file, keyConfiguration);

        assertEquals(true, file.exists());

        String readFile = readFile(file);
        assertEquals(keyConfiguration.toString(), readFile);
    }

    @Test
    public void loadKeyConfiguration() {
        Emulator emulator = createEmulator();
        File file = new File("src/test/resources/keyConfigTest.dat");
        KeyConfiguration keyConfiguration = KeyConfigurationManager.loadKeyConfiguration(file, emulator);

        assertEquals("1", keyConfiguration.getOrNOP(DIGIT1).toString());
        assertEquals("2", keyConfiguration.getOrNOP(DIGIT2).toString());
        assertEquals("3", keyConfiguration.getOrNOP(DIGIT3).toString());
        assertEquals("C", keyConfiguration.getOrNOP(DIGIT4).toString());

        assertEquals("4", keyConfiguration.getOrNOP(Q).toString());
        assertEquals("5", keyConfiguration.getOrNOP(W).toString());
        assertEquals("6", keyConfiguration.getOrNOP(E).toString());
        assertEquals("D", keyConfiguration.getOrNOP(R).toString());

        assertEquals("7", keyConfiguration.getOrNOP(A).toString());
        assertEquals("8", keyConfiguration.getOrNOP(S).toString());
        assertEquals("9", keyConfiguration.getOrNOP(D).toString());
        assertEquals("E", keyConfiguration.getOrNOP(F).toString());

        assertEquals("A", keyConfiguration.getOrNOP(Z).toString());
        assertEquals("0", keyConfiguration.getOrNOP(X).toString());
        assertEquals("B", keyConfiguration.getOrNOP(C).toString());
        assertEquals("F", keyConfiguration.getOrNOP(V).toString());

        assertEquals("NOP", keyConfiguration.getOrNOP(NUMPAD0).toString());
    }

    @Test
    public void loadKeyConfigurationFileError() {
        Emulator emulator = createEmulator();
        File file = new File("src/test/resources/doesNotExist");

        KeyConfiguration keyConfiguration = KeyConfigurationManager.loadKeyConfiguration(file, emulator);

        assertEquals(KeyConfiguration.createKeyConfiguration(emulator).toString(), keyConfiguration.toString());
    }

    @Test
    public void saveKeyConfigurationFileError() {
        Emulator emulator = createEmulator();
        KeyConfiguration keyConfiguration = createKeyConfiguration(emulator);
        emulator.setKeyConfiguration(keyConfiguration);

        File file = new File("src/test/resources/doesNotExist/config.dat");

        try {
            KeyConfigurationManager.saveKeyConfiguration(file, emulator.getKeyConfiguration());
        } catch (IOException exception) {
            return;
        }

        fail("IOException was not thrown");
    }

    private String readFile(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file))) {
            String readLine = lineNumberReader.readLine();

            while (readLine != null) {
                stringBuilder.append(readLine).append("\n");
                readLine = lineNumberReader.readLine();
            }

            lineNumberReader.close();
        }


        return stringBuilder.toString().trim();
    }
}