package hr.java.payroll.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller class responsible for switching between scenes in the application.
 * This class manages the loading of FXML files, scene transitions, and applying fade-in effects.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class SceneController {
    private static Stage stage;
    private static final Logger log = LoggerFactory.getLogger(SceneController.class);

    /**
     * Private constructor to prevent instantiation
     */
    private SceneController() {}

    /**
     * Sets the primary stage for the application.
     * This method is used to initialize the stage before switching scenes.
     *
     * @param primaryStage The primary stage of the application.
     */
    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    /**
     * Switches to the specified scene by loading the corresponding FXML file.
     * This method also applies a fade-in transition to the new scene.
     *
     * @param fxmlFile The name of the FXML file to load and display as a new scene.
     */
    public static void switchToScene(String fxmlFile) {
        if (stage == null) {
            log.error("Stage has not been set before switching scenes. Cannot switch to scene '{}'.", fxmlFile);
            return;
        }
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneController.class.getResource(fxmlFile)));
            Scene scene = new Scene(root, 1400, 700);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.25), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            stage.setScene(scene);
            stage.show();
        } catch (NullPointerException | IOException e) {
            log.error("Error occurred while loading the scene '{}'. Exception: {}", fxmlFile, e.getClass().getSimpleName(), e);
        }
    }
}
