package de.nihas101.chip8.config;

import de.nihas101.chip8.utils.KeyConfiguration;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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
    public KeyConfiguration keyConfiguration;

    public void resetKeyConfiguration(ActionEvent actionEvent) {
        /* TODO */
        actionEvent.consume();
    }
}
