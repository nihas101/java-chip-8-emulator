package de.nihas101.chip8.config;

import de.nihas101.chip8.utils.KeyConfiguration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ConfigureWindow extends Application {
    ConfigureController configureController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        /* Load root-node and get controller */
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("configureControls.fxml"));
        Pane root = loader.load();
        configureController = loader.getController();

        /* Create Scene */
        Scene scene = new Scene(root);
        primaryStage.setTitle("Configure controls");
        primaryStage.setScene(scene);

        primaryStage.showAndWait();
    }

    public static KeyConfiguration configureControls(){
        ConfigureWindow configureWindow = new ConfigureWindow();
        try { configureWindow.start(new Stage()); }
        catch (Exception e) { e.printStackTrace(); }

        return configureWindow.configureController.keyConfiguration;
    }
}
