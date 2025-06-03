package hr.java.payroll.controller;

import hr.java.payroll.database.ContractFullTimeDatabaseManager;
import hr.java.payroll.database.ContractPartTimeDatabaseManager;
import hr.java.payroll.database.EmployeeDatabaseManager;
import hr.java.payroll.entities.Contract;
import hr.java.payroll.entities.ContractFullTime;
import hr.java.payroll.entities.ContractPartTime;
import hr.java.payroll.entities.Employee;
import hr.java.payroll.enums.Gender;
import hr.java.payroll.enums.Role;
import hr.java.payroll.exception.InvalidEmailException;
import hr.java.payroll.exception.InvalidInputException;
import hr.java.payroll.exception.InvalidPasswordException;
import hr.java.payroll.exception.MissingRequiredFieldException;
import hr.java.payroll.utils.InputCheck;
import hr.java.payroll.utils.Message;
import hr.java.payroll.utils.Validator;
import hr.java.payroll.utils.Var;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Controller for adding a new employee.
 * This controller handles input validation, contract selection, and saving the new employee to the database.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class EmployeeAddController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeAddController.class);
    ContractPartTimeDatabaseManager contractPartTimeDatabaseManager = new ContractPartTimeDatabaseManager();
    ContractFullTimeDatabaseManager contractFullTimeDatabaseManager = new ContractFullTimeDatabaseManager();
    EmployeeDatabaseManager employeeDatabaseManager = new EmployeeDatabaseManager();

    @FXML private TextField employeeAddFirstNameTextField;
    @FXML private TextField employeeAddLastNameTextField;
    @FXML private TextField employeeAddEmailTextField;
    @FXML private TextField employeeAddPasswordTextField;
    @FXML private DatePicker employeeAddDateOfBirthDatePicker;
    @FXML private ComboBox<Gender> employeeAddGenderComboBox;
    @FXML private ComboBox<String> employeeAddContractComboBox;
    @FXML private ComboBox<Role> employeeAddRoleComboBox;

    /**
     * Initializes the controller by setting the gender, contract, and role options.
     * Populates the contract combo box with available contracts and role combo box with available roles.
     */
    public void initialize() {
        employeeAddGenderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        List<? extends Contract> contracts = Stream.concat(
                contractPartTimeDatabaseManager.getAllPartTimeContracts().stream(),
                contractFullTimeDatabaseManager.getAllFullTimeContracts().stream()
        ).toList();
        employeeAddContractComboBox.setItems(FXCollections.observableArrayList(contracts.stream().map(Contract::getName).toList()));
        employeeAddRoleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
    }

    /**
     * Creates a new employee with the data provided in the input fields.
     * Validates the inputs and handles any errors. If validation is successful, the employee is saved in the database.
     */
    public void create(){
        List<Contract> contracts = new ArrayList<>();
        Optional.ofNullable(contractFullTimeDatabaseManager.getAllFullTimeContracts()).ifPresent(contracts::addAll);
        Optional.ofNullable(contractPartTimeDatabaseManager.getAllPartTimeContracts()).ifPresent(contracts::addAll);
        try {
            Validator.stringInput(employeeAddFirstNameTextField.getText(), "Invalid first name input.");
            Validator.stringInput(employeeAddLastNameTextField.getText(), "Invalid last name input.");
            Validator.emailValidator(employeeAddEmailTextField.getText(), employeeDatabaseManager.getEmployees(contracts), "Invalid email input");
            Validator.passwordValidator(employeeAddPasswordTextField.getText(), "Invalid password input");
            Validator.dateOfBirthValidator(employeeAddDateOfBirthDatePicker.getValue(), "Employee must be older than 16.");
            Validator.nullInput(employeeAddGenderComboBox.getValue());
            Validator.nullInput(employeeAddContractComboBox.getValue());
            Validator.nullInput(employeeAddRoleComboBox.getValue());
        } catch (MissingRequiredFieldException e) {
            Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", e.getMessage());
            log.error("Missing required field: {}", e.getMessage());
            return;
        } catch (InvalidInputException | InvalidEmailException | InvalidPasswordException e){
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", e.getMessage());
            log.warn("Validation error: {}", e.getMessage());
            return;
        }

        String firstName = employeeAddFirstNameTextField.getText();
        String lastName = employeeAddLastNameTextField.getText();
        String email = employeeAddEmailTextField.getText();
        String password = employeeAddPasswordTextField.getText();
        LocalDate dateOfBirth = employeeAddDateOfBirthDatePicker.getValue();
        Gender gender = employeeAddGenderComboBox.getValue();
        Role role = employeeAddRoleComboBox.getValue();
        Contract contract;
        try{
            contract = contracts.stream()
                    .filter(c -> c.getName().equals(employeeAddContractComboBox.getValue()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("There is no contract with this name!"));
            if(contract instanceof ContractPartTime contractPartTime){
                employeeDatabaseManager.saveEmployee(new Employee.EmployeeBuilder().setId(0L).setFirstName(firstName).setLastName(lastName).setEmail(email).setPassword(InputCheck.hashPassword(password)).setDateOfBirth(dateOfBirth).setGender(gender).setContract(contractPartTime).setRole(role).build());

            }
            else if(contract instanceof ContractFullTime contractFullTime){
                employeeDatabaseManager.saveEmployee(new Employee.EmployeeBuilder().setId(0L).setFirstName(firstName).setLastName(lastName).setEmail(email).setPassword(InputCheck.hashPassword(password)).setDateOfBirth(dateOfBirth).setGender(gender).setContract(contractFullTime).setRole(role).build());

            }
        } catch (IllegalArgumentException e){
            Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "", e.getMessage());
            log.warn("Error while fetching contract: {}", e.getMessage());
            return;
        }
        Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE,"", "New employee was created");
        SceneController.switchToScene("/hr/java/payroll/main/employeeList.fxml");
    }
}
