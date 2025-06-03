package hr.java.payroll.main;
import hr.java.payroll.controller.SceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Main entry point for the Payroll application.
 * This class initializes and launches the JavaFX application, sets up the login scene, and handles application startup.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class PayrollApp extends Application {

    /**
     * Starts the JavaFX application by setting up the primary stage.
     * Loads the login screen, sets the window icon, and initializes the application window.
     *
     * @param stage the primary stage for the application
     * @throws IOException if loading the FXML file fails
     */
    @Override
    public void start(Stage stage) throws IOException {
        try{
            SceneController.setStage(stage);
            Image icon = new Image(getClass().getResourceAsStream("/hr/java/payroll/photos/icon.png"));
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
            Scene scene = new Scene(root, 1400, 700);
            stage.getIcons().add(icon);
            stage.setScene(scene);
            stage.setTitle("Paybil");
            stage.show();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Launches the JavaFX application.
     * This method is the entry point for the application and calls the {@link #start(Stage)} method.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        launch();
    }
}