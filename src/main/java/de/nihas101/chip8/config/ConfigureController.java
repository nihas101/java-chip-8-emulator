package de.nihas101.chip8.config;

import de.nihas101.chip8.hardware.Emulator;
import de.nihas101.chip8.hardware.keys.EmulatorKey;
import de.nihas101.chip8.utils.KeyConfiguration;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.nihas101.chip8.hardware.keys.EmulatorKey.createEmulatorKey;
import static de.nihas101.chip8.utils.Constants.*;
import static de.nihas101.chip8.utils.KeyConfiguration.createKeyConfiguration;
import static javafx.scene.input.KeyCode.getKeyCode;

public class ConfigureController {
    public Button resetButton;
    public TextField keyFTextField;
    public TextField keyETextField;
    public TextField keyDTextField;
    public TextField keyCTextField;
    public TextField keyBTextField;
    public TextField keyATextField;
    public TextField key9TextField;
    public TextField key8TextField;
    public TextField key7TextField;
    public TextField key6TextField;
    public TextField key5TextField;
    public TextField key4TextField;
    public TextField key3TextField;
    public TextField key2TextField;
    public TextField key1TextField;
    public TextField key0TextField;
    private Emulator emulator;
    private HashMap<KeyCode, EmulatorKey> keyHashMap;

    public void resetKeyConfiguration(ActionEvent actionEvent) {
        key0TextField.setText("N");
        key1TextField.setText("Q");
        key2TextField.setText("W");
        key3TextField.setText("E");
        key4TextField.setText("A");
        key5TextField.setText("S");
        key6TextField.setText("D");
        key7TextField.setText("Z");
        key8TextField.setText("X");
        key9TextField.setText("C");
        keyATextField.setText("B");
        keyBTextField.setText("M");
        keyCTextField.setText("R");
        keyDTextField.setText("F");
        keyETextField.setText("V");
        keyFTextField.setText("COMMA");
        actionEvent.consume();

        /* TODO: Set text field to only hold one char! */
        /* TODO: Think about special characters */
    }

    public KeyConfiguration getKeyConfiguration() {
        keyHashMap = new HashMap<>();

        insertIntoKeyHashMap(key0TextField.getText(), "0", KEY_0);
        insertIntoKeyHashMap(key1TextField.getText(), "1", KEY_1);
        insertIntoKeyHashMap(key2TextField.getText(), "2", KEY_2);
        insertIntoKeyHashMap(key3TextField.getText(), "3", KEY_3);
        insertIntoKeyHashMap(key4TextField.getText(), "4", KEY_4);
        insertIntoKeyHashMap(key5TextField.getText(), "5", KEY_5);
        insertIntoKeyHashMap(key6TextField.getText(), "6", KEY_6);
        insertIntoKeyHashMap(key7TextField.getText(), "7", KEY_7);
        insertIntoKeyHashMap(key8TextField.getText(), "8", KEY_8);
        insertIntoKeyHashMap(key9TextField.getText(), "9", KEY_9);
        insertIntoKeyHashMap(keyATextField.getText(), "A", KEY_A);
        insertIntoKeyHashMap(keyBTextField.getText(), "B", KEY_B);
        insertIntoKeyHashMap(keyCTextField.getText(), "C", KEY_C);
        insertIntoKeyHashMap(keyDTextField.getText(), "D", KEY_D);
        insertIntoKeyHashMap(keyETextField.getText(), "E", KEY_E);
        insertIntoKeyHashMap(keyFTextField.getText(), "F", KEY_F);

        return createKeyConfiguration(keyHashMap);
    }

    private void insertIntoKeyHashMap(String keyCodeString, String keyName, int emulatorKeyCode){
        keyHashMap.put(getKeyCode(keyCodeString), createEmulatorKey(keyName, () -> emulator.getCentralProcessingUnit().setKeyCode(emulatorKeyCode)));
    }

    public void setup(Emulator emulator) {
        this.emulator = emulator;
        Set<Map.Entry<KeyCode, EmulatorKey>> entries = emulator.getKeyConfiguration().entrySet();
        setupTextFields(entries);
    }

    private void setupTextFields(Set<Map.Entry<KeyCode, EmulatorKey>> entries) {
        entries.forEach((entry) -> setupTextField(entry.getValue(), entry.getKey()));
    }

    private void setupTextField(EmulatorKey emulatorKey, KeyCode key) {
        switch (emulatorKey.getKeyName()){
            case "0": key0TextField.setText(key.getName()); break;
            case "1": key1TextField.setText(key.getName()); break;
            case "2": key2TextField.setText(key.getName()); break;
            case "3": key3TextField.setText(key.getName()); break;
            case "4": key4TextField.setText(key.getName()); break;
            case "5": key5TextField.setText(key.getName()); break;
            case "6": key6TextField.setText(key.getName()); break;
            case "7": key7TextField.setText(key.getName()); break;
            case "8": key8TextField.setText(key.getName()); break;
            case "9": key9TextField.setText(key.getName()); break;
            case "A": keyATextField.setText(key.getName()); break;
            case "B": keyBTextField.setText(key.getName()); break;
            case "C": keyCTextField.setText(key.getName()); break;
            case "D": keyDTextField.setText(key.getName()); break;
            case "E": keyETextField.setText(key.getName()); break;
            case "F": keyFTextField.setText(key.getName()); break;
        }
    }
}
