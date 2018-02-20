package de.nihas101.chip8.config;

import de.nihas101.chip8.hardware.Emulator;
import de.nihas101.chip8.hardware.keys.EmulatorKey;
import de.nihas101.chip8.utils.KeyConfiguration;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
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
    public TextField[] textFields;

    private Emulator emulator;
    private HashMap<KeyCode, EmulatorKey> keyHashMap;
    private boolean resetInProgress = false;

    public void resetKeyConfiguration(ActionEvent actionEvent) {
        resetInProgress = true;
        key0TextField.setText("X");
        key1TextField.setText("1");
        key2TextField.setText("2");
        key3TextField.setText("3");
        key4TextField.setText("Q");
        key5TextField.setText("W");
        key6TextField.setText("E");
        key7TextField.setText("A");
        key8TextField.setText("S");
        key9TextField.setText("D");
        keyATextField.setText("Z");
        keyBTextField.setText("C");
        keyCTextField.setText("4");
        keyDTextField.setText("R");
        keyETextField.setText("F");
        keyFTextField.setText("V");
        resetInProgress = false;
        actionEvent.consume();
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

    private void insertIntoKeyHashMap(String keyCodeString, String keyName, int emulatorKeyCode) {
        keyHashMap.put(getKeyCode(keyCodeString), createEmulatorKey(keyName, () -> emulator.getCentralProcessingUnit().setKeyCode(emulatorKeyCode)));
    }

    public void setup(Emulator emulator) {
        this.emulator = emulator;
        Set<Map.Entry<KeyCode, EmulatorKey>> entries = emulator.getKeyConfiguration().entrySet();
        setupTextFields(entries);
    }

    private void setupTextFields(Set<Map.Entry<KeyCode, EmulatorKey>> entries) {
        textFields = new TextField[]{
                key0TextField, key1TextField, key2TextField, key3TextField, key4TextField, key5TextField,
                key6TextField, key7TextField, key8TextField, key9TextField, keyATextField, keyBTextField,
                keyCTextField, keyDTextField, keyETextField, keyFTextField
        };
        entries.forEach((entry) -> setupTextField(entry.getValue(), entry.getKey()));
        setupTextFieldInput();
    }

    private void setupTextField(EmulatorKey emulatorKey, KeyCode key) {
        if (emulatorKey == null || key == null) return;

        switch (emulatorKey.getKeyName()) {
            case "0":
                key0TextField.setText(key.getName());
                break;
            case "1":
                key1TextField.setText(key.getName());
                break;
            case "2":
                key2TextField.setText(key.getName());
                break;
            case "3":
                key3TextField.setText(key.getName());
                break;
            case "4":
                key4TextField.setText(key.getName());
                break;
            case "5":
                key5TextField.setText(key.getName());
                break;
            case "6":
                key6TextField.setText(key.getName());
                break;
            case "7":
                key7TextField.setText(key.getName());
                break;
            case "8":
                key8TextField.setText(key.getName());
                break;
            case "9":
                key9TextField.setText(key.getName());
                break;
            case "A":
                keyATextField.setText(key.getName());
                break;
            case "B":
                keyBTextField.setText(key.getName());
                break;
            case "C":
                keyCTextField.setText(key.getName());
                break;
            case "D":
                keyDTextField.setText(key.getName());
                break;
            case "E":
                keyETextField.setText(key.getName());
                break;
            case "F":
                keyFTextField.setText(key.getName());
                break;
        }
    }

    private void setupTextFieldInput() {
        for (TextField textField : textFields) textField.setTextFormatter(createSingleCharFormatter(textField));
    }

    private TextFormatter<ListChangeListener.Change> createSingleCharFormatter(TextField keyTextField) {
        return new TextFormatter<>(change -> {
            if (change.getText().length() > 0) return handleChange(keyTextField, change);
            else return noChange(change);
        });
    }

    private Change handleChange(TextField keyTextField, Change change) {
        String assignment = String.valueOf(change.getText().charAt(0)).toUpperCase();
        if (alreadyAssigned(assignment)) {
            change.setText("");
            return change;
        }

        if (change.getControlNewText().length() > 1 && change.isAdded()) {
            keyTextField.setText(assignment);
            change.setText("");
        } else change.setText(change.getText().toUpperCase());

        return change;
    }

    private Change noChange(Change change) {
        change.setText("");
        return change;
    }

    private boolean alreadyAssigned(String text) {
        if (text == null || resetInProgress) return false;

        int assignments = 0;
        for (TextField textField : textFields)
            if (text.equals(textField.getText())) assignments++;

        return assignments > 0;
    }
}
