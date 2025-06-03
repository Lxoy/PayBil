package hr.java.payroll.controller;

import hr.java.payroll.database.ContractFullTimeDatabaseManager;
import hr.java.payroll.database.ContractPartTimeDatabaseManager;
import hr.java.payroll.database.EmployeeDatabaseManager;
import hr.java.payroll.entities.Contract;
import hr.java.payroll.entities.Employee;
import hr.java.payroll.enums.Gender;
import hr.java.payroll.enums.Role;
import hr.java.payroll.exception.InvalidEmailException;
import hr.java.payroll.exception.InvalidInputException;
import hr.java.payroll.exception.InvalidPasswordException;
import hr.java.payroll.exception.MissingRequiredFieldException;
import hr.java.payroll.utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Controller for the employee editing functionality in the Payroll Management System.
 * This class allows for the editing of employee details, including their first name, last name,
 * email, password, date of birth, gender, contract, and role.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class EmployeeEditController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeEditController.class);
    ContractPartTimeDatabaseManager contractPartTimeDatabaseManager = new ContractPartTimeDatabaseManager();
    ContractFullTimeDatabaseManager contractFullTimeDatabaseManager = new ContractFullTimeDatabaseManager();
    EmployeeDatabaseManager employeeDatabaseManager = new EmployeeDatabaseManager();

    @FXML private ComboBox<String> employeeEditAllEmployeesComboBox;
    @FXML private TextField employeeEditFirstNameTextField;
    @FXML private TextField employeeEditLastNameTextField;
    @FXML private TextField employeeEditEmailTextField;
    @FXML private TextField employeeEditPasswordTextField;
    @FXML private DatePicker employeeEditDateOfBirthDatePicker;
    @FXML private ComboBox<Gender> employeeEditGenderComboBox;
    @FXML private ComboBox<String> employeeEditContractComboBox;
    @FXML private ComboBox<Role> employeeEditRoleComboBox;

    /**
     * Initializes the employee editing form, populating the combo boxes with available employees, contracts, roles, and genders.
     */
    public void initialize() {
        employeeEditDateOfBirthDatePicker.setDisable(true);
        employeeEditGenderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        List<? extends Contract> contracts = Stream.concat(
                contractFullTimeDatabaseManager.getAllFullTimeContracts().stream(),
                contractPartTimeDatabaseManager.getAllPartTimeContracts().stream()
        ).toList();
        employeeEditContractComboBox.setItems(FXCollections.observableArrayList(contracts.stream().map(Contract::getName).toList()));
        employeeEditRoleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
        employeeEditAllEmployeesComboBox.setItems(
                FXCollections.observableArrayList(
                        employeeDatabaseManager.getEmployees((List<Contract>) contracts)
                                .stream()
                                .map(Employee::getEmail)
                                .toList()
                )
        );
        employeeEditAllEmployeesComboBox.setOnAction(event -> {
            clear();
            try{
                Validator.nullInput(employeeEditAllEmployeesComboBox.getValue(), "Select employee you want to edit");
                loadEmployeeDetails();
            } catch (MissingRequiredFieldException e) {
                Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", e.getMessage());
                log.warn("Missing required field: {}", e.getMessage());
            }
        });
    }

    /**
     * Loads the details of the selected employee into the form fields for editing.
     */
    private void loadEmployeeDetails() {
        employeeEditGenderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        List<? extends Contract> contracts = Stream.concat(
                contractPartTimeDatabaseManager.getAllPartTimeContracts().stream(),
                contractFullTimeDatabaseManager.getAllFullTimeContracts().stream()
        ).toList();
        Employee employee;
        try{
            employee = employeeDatabaseManager.getEmployees((List<Contract>) contracts)
                    .stream()
                    .filter(c -> c.getEmail().equals(employeeEditAllEmployeesComboBox.getValue()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        } catch (IllegalArgumentException e) {
            Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "", e.getMessage());
            log.warn("Error while fetching employee: {}", e.getMessage());
            return;
        }
        employeeEditDateOfBirthDatePicker.setDisable(false);
        employeeEditFirstNameTextField.setText(employee.getFirstName());
        employeeEditLastNameTextField.setText(employee.getLastName());
        employeeEditEmailTextField.setText(employee.getEmail());
        employeeEditDateOfBirthDatePicker.setValue(employee.getDateOfBirth());
        employeeEditGenderComboBox.setValue(employee.getGender());
        employeeEditContractComboBox.setValue(employee.getContract().getName());
        employeeEditRoleComboBox.setValue(employee.getRole());
    }

    /**
     * Handles the editing of employee details.
     * Validates the inputs, updates the employee information, and saves the changes to the database.
     */
    public void edit(){
        if (employeeEditAllEmployeesComboBox.getValue() == null) {
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", "Please select a employee to edit.");
            return;
        }
        if (!confirmEditAction()) return;

        List<? extends Contract> contracts = Stream.concat(
                contractFullTimeDatabaseManager.getAllFullTimeContracts().stream(),
                contractPartTimeDatabaseManager.getAllPartTimeContracts().stream()
        ).toList();
        Set<Employee> employees = employeeDatabaseManager.getEmployees((List<Contract>) contracts);
        try{
            Validator.stringInput(employeeEditFirstNameTextField.getText(), "Invalid first name input.");
            Validator.stringInput(employeeEditLastNameTextField.getText(), "Invalid last name input.");
            if(!employeeEditEmailTextField.getText().equals(employeeEditAllEmployeesComboBox.getValue())){
                Validator.emailValidator(employeeEditEmailTextField.getText(), employees, "Invalid email input.");
            }
            if(!employeeEditPasswordTextField.getText().isEmpty()){
                Validator.passwordValidator(employeeEditPasswordTextField.getText(), "Invalid password input.");
            }
            Validator.dateOfBirthValidator(employeeEditDateOfBirthDatePicker.getValue(), "Employee must be older than 16.");
            Validator.nullInput(employeeEditGenderComboBox.getValue(), "Invalid gender input.");
            Validator.nullInput(employeeEditRoleComboBox.getValue(), "Invalid role input.");
        } catch (MissingRequiredFieldException e) {
            Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", e.getMessage());
            log.warn("Missing required field: {}", e.getMessage());
            return;
        } catch (InvalidInputException | InvalidPasswordException | InvalidEmailException e){
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", e.getMessage());
            log.warn("Invalid input error: {}", e.getMessage());
            return;
        }
        Employee employee;
        try{
            employee = employees.stream().filter(c -> c.getEmail().equals(employeeEditAllEmployeesComboBox.getValue()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found!"));
        } catch (IllegalArgumentException e) {
            Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "", e.getMessage());
            log.warn("Error while fetching employee: {}", e.getMessage());
            return;
        }

        String oldFirstName = employee.getFirstName();
        String newFirstName = employeeEditFirstNameTextField.getText();
        String oldLastName = employee.getLastName();
        String newLastName = employeeEditLastNameTextField.getText();
        String oldEmail = employee.getEmail();
        String newEmail = employeeEditEmailTextField.getText();
        String oldPassword = employee.getPassword();
        String newPassword = employeeEditPasswordTextField.getText().isEmpty() ? employee.getPassword() : InputCheck.hashPassword(employeeEditPasswordTextField.getText());
        LocalDate oldDateOfBirth = employee.getDateOfBirth();
        LocalDate newDateOfBirth = employeeEditDateOfBirthDatePicker.getValue();
        Gender oldGender = employee.getGender();
        Gender newGender = employeeEditGenderComboBox.getValue();
        Role oldRole = employee.getRole();
        Role newRole = employeeEditRoleComboBox.getValue();

        Contract oldContract = employee.getContract();
        Contract newContract;
        try{
            newContract = contracts.stream().filter(c -> c.getName().equals(employeeEditContractComboBox.getValue()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Contract not found"));
        } catch (IllegalArgumentException e){
            Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "", e.getMessage());
            log.warn("Error while fetching contract: {}", e.getMessage());
            return;
        }

        Validator.isDataEdited("Employee First Name", oldFirstName, newFirstName);
        Validator.isDataEdited("Employee Last Name", oldLastName, newLastName);
        Validator.isDataEdited("Employee Email", oldEmail, newEmail);
        Validator.isDataEdited("Employee Password", oldPassword, newPassword);
        Validator.isDataEdited("Employee Date of Birth", oldDateOfBirth, newDateOfBirth);
        Validator.isDataEdited("Employee Contract", oldContract.getName(), newContract.getName());
        Validator.isDataEdited("Employee Gender", oldGender, newGender);
        Validator.isDataEdited("Employee Role", oldRole, newRole);
        Employee editedEmployee = new Employee.EmployeeBuilder().setId(employee.getId()).setFirstName(newFirstName).setLastName(newLastName).setEmail(newEmail).setPassword(newPassword).setContract(newContract).setDateOfBirth(newDateOfBirth).setGender(newGender).setRole(newRole).build();

        employeeDatabaseManager.editEmployee(editedEmployee);
        Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE,"", "Employee was updated successfully.");
        if(employeeEditAllEmployeesComboBox.getValue().equals(EmployeeSession.getInstance().getLoggedInEmployee().getEmail())){
            EmployeeSession.getInstance().login(editedEmployee);
        }
        SceneController.switchToScene("/hr/java/payroll/main/employeeList.fxml");
    }

    /**
     * Confirms the action of editing an employee, showing a confirmation alert.
     *
     * @return true if the user confirmed the action, false otherwise.
     */
    private boolean confirmEditAction() {
        Alert alert = Message.createAlert(Alert.AlertType.CONFIRMATION, "Confirm Employee Edit", "Edit Employee",
                "Are you sure you want to edit this employee? This action cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Clears all input fields in the employee edit form.
     */
    private void clear(){
        employeeEditFirstNameTextField.clear();
        employeeEditLastNameTextField.clear();
        employeeEditEmailTextField.clear();
        employeeEditPasswordTextField.clear();
        employeeEditDateOfBirthDatePicker.setValue(null);
        employeeEditDateOfBirthDatePicker.setPromptText("-");
        employeeEditContractComboBox.setValue(null);
        employeeEditContractComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "-" : item);
            }
        });
        employeeEditGenderComboBox.setValue(null);
        employeeEditGenderComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Gender item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "-" : item.toString());
            }
        });
        employeeEditRoleComboBox.setValue(null);
        employeeEditRoleComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Role item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "-" : item.toString());
            }
        });
    }
}
