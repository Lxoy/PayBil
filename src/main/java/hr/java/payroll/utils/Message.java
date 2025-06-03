package hr.java.payroll.utils;

import javafx.scene.control.Alert;

/**
 * Utility class for displaying message alerts in the application.
 * Provides methods for showing alerts with various types and contents.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class Message {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Message(){}

    /**
     * Displays an alert with the specified alert type, title, header, and content.
     *
     * @param alertType the type of the alert (e.g., INFORMATION, WARNING, ERROR)
     * @param title the title of the alert window
     * @param header the header text of the alert
     * @param content the content text of the alert
     */
    public static void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = createAlert(alertType, title, header, content);
        alert.showAndWait();
    }

    /**
     * Creates an alert with the specified alert type, title, header, and content.
     * This method is useful for customizing the alert before displaying it.
     *
     * @param alertType the type of the alert (e.g., INFORMATION, WARNING, ERROR)
     * @param title the title of the alert window
     * @param header the header text of the alert
     * @param content the content text of the alert
     * @return the created alert object
     */
    public static Alert createAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }
}
