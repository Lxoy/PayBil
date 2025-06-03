package hr.java.payroll.controller;

import hr.java.payroll.database.ContractPartTimeDatabaseManager;
import hr.java.payroll.entities.ContractPartTime;
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
 * Controller for editing part-time contracts.
 * This class handles the UI interactions for updating contract details.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class ContractPartTimeEditController {
    private static final Logger log = LoggerFactory.getLogger(ContractPartTimeEditController.class);
    ContractPartTimeDatabaseManager contractPartTimeDatabaseManager = new ContractPartTimeDatabaseManager();

    @FXML
    private ComboBox<String> contractPartTimeEditAllContractsComboBox;
    @FXML private TextField contractPartTimeEditNameTextField;
    @FXML private ComboBox<Position> contractPartTimeEditPositionComboBox;
    @FXML private TextField contractPartTimeEditHoursWorkedTextField;
    @FXML private TextField contractPartTimeEditHourlyRateTextField;
    @FXML private DatePicker contractPartTimeEditStartDateDatePicker;
    @FXML private DatePicker contractPartTimeEditEndDateDatePicker;

    /**
     * Initializes the contract part-time editor.
     * Loads all available contracts and positions into ComboBoxes.
     */
    public void initialize() {
        contractPartTimeEditAllContractsComboBox.setItems(
                FXCollections.observableArrayList(
                        contractPartTimeDatabaseManager.getAllPartTimeContracts()
                                .stream()
                                .map(ContractPartTime::getName)
                                .toList()
                )
        );
        contractPartTimeEditPositionComboBox.setItems(FXCollections.observableArrayList(Position.values()));
        contractPartTimeEditAllContractsComboBox.setOnAction(event -> {
            try {
                Validator.nullInput(contractPartTimeEditAllContractsComboBox.getValue(), "Select contract you want to edit");
                loadContractDetails();
            } catch (MissingRequiredFieldException e) {
                Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", e.getMessage());
                log.error("Error: Contract selection is missing when trying to edit. Exception: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * Loads the details of the selected contract into the editor fields.
     */
    private void loadContractDetails() {
        ContractPartTime contractPartTime;
        try{
            contractPartTime = contractPartTimeDatabaseManager.getAllPartTimeContracts()
                    .stream()
                    .filter(c -> c.getName().equals(contractPartTimeEditAllContractsComboBox.getValue()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Contract not found"));
        } catch (IllegalArgumentException e) {
            Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "", e.getMessage());
            log.warn("Error while fetching contract: {}", e.getMessage());
            return;
        }

        contractPartTimeEditNameTextField.setText(contractPartTime.getName());
        contractPartTimeEditPositionComboBox.setValue(contractPartTime.getPosition());
        contractPartTimeEditHoursWorkedTextField.setText(contractPartTime.getHoursWorked().toString());
        contractPartTimeEditHourlyRateTextField.setText(contractPartTime.getHourlyRate().toString());
        contractPartTimeEditStartDateDatePicker.setValue(contractPartTime.getStartDate());
        contractPartTimeEditEndDateDatePicker.setValue(contractPartTime.getEndDate());
    }

    /**
     * Handles the editing of the selected contract.
     * Validates the input and updates the contract if valid.
     */
    public void edit(){
        if (contractPartTimeEditAllContractsComboBox.getValue() == null) {
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", "Please select a contract to edit.");
            return;
        }
        try{
            Validator.stringInput(contractPartTimeEditNameTextField.getText(),"Invalid name input.");
            Validator.nullInput(contractPartTimeEditPositionComboBox.getValue());
            Validator.hoursWorked(contractPartTimeEditHoursWorkedTextField.getText(),"Invalid hours worked input.");
            Validator.hourlyRate(contractPartTimeEditHourlyRateTextField.getText(),"Invalid hourly rate input.");
            Validator.nullInput(contractPartTimeEditStartDateDatePicker.getValue());
            Validator.nullInput(contractPartTimeEditEndDateDatePicker.getValue());
            Validator.contractDate(contractPartTimeEditStartDateDatePicker.getValue(), contractPartTimeEditEndDateDatePicker.getValue(), "Contract start date must be before contract end date.");
        } catch (MissingRequiredFieldException e) {
            Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", e.getMessage());
            log.info("Missing required field error while editing contract: {}", e.getMessage());
            return;
        } catch (InvalidInputException e){
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", e.getMessage());
            log.warn("Invalid input error while editing contract: {}", e.getMessage());
            return;
        }
        ContractPartTime contractPartTime;
        try{
            contractPartTime = contractPartTimeDatabaseManager.getAllPartTimeContracts()
                    .stream()
                    .filter(c -> c.getName().equals(contractPartTimeEditAllContractsComboBox.getValue()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Contract not found"));
        } catch (IllegalArgumentException e) {
            Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "", e.getMessage());
            log.warn("Error while fetching contract: {}", e.getMessage());
            return;
        }

        String oldName = contractPartTime.getName();
        String newName = contractPartTimeEditNameTextField.getText();

        Position oldPosition = contractPartTime.getPosition();
        Position newPosition = contractPartTimeEditPositionComboBox.getValue();

        LocalDate oldStartDate = contractPartTime.getStartDate();
        LocalDate newStartDate = LocalDate.parse(contractPartTimeEditStartDateDatePicker.getValue().toString());

        LocalDate oldEndDate = contractPartTime.getEndDate();
        LocalDate newEndDate = LocalDate.parse(contractPartTimeEditEndDateDatePicker.getValue().toString());

        BigDecimal oldHoursWorked = contractPartTime.getHoursWorked();
        BigDecimal newHoursWorked = new BigDecimal(contractPartTimeEditHoursWorkedTextField.getText());

        BigDecimal oldHourlyRate = contractPartTime.getHourlyRate();
        BigDecimal newHourlyRate = new BigDecimal(contractPartTimeEditHourlyRateTextField.getText());

        Validator.isDataEdited("Part Time Contract Name", oldName, newName);
        Validator.isDataEdited("Part Time Contract Position", oldPosition, newPosition);
        Validator.isDataEdited("Part Time Contract Start Date", oldStartDate, newStartDate);
        Validator.isDataEdited("Part Time Contract End Date", oldEndDate, newEndDate);
        Validator.isDataEdited("Part Time Contract Hours Worked", oldHoursWorked, newHoursWorked);
        Validator.isDataEdited("Part Time Contract Hourly Rate", oldHourlyRate, newHourlyRate);

        contractPartTimeDatabaseManager.editPartTimeContract(new ContractPartTime.ContractPartTimeBuilder().setId(contractPartTime.getId()).setName(newName).setPosition(newPosition).setBaseSalary(newHoursWorked, newHourlyRate).setStartDate(newStartDate).setEndDate(newEndDate).setHoursWorked(newHoursWorked).setHourlyRate(newHourlyRate).build());
        Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE,"", "Employee was updated successfully.");
        SceneController.switchToScene("/hr/java/payroll/main/contractPartTimeList.fxml");
    }
}
