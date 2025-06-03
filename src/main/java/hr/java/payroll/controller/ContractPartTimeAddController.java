package hr.java.payroll.controller;

import hr.java.payroll.database.ContractPartTimeDatabaseManager;
import hr.java.payroll.entities.ContractPartTime;
import hr.java.payroll.enums.Position;
import hr.java.payroll.exception.InvalidInputException;
import hr.java.payroll.exception.MissingRequiredFieldException;
import hr.java.payroll.utils.Message;
import hr.java.payroll.utils.Validator;
import hr.java.payroll.utils.Var;
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
 * Controller class for creating part-time contracts in the payroll system.
 * Provides functionality for validating input fields and creating new part-time contracts.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class ContractPartTimeAddController {
    private static final Logger log = LoggerFactory.getLogger(ContractPartTimeAddController.class);
    ContractPartTimeDatabaseManager contractPartTimeDatabaseManager = new ContractPartTimeDatabaseManager();

    @FXML private TextField contractPartTimeAddNameTextField;
    @FXML private ComboBox<Position> contractPartTimeAddPositionComboBox;
    @FXML private TextField contractPartTimeAddHoursWorkedTextField;
    @FXML private TextField contractPartTimeAddHourlyRateTextField;
    @FXML private DatePicker contractPartTimeAddStartDateDatePicker;
    @FXML private DatePicker contractPartTimeAddEndDateDatePicker;

    /**
     * Initializes the controller by setting up the combo box for positions.
     * This method is called when the controller is first loaded.
     */
    public void initialize() {
        contractPartTimeAddPositionComboBox.getItems().addAll(Position.values());
    }

    /**
     * Creates a new part-time contract based on the user's input in the form fields.
     * Validates the input, checks for missing or invalid fields, and saves the new contract to the database.
     *
     * If any errors occur during validation, appropriate messages are shown to the user.
     */
    public void create(){
        try {
            Validator.stringInput(contractPartTimeAddNameTextField.getText(),"Invalid name input.");
            Validator.nullInput(contractPartTimeAddPositionComboBox.getValue());
            Validator.hoursWorked(contractPartTimeAddHoursWorkedTextField.getText(),"Invalid hours worked input.");
            Validator.hourlyRate(contractPartTimeAddHourlyRateTextField.getText(),"Invalid hourly rate input.");
            Validator.nullInput(contractPartTimeAddStartDateDatePicker.getValue());
            Validator.nullInput(contractPartTimeAddEndDateDatePicker.getValue());
            Validator.contractDate(contractPartTimeAddStartDateDatePicker.getValue(), contractPartTimeAddEndDateDatePicker.getValue(), "Contract start date must be before contract end date.");
        } catch (MissingRequiredFieldException e) {
            Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", e.getMessage());
            log.info("Missing required field: {}", e.getMessage());
            return;
        } catch (InvalidInputException e){
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", e.getMessage());
            log.warn("Invalid input error while creating a contract: {}", e.getMessage());
            return;
        }

        String name = contractPartTimeAddNameTextField.getText().trim();
        Position position = contractPartTimeAddPositionComboBox.getValue();
        LocalDate startDate = LocalDate.parse(contractPartTimeAddStartDateDatePicker.getValue().toString());
        LocalDate endDate = LocalDate.parse(contractPartTimeAddEndDateDatePicker.getValue().toString());
        BigDecimal hoursWorked = new BigDecimal(contractPartTimeAddHoursWorkedTextField.getText().trim());
        BigDecimal hourlyRate = new BigDecimal(contractPartTimeAddHourlyRateTextField.getText().trim());

        contractPartTimeDatabaseManager.savePartTimeContract(new ContractPartTime.ContractPartTimeBuilder().setId(0L).setName(name).setPosition(position).setBaseSalary(hoursWorked, hourlyRate).setStartDate(startDate).setEndDate(endDate).setHoursWorked(hoursWorked).setHourlyRate(hourlyRate).build());
        Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE,"", "New contract was created");
        SceneController.switchToScene("/hr/java/payroll/main/contractPartTimeList.fxml");
    }
}
