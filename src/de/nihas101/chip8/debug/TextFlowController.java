package de.nihas101.chip8.debug;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TextFlowController {
    @FXML
    public TextFlow debugTextFlow;
    private Text debugText;

    public void addText(Text debugText){
        this.debugText = debugText;
        debugTextFlow.getChildren().add(debugText);
    }

    public void setDebugText(String debugInfo){
        debugText.setText(debugInfo);
    }
}
