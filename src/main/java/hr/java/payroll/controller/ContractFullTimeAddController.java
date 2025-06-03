package hr.java.payroll.controller;

import hr.java.payroll.database.ContractFullTimeDatabaseManager;
import hr.java.payroll.entities.ContractFullTime;
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
 * Controller class responsible for adding a new full-time contract in the Payroll Management System.
 * It provides functionality for input validation, contract creation, and saving the contract to the database.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class ContractFullTimeAddController {
    private static final Logger log = LoggerFactory.getLogger(ContractFullTimeAddController.class);
    ContractFullTimeDatabaseManager contractFullTimeDatabaseManager = new ContractFullTimeDatabaseManager();

    @FXML private TextField contractFullTimeAddNameTextField;
    @FXML private ComboBox<Position> contractFullTimeAddPositionComboBox;
    @FXML private TextField contractFullTimeAddBaseSalaryTextField;
    @FXML private TextField contractFullTimeAddBonusTextField;
    @FXML private DatePicker contractFullTimeAddStartDateDatePicker;
    @FXML private DatePicker contractFullTimeAddEndDateDatePicker;

    /**
     * Initializes the position combo box with all available positions.
     */
    public void initialize() {
        contractFullTimeAddPositionComboBox.getItems().addAll(Position.values());
        contractFullTimeAddEndDateDatePicker.setDisable(true);
    }

    /**
     * Creates a new full-time contract based on the user inputs.
     * Validates the input fields and handles any missing or invalid input by showing appropriate alerts.
     * If all inputs are valid, the new contract is saved to the database, and the user is informed of the successful creation.
     */
    public void create(){
        try {
            Validator.stringInput(contractFullTimeAddNameTextField.getText(), "Invalid name input.");
            Validator.nullInput(contractFullTimeAddPositionComboBox.getValue());
            Validator.baseSalary(contractFullTimeAddBaseSalaryTextField.getText(),"Invalid base salary input.");
            Validator.bonus(contractFullTimeAddBonusTextField.getText(),"Invalid bonus input.");
            Validator.nullInput(contractFullTimeAddStartDateDatePicker.getValue());
            contractFullTimeAddEndDateDatePicker.setValue((contractFullTimeAddStartDateDatePicker.getValue().plusYears(1)));
        } catch (MissingRequiredFieldException e) {
            Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", e.getMessage());
            log.info("Missing required field: {}", e.getMessage());
            return;
        } catch (InvalidInputException e){
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", e.getMessage());
            log.warn("Invalid input error while creating a contract: {}", e.getMessage());
            return;
        }

        String name = contractFullTimeAddNameTextField.getText();
        Position position = contractFullTimeAddPositionComboBox.getValue();
        BigDecimal baseSalary = new BigDecimal(contractFullTimeAddBaseSalaryTextField.getText());
        LocalDate startDate = LocalDate.parse(contractFullTimeAddStartDateDatePicker.getValue().toString());
        LocalDate endDate = LocalDate.parse(contractFullTimeAddEndDateDatePicker.getValue().toString());
        BigDecimal bonus = new BigDecimal(contractFullTimeAddBonusTextField.getText());
        contractFullTimeDatabaseManager.saveFullTimeContract(new ContractFullTime.ContractFullTimeBuilder().setId(0L).setName(name).setPosition(position).setBaseSalary(baseSalary).setStartDate(startDate).setEndDate(endDate).setBonus(bonus).build());
        Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE,"", "New contract was created");
        SceneController.switchToScene("/hr/java/payroll/main/contractFullTimeList.fxml");
    }
}
