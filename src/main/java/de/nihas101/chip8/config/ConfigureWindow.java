package de.nihas101.chip8.config;

import de.nihas101.chip8.hardware.Emulator;
import de.nihas101.chip8.utils.KeyConfiguration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ConfigureWindow extends Application {
    private final Emulator emulator;
    private ConfigureController configureController;

    private ConfigureWindow(Emulator emulator) {
        this.emulator = emulator;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        /* Load root-node and get controller */
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("configureControls.fxml"));
        Pane root = loader.load();
        configureController = loader.getController();
        configureController.setup(emulator);

        /* Create Scene */
        Scene scene = new Scene(root);
        primaryStage.setTitle("Configure controls");
        primaryStage.setScene(scene);

        primaryStage.showAndWait();
    }

    public static KeyConfiguration configureControls(Emulator emulator) {
        ConfigureWindow configureWindow = new ConfigureWindow(emulator);
        try {
            configureWindow.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return configureWindow.configureController.getKeyConfiguration();
    }
}
