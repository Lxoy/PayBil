package hr.java.payroll.controller;

import hr.java.payroll.entities.serializer.ChangeLog;
import hr.java.payroll.entities.serializer.ChangeLogSerializer;
import hr.java.payroll.enums.Role;
import hr.java.payroll.exception.InvalidInputException;
import hr.java.payroll.utils.InputCheck;
import hr.java.payroll.utils.Message;
import hr.java.payroll.utils.Var;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller class for managing and displaying the changes history in the Payroll Management System.
 * This class allows users to search and filter through changes logs by different criteria such as
 * field changed, role, old value, and new value. It also provides functionality to clear search fields.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class ChangesHistoryController {
    private static final Logger log = LoggerFactory.getLogger(ChangesHistoryController.class);

    @FXML private TableView<ChangeLog> changesHistoryTableView;
    @FXML private TableColumn<ChangeLog, String> cheangesHistoryFieldChangedTableColumn;
    @FXML private TableColumn<ChangeLog, String> cheangesHistoryOldValueTableColumn;
    @FXML private TableColumn<ChangeLog, String> cheangesHistoryNewValueTableColumn;
    @FXML private TableColumn<ChangeLog, String> cheangesHistoryRoleTableColumn;
    @FXML private TableColumn<ChangeLog, String> cheangesHistoryChangeDateTableColumn;

    @FXML private TextField cheangesHistoryFieldChangedTextField;
    @FXML private ComboBox<Role> cheangesHistoryRoleComboBox;
    @FXML private TextField cheangesHistoryOldValueTextField;
    @FXML private TextField cheangesHistoryNewValueTextField;

    /**
     * Initializes the changes history view by setting up the table columns and loading available roles
     * in the combo box. It also triggers the search function to populate the table with existing change logs.
     */
    public void initialize() {
        cheangesHistoryRoleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
        cheangesHistoryFieldChangedTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFieldChanged()));
        cheangesHistoryOldValueTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOldValue()));
        cheangesHistoryNewValueTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNewValue()));
        cheangesHistoryRoleTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        cheangesHistoryChangeDateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedDateTime()));
        search();
    }

    /**
     * Searches through the change logs based on the input criteria (field changed, role, old value, new value).
     * If any input is invalid, it displays a warning alert and logs the issue.
     */
    public void search(){
        try {
            InputCheck.stringCheck(cheangesHistoryFieldChangedTextField.getText(), "Invalid field changed input.");
        } catch (InvalidInputException e) {
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", e.getMessage());
            log.warn("Invalid input for 'fieldChanged' field: {}", e.getMessage());
            return;
        }

        String fieldChanged = cheangesHistoryFieldChangedTextField.getText().trim().toLowerCase();
        Role role = cheangesHistoryRoleComboBox.getValue();
        String oldValue = cheangesHistoryOldValueTextField.getText().trim().toLowerCase();
        String newValue = cheangesHistoryNewValueTextField.getText().trim().toLowerCase();

        List<ChangeLog> changeLogList = ChangeLogSerializer.deserializeChanges().stream()
                .filter(changeLog -> fieldChanged.isEmpty() || changeLog.getFieldChanged().toLowerCase().contains(fieldChanged))
                .filter(changeLog -> role == null || changeLog.getRole().equals(role.name()))
                .filter(changeLog -> oldValue.isEmpty() || changeLog.getOldValue().toLowerCase().contains(oldValue))
                .filter(changeLog -> newValue.isEmpty() || changeLog.getNewValue().toLowerCase().contains(newValue))
                .toList();

        ObservableList<ChangeLog> observableList = FXCollections.observableArrayList(changeLogList.reversed());
        changesHistoryTableView.setItems(observableList);
    }

    /**
     * Clears all search input fields and triggers a new search to refresh the displayed data.
     */
    public void clear(){
        cheangesHistoryFieldChangedTextField.clear();
        cheangesHistoryRoleComboBox.setValue(null);
        cheangesHistoryRoleComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Role item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "Role" : item.toString());
            }
        });
        cheangesHistoryOldValueTextField.clear();
        cheangesHistoryNewValueTextField.clear();
        search();
    }
}
