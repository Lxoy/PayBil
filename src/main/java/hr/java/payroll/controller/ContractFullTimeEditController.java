package hr.java.payroll.controller;

import hr.java.payroll.database.ContractFullTimeDatabaseManager;
import hr.java.payroll.entities.ContractFullTime;
import hr.java.payroll.enums.Position;
import hr.java.payroll.exception.InvalidInputException;
import hr.java.payroll.exception.MissingRequiredFieldException;
import hr.java.payroll.utils.Message;
import hr.java.payroll.utils.Validator;
import hr.java.payroll.utils.Var;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Controller class responsible for editing an existing full-time contract in the Payroll Management System.
 * It provides functionality for loading contract details, input validation, and updating the contract in the database.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class ContractFullTimeEditController {
    private static final Logger log = LoggerFactory.getLogger(ContractFullTimeEditController.class);
    ContractFullTimeDatabaseManager contractFullTimeDatabaseManager = new ContractFullTimeDatabaseManager();

    @FXML private ComboBox<String> contractFullTimeEditAllContractsComboBox;
    @FXML private TextField contractFullTimeEditNameTextField;
    @FXML private ComboBox<Position> contractFullTimeEditPositionComboBox;
    @FXML private TextField contractFullTimeEditBaseSalaryTextField;
    @FXML private TextField contractFullTimeEditBonusTextField;
    @FXML private DatePicker contractFullTimeEditStartDateDatePicker;
    @FXML private DatePicker contractFullTimeEditEndDateDatePicker;

    /**
     * Initializes the combo boxes with contract names and positions.
     * Sets up event handling for selecting a contract to edit.
     */
    public void initialize() {
        contractFullTimeEditEndDateDatePicker.setDisable(true);
        contractFullTimeEditAllContractsComboBox.setItems(
                FXCollections.observableArrayList(
                        contractFullTimeDatabaseManager.getAllFullTimeContracts()
                                .stream()
                                .map(ContractFullTime::getName)
                                .toList()
                )
        );
        contractFullTimeEditPositionComboBox.setItems(FXCollections.observableArrayList(Position.values()));
        contractFullTimeEditAllContractsComboBox.setOnAction(event -> {
            try {
                Validator.nullInput(contractFullTimeEditAllContractsComboBox.getValue(), "Select contract you want to edit");
                loadContractDetails();
            } catch (MissingRequiredFieldException e) {
                Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", e.getMessage());
                log.error("Error: Contract selection is missing when trying to edit. Exception: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * Loads the details of the selected contract into the input fields for editing.
     */
    private void loadContractDetails() {
        ContractFullTime contractFullTime;
        try{
            contractFullTime = contractFullTimeDatabaseManager.getAllFullTimeContracts()
                    .stream()
                    .filter(c -> c.getName().equals(contractFullTimeEditAllContractsComboBox.getValue()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Contract not found"));
        } catch (IllegalArgumentException e) {
            Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "", e.getMessage());
            log.warn("Error while fetching contract: {}", e.getMessage());
            return;
        }

        contractFullTimeEditNameTextField.setText(contractFullTime.getName());
        contractFullTimeEditPositionComboBox.setValue(contractFullTime.getPosition());
        contractFullTimeEditBaseSalaryTextField.setText(contractFullTime.getBaseSalary().toString());
        contractFullTimeEditBonusTextField.setText(contractFullTime.getBonus().toString());
        contractFullTimeEditStartDateDatePicker.setValue(contractFullTime.getStartDate());
        contractFullTimeEditEndDateDatePicker.setValue(contractFullTime.getEndDate());
    }

    /**
     * Edits the selected contract with the updated details provided by the user.
     * Validates the input, checks if any data has been edited, and saves the updated contract.
     */
    public void edit(){
        if (contractFullTimeEditAllContractsComboBox.getValue() == null) {
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", "Please select a contract to edit.");
            return;
        }
        try{
            Validator.stringInput(contractFullTimeEditNameTextField.getText(), "Invalid name input.");
            Validator.nullInput(contractFullTimeEditPositionComboBox.getValue());
            Validator.baseSalary(contractFullTimeEditBaseSalaryTextField.getText(),"Invalid base salary input.");
            Validator.bonus(contractFullTimeEditBonusTextField.getText(),"Invalid bonus input.");
            Validator.nullInput(contractFullTimeEditStartDateDatePicker.getValue());
            contractFullTimeEditEndDateDatePicker.setValue((contractFullTimeEditStartDateDatePicker.getValue().plusYears(1)));
        } catch (MissingRequiredFieldException e) {
            Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", e.getMessage());
            log.info("Missing required field error while editing contract: {}", e.getMessage());
            return;
        } catch (InvalidInputException e){
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", e.getMessage());
            log.warn("Invalid input error while editing contract: {}", e.getMessage());
            return;
        }
        ContractFullTime contractFullTime;
        try{
            contractFullTime = contractFullTimeDatabaseManager.getAllFullTimeContracts()
                    .stream()
                    .filter(c -> c.getName().equals(contractFullTimeEditAllContractsComboBox.getValue()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Contract not found"));
        } catch (IllegalArgumentException e) {
            Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "", e.getMessage());
            log.warn("Error while fetching contract: {}", e.getMessage());
            return;
        }

        String oldName = contractFullTime.getName();
        String newName = contractFullTimeEditNameTextField.getText();

        Position oldPosition = contractFullTime.getPosition();
        Position newPosition = contractFullTimeEditPositionComboBox.getValue();
        BigDecimal oldBaseSalary = contractFullTime.getBaseSalary();
        BigDecimal newBaseSalary = new BigDecimal(contractFullTimeEditBaseSalaryTextField.getText());

        LocalDate oldStartDate = contractFullTime.getStartDate();
        LocalDate newStartDate = LocalDate.parse(contractFullTimeEditStartDateDatePicker.getValue().toString());

        LocalDate oldEndDate = contractFullTime.getEndDate();
        LocalDate newEndDate = LocalDate.parse(contractFullTimeEditEndDateDatePicker.getValue().toString());

        BigDecimal oldBonus = new BigDecimal(contractFullTime.getBonus().toString());
        BigDecimal newBonus = new BigDecimal(contractFullTimeEditBonusTextField.getText());

        Validator.isDataEdited("Full Time Contract Name", oldName, newName);
        Validator.isDataEdited("Full Time Contract Position", oldPosition, newPosition);
        Validator.isDataEdited("Full Time Contract Base Salary", oldBaseSalary, newBaseSalary);
        Validator.isDataEdited("Full Time Contract Bonus", oldBonus, newBonus);
        Validator.isDataEdited("Full Time Contract Start Date", oldStartDate, newStartDate);
        Validator.isDataEdited("Full Time Contract End Date", oldEndDate, newEndDate);

        contractFullTimeDatabaseManager.editFullTimeContract(new ContractFullTime.ContractFullTimeBuilder().setId(contractFullTime.getId()).setName(newName).setPosition(newPosition).setBaseSalary(newBaseSalary).setStartDate(newStartDate).setEndDate(newEndDate).setBonus(newBonus).build());
        Message.showAlert(Alert.AlertType.INFORMATION,Var.INFO_TITLE,"", "Contract was updated successfully.");
        SceneController.switchToScene("/hr/java/payroll/main/contractFullTimeList.fxml");
    }
}
