package hr.java.payroll.controller;

import hr.java.payroll.database.ContractFullTimeDatabaseManager;
import hr.java.payroll.entities.ContractFullTime;
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
 * Controller class for managing full-time contracts in the payroll system.
 * Provides functionality for displaying, searching, editing, and deleting contracts.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class ContractFullTimeListController {
    private static final Logger log = LoggerFactory.getLogger(ContractFullTimeListController.class);
    ContractFullTimeDatabaseManager contractFullTimeDatabaseManager = new ContractFullTimeDatabaseManager();

    @FXML private TableView<ContractFullTime> contractFullTimeListTableView;
    @FXML private TableColumn<ContractFullTime, String> contractFullTimeListIdTableColumn;
    @FXML private TableColumn<ContractFullTime, String> contractFullTimeListNameTableColumn;
    @FXML private TableColumn<ContractFullTime, String> contractFullTimeListPositionTableColumn;
    @FXML private TableColumn<ContractFullTime, String> contractFullTimeListBaseSalaryTableColumn;
    @FXML private TableColumn<ContractFullTime, String> contractFullTimeListBonusTableColumn;
    @FXML private TableColumn<ContractFullTime, String> contractFullTimeListStartDateTableColumn;
    @FXML private TableColumn<ContractFullTime, String> contractFullTimeListEndDateTableColumn;

    @FXML private TextField contractFullTimeListNameTextField;
    @FXML private ComboBox<Position> contractFullTimeListPositionComboBox;
    @FXML private TextField contractFullTimeListBaseSalaryTextField;
    @FXML private TextField contractFullTimeListBonusTextField;
    @FXML private DatePicker contractFullTimeListStartDatePicker;
    @FXML private DatePicker contractFullTimeListEndDatePicker;

    /**
     * Initializes the controller by setting up table columns and populating the combo box for positions.
     * Sets up the search functionality and binds table columns to contract data.
     */
    public void initialize() {
        contractFullTimeListPositionComboBox.setItems(FXCollections.observableArrayList(Position.values()));

        contractFullTimeListIdTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        contractFullTimeListNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        contractFullTimeListPositionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPosition().name()));
        contractFullTimeListBaseSalaryTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBaseSalary().toString()));
        contractFullTimeListBonusTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBonus().toString()));
        contractFullTimeListStartDateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(Validator.changeDateFormatDDMMYYYY(cellData.getValue().getStartDate())));
        contractFullTimeListEndDateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(Validator.changeDateFormatDDMMYYYY(cellData.getValue().getEndDate())));
        search();
    }

    /**
     * Searches for full-time contracts based on the input filters such as name, position, salary, and dates.
     * Filters are applied if provided in the corresponding input fields.
     */
    public void search() {
        try {
            InputCheck.stringCheck(contractFullTimeListNameTextField.getText(), "Invalid name input.");
            InputCheck.bigDecimalCheck(contractFullTimeListBaseSalaryTextField.getText(), "Invalid base salary input.");
            InputCheck.bigDecimalCheck(contractFullTimeListBonusTextField.getText(), "Invalid bonus input.");
        } catch (InvalidInputException e) {
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", e.getMessage());
            log.warn("Input validation failed. Error: {}", e.getMessage(), e);
            return;
        }
        String name = contractFullTimeListNameTextField.getText().toLowerCase();
        Position position = contractFullTimeListPositionComboBox.getValue();
        BigDecimal baseSalary = contractFullTimeListBaseSalaryTextField.getText().isEmpty() ? null : new BigDecimal(contractFullTimeListBaseSalaryTextField.getText().trim());
        BigDecimal bonus = contractFullTimeListBonusTextField.getText().isEmpty() ? null : new BigDecimal(contractFullTimeListBonusTextField.getText().trim());
        LocalDate startDate = contractFullTimeListStartDatePicker.getValue();
        LocalDate endDate = contractFullTimeListEndDatePicker.getValue();

        List<ContractFullTime> fullTimeContracts = contractFullTimeDatabaseManager.getAllFullTimeContracts().stream()
                .filter(contractFullTime -> name.isEmpty() || contractFullTime.getName().toLowerCase().contains(name))
                .filter(contractFullTime -> position == null || contractFullTime.getPosition().name().equals(position.name()))
                .filter(contractFullTime -> baseSalary == null || contractFullTime.getBaseSalary().compareTo(baseSalary) == 0)
                .filter(contractFullTime -> bonus == null || contractFullTime.getBonus().compareTo(bonus) == 0)
                .filter(contractFullTime -> startDate == null || !contractFullTime.getStartDate().isBefore(startDate))
                .filter(contractFullTime -> endDate == null || !contractFullTime.getEndDate().isAfter(endDate))
                .toList();

        ObservableList<ContractFullTime> observableList = FXCollections.observableArrayList(fullTimeContracts);
        contractFullTimeListTableView.setItems(observableList);
    }

    /**
     * Clears the search filters and refreshes the contract list.
     */
    public void clear(){
        contractFullTimeListNameTextField.clear();
        contractFullTimeListPositionComboBox.setValue(null);
        contractFullTimeListPositionComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Position item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "Position" : item.toString());
            }
        });
        contractFullTimeListBaseSalaryTextField.clear();
        contractFullTimeListBonusTextField.clear();
        contractFullTimeListStartDatePicker.setValue(null);
        contractFullTimeListStartDatePicker.setPromptText("Start Date");
        contractFullTimeListEndDatePicker.setValue(null);
        contractFullTimeListEndDatePicker.setPromptText("End Date");
        search();
    }

    /**
     * Deletes the selected contract from the list.
     * Prompts the user with a confirmation dialog before proceeding with the deletion.
     */
    public void delete() {
        ContractFullTime selectedContract = contractFullTimeListTableView.getSelectionModel().getSelectedItem();

        if (selectedContract == null) {
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "No contract selected", "Please select a contract to delete.");
            return;
        }

        Alert alert = Message.createAlert(Alert.AlertType.CONFIRMATION, Var.CONFIRM_TITLE, "Delete Contract",
                "Are you sure you want to delete the selected contract? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                contractFullTimeDatabaseManager.deleteContract(selectedContract);
                search();
            } catch (DatabaseException e) {
                Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "Contract Deletion Failed", e.getMessage());
                log.error("Failed to delete contract. Contract id: {}. Error: {}", selectedContract.getId(), e.getMessage(), e);
            }
        }
    }

}
