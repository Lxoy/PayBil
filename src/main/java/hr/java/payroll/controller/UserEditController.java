package hr.java.payroll.controller;

import hr.java.payroll.database.ContractFullTimeDatabaseManager;
import hr.java.payroll.database.ContractPartTimeDatabaseManager;
import hr.java.payroll.database.EmployeeDatabaseManager;
import hr.java.payroll.entities.Contract;
import hr.java.payroll.entities.Employee;
import hr.java.payroll.enums.Gender;
import hr.java.payroll.exception.InvalidEmailException;
import hr.java.payroll.exception.InvalidInputException;
import hr.java.payroll.exception.InvalidPasswordException;
import hr.java.payroll.exception.MissingRequiredFieldException;
import hr.java.payroll.utils.*;
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
 * Controller class responsible for handling the editing of user information (employee details).
 * It allows the user to update their first name, last name, email, password, date of birth, and gender.
 * Validations are applied before committing changes.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class UserEditController {
    private static final Logger log = LoggerFactory.getLogger(UserEditController.class);
    ContractFullTimeDatabaseManager contractFullTimeDatabaseManager = new ContractFullTimeDatabaseManager();
    ContractPartTimeDatabaseManager contractPartTimeDatabaseManager = new ContractPartTimeDatabaseManager();
    EmployeeDatabaseManager employeeDatabaseManager = new EmployeeDatabaseManager();

    @FXML private TextField userEditFirstNameTextField;
    @FXML private TextField userEditLastNameTextField;
    @FXML private TextField userEditEmailTextField;
    @FXML private TextField userEditPasswordTextField;
    @FXML private DatePicker userEditDateOfBirthDatePicker;
    @FXML private ComboBox<Gender> userEditGenderComboBox;

    /**
     * Initializes the form with the current user's information (from the session).
     * This method pre-fills the text fields and combo box with the logged-in user's data.
     */
    public void initialize() {
        userEditGenderComboBox.getItems().addAll(Gender.values());
        userEditFirstNameTextField.setText(EmployeeSession.getInstance().getLoggedInEmployee().getFirstName());
        userEditLastNameTextField.setText(EmployeeSession.getInstance().getLoggedInEmployee().getLastName());
        userEditEmailTextField.setText(EmployeeSession.getInstance().getLoggedInEmployee().getEmail());
        userEditDateOfBirthDatePicker.setValue(EmployeeSession.getInstance().getLoggedInEmployee().getDateOfBirth());
        userEditGenderComboBox.setValue(EmployeeSession.getInstance().getLoggedInEmployee().getGender());
    }

    /**
     * Confirms and applies changes made to the user's information.
     * This method validates the input fields and updates the employee record if the input is valid.
     * A confirmation alert is shown before saving the changes.
     */
    public void edit(){
        Alert alert = Message.createAlert(Alert.AlertType.CONFIRMATION, "Confirm Employee Edit", "Edit Employee",
                "Are you sure you want to edit this employee? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            List<? extends Contract> contracts = Stream.concat(
                    contractFullTimeDatabaseManager.getAllFullTimeContracts().stream(),
                    contractPartTimeDatabaseManager.getAllPartTimeContracts().stream()
            ).toList();
            Set<Employee> employees = employeeDatabaseManager.getEmployees((List<Contract>) contracts);
            try{
                Validator.stringInput(userEditFirstNameTextField.getText(), "Invalid first name input.");
                Validator.stringInput(userEditLastNameTextField.getText(), "Invalid last name input.");
                if(!userEditEmailTextField.getText().equals(EmployeeSession.getInstance().getLoggedInEmployee().getEmail())){
                    Validator.emailValidator(userEditEmailTextField.getText(), employees, "Invalid email input.");
                }
                if(!userEditPasswordTextField.getText().isEmpty()){
                    Validator.passwordValidator(userEditPasswordTextField.getText(), "Invalid password input.");
                }
                Validator.dateOfBirthValidator(userEditDateOfBirthDatePicker.getValue(), "Employee must be older than 16.");
                Validator.nullInput(userEditGenderComboBox.getValue(), "Invalid gender input.");
            } catch (MissingRequiredFieldException e) {
                Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", e.getMessage());
                log.warn("Missing required field: {}", e.getMessage(), e);
                return;
            } catch (InvalidInputException | InvalidPasswordException | InvalidEmailException e){
                Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", e.getMessage());
                log.warn("Validation failed: {}", e.getMessage(), e);
                return;
            }

            String oldFirstName = EmployeeSession.getInstance().getLoggedInEmployee().getFirstName();
            String newFirstName = userEditFirstNameTextField.getText();
            String oldLastName = EmployeeSession.getInstance().getLoggedInEmployee().getLastName();
            String newLastName = userEditLastNameTextField.getText();
            String oldEmail = EmployeeSession.getInstance().getLoggedInEmployee().getEmail();
            String newEmail = userEditEmailTextField.getText();
            String oldPassword = EmployeeSession.getInstance().getLoggedInEmployee().getPassword();
            String newPassword = userEditPasswordTextField.getText().isEmpty() ? EmployeeSession.getInstance().getLoggedInEmployee().getPassword() : InputCheck.hashPassword(userEditPasswordTextField.getText());
            LocalDate oldDateOfBirth = EmployeeSession.getInstance().getLoggedInEmployee().getDateOfBirth();
            LocalDate newDateOfBirth = userEditDateOfBirthDatePicker.getValue();
            Gender oldGender = EmployeeSession.getInstance().getLoggedInEmployee().getGender();
            Gender newGender = userEditGenderComboBox.getValue();

            Validator.isDataEdited("Employee First Name", oldFirstName, newFirstName);
            Validator.isDataEdited("Employee Last Name", oldLastName, newLastName);
            Validator.isDataEdited("Employee Email", oldEmail, newEmail);
            Validator.isDataEdited("Employee Password", oldPassword, newPassword);
            Validator.isDataEdited("Employee Date of Birth", oldDateOfBirth, newDateOfBirth);
            Validator.isDataEdited("Employee Gender", oldGender, newGender);
            Employee editedEmployee = new Employee.EmployeeBuilder().setId(EmployeeSession.getInstance().getLoggedInEmployee().getId()).setFirstName(newFirstName).setLastName(newLastName).setEmail(newEmail).setPassword(newPassword).setContract(EmployeeSession.getInstance().getLoggedInEmployee().getContract()).setDateOfBirth(newDateOfBirth).setGender(newGender).setRole(EmployeeSession.getInstance().getLoggedInEmployee().getRole()).build();
            employeeDatabaseManager.editEmployee(editedEmployee);
            Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE,"", "Employee was updated successfully.");
            EmployeeSession.getInstance().login(editedEmployee);
            SceneController.switchToScene("/hr/java/payroll/main/userView.fxml");
        }
    }

    /**
     * Cancels the edit operation and returns to the user view scene.
     */
    public void cancel(){
        SceneController.switchToScene("/hr/java/payroll/main/userView.fxml");
    }
}
