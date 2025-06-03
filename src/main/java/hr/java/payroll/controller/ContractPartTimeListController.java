package hr.java.payroll.controller;

import hr.java.payroll.database.ContractPartTimeDatabaseManager;
import hr.java.payroll.entities.ContractPartTime;
import hr.java.payroll.enums.Position;
import hr.java.payroll.exception.DatabaseException;
import hr.java.payroll.exception.InvalidInputException;
import hr.java.payroll.utils.InputCheck;
import hr.java.payroll.utils.Message;
import hr.java.payroll.utils.Validator;
import hr.java.payroll.utils.Var;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing the list of part-time contracts.
 * Handles searching, clearing, and deleting contracts in the database.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class ContractPartTimeListController {
    private static final Logger log = LoggerFactory.getLogger(ContractPartTimeListController.class);
    ContractPartTimeDatabaseManager contractPartTimeDatabaseManager = new ContractPartTimeDatabaseManager();

    @FXML private TableView<ContractPartTime> contractPartTimeListTableView;
    @FXML private TableColumn<ContractPartTime, String> contractPartTimeListIdTableColumn;
    @FXML private TableColumn<ContractPartTime, String> contractPartTimeListNameTableColumn;
    @FXML private TableColumn<ContractPartTime, String> contractPartTimeListPositionTableColumn;
    @FXML private TableColumn<ContractPartTime, String> contractPartTimeListBaseSalaryTableColumn;
    @FXML private TableColumn<ContractPartTime, String> contractPartTimeListHoursWorkedTableColumn;
    @FXML private TableColumn<ContractPartTime, String> contractPartTimeListHourlyRateTableColumn;
    @FXML private TableColumn<ContractPartTime, String> contractPartTimeListStartDateTableColumn;
    @FXML private TableColumn<ContractPartTime, String> contractPartTimeListEndDateTableColumn;

    @FXML private TextField contractPartTimeListNameTextField;
    @FXML private ComboBox<Position> contractPartTimeListPositionComboBox;
    @FXML private TextField contractPartTimeListBaseSalaryTextField;
    @FXML private TextField contractPartTimeListHoursWorkedTextField;
    @FXML private TextField contractPartTimeListHourlyRateTextField;
    @FXML private DatePicker contractPartTimeListStartDatePicker;
    @FXML private DatePicker contractPartTimeListEndDatePicker;

    /**
     * Initializes the ContractPartTimeListController by setting up the table view and combo box.
     * The table columns are populated with data from the ContractPartTime database.
     */
    public void initialize() {
        contractPartTimeListPositionComboBox.setItems(FXCollections.observableArrayList(Position.values()));

        contractPartTimeListIdTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        contractPartTimeListNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        contractPartTimeListPositionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPosition().name()));
        contractPartTimeListBaseSalaryTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBaseSalary().toString()));
        contractPartTimeListHoursWorkedTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHoursWorked().toString()));
        contractPartTimeListHourlyRateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHourlyRate().toString()));
        contractPartTimeListStartDateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(Validator.changeDateFormatDDMMYYYY(cellData.getValue().getStartDate())));
        contractPartTimeListEndDateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(Validator.changeDateFormatDDMMYYYY(cellData.getValue().getEndDate())));
        search();
    }

    /**
     * Searches for part-time contracts based on the user's input.
     * Filters contracts by name, position, base salary, hours worked, hourly rate, start date, and end date.
     */
    public void search(){
        try {
            InputCheck.stringCheck(contractPartTimeListNameTextField.getText(), "Invalid name input.");
            InputCheck.bigDecimalCheck(contractPartTimeListBaseSalaryTextField.getText(), "Invalid base salary input.");
            InputCheck.bigDecimalCheck(contractPartTimeListHoursWorkedTextField.getText(), "Invalid hours worked input.");
            InputCheck.bigDecimalCheck(contractPartTimeListHourlyRateTextField.getText(), "Invalid hourly rate input.");
        } catch (InvalidInputException e) {
            Message.showAlert(Alert.AlertType.WARNING,  Var.WARNING_TITLE, "", e.getMessage());
            log.warn("Input validation failed. Error: {}", e.getMessage(), e);
            return;
        }

        String name = contractPartTimeListNameTextField.getText().trim().toLowerCase();
        Position position = contractPartTimeListPositionComboBox.getValue();
        BigDecimal baseSalary = contractPartTimeListBaseSalaryTextField.getText().isEmpty() ? null : new BigDecimal(contractPartTimeListBaseSalaryTextField.getText().trim());
        BigDecimal hoursWorked = contractPartTimeListHoursWorkedTextField.getText().isEmpty() ? null : new BigDecimal(contractPartTimeListHoursWorkedTextField.getText());
        BigDecimal hourlyRate = contractPartTimeListHourlyRateTextField.getText().isEmpty() ? null : new BigDecimal(contractPartTimeListHourlyRateTextField.getText());
        LocalDate startDate = contractPartTimeListStartDatePicker.getValue();
        LocalDate endDate = contractPartTimeListEndDatePicker.getValue();

        List<ContractPartTime> partTimeContracts = contractPartTimeDatabaseManager.getAllPartTimeContracts().stream()
                .filter(contractPartTime -> name.isEmpty() || contractPartTime.getName().toLowerCase().contains(name))
                .filter(contractPartTime -> position == null || contractPartTime.getPosition().equals(position))
                .filter(contractPartTime -> baseSalary == null || contractPartTime.getBaseSalary().compareTo(baseSalary) == 0)
                .filter(contractPartTime -> hoursWorked == null || contractPartTime.getHoursWorked().compareTo(hoursWorked) == 0)
                .filter(contractPartTime -> hourlyRate == null || contractPartTime.getHourlyRate().compareTo(hourlyRate) == 0)
                .filter(contractPartTime -> startDate == null || !contractPartTime.getStartDate().isBefore(startDate))
                .filter(contractPartTime -> endDate == null || !contractPartTime.getEndDate().isAfter(endDate))
                .toList();

        ObservableList<ContractPartTime> observableList = FXCollections.observableArrayList(partTimeContracts);
        contractPartTimeListTableView.setItems(observableList);
    }

    /**
     * Clears all input fields and resets the search criteria to default.
     */
    public void clear(){
        contractPartTimeListNameTextField.clear();
        contractPartTimeListPositionComboBox.setValue(null);
        contractPartTimeListPositionComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Position item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "Position" : item.toString());
            }
        });
        contractPartTimeListBaseSalaryTextField.clear();
        contractPartTimeListHoursWorkedTextField.clear();
        contractPartTimeListHourlyRateTextField.clear();
        contractPartTimeListStartDatePicker.setValue(null);
        contractPartTimeListStartDatePicker.setPromptText("Start Date");
        contractPartTimeListEndDatePicker.setValue(null);
        contractPartTimeListEndDatePicker.setPromptText("End Date");
        search();
    }

    /**
     * Deletes the selected part-time contract from the list and the database.
     * A confirmation dialog is shown before the contract is deleted.
     */
    public void delete(){
        ContractPartTime selectedContract = contractPartTimeListTableView.getSelectionModel().getSelectedItem();

        if (selectedContract == null) {
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "No contract selected", "Please select a contract to delete.");
            return;
        }

        Alert alert = Message.createAlert(Alert.AlertType.CONFIRMATION, Var.CONFIRM_TITLE, "Delete Contract", "Are you sure you want to delete the selected contract? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                contractPartTimeDatabaseManager.deleteContract(selectedContract);
                search();
            } catch (DatabaseException e) {
                Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "Contract Deletion Failed", e.getMessage());
                log.error("Failed to delete contract. Contract id: {}. Error: {}", selectedContract.getId(), e.getMessage(), e);
            }
        }
    }
}
