package hr.java.payroll.controller;

import hr.java.payroll.database.ContractFullTimeDatabaseManager;
import hr.java.payroll.database.ContractPartTimeDatabaseManager;
import hr.java.payroll.database.EmployeeDatabaseManager;
import hr.java.payroll.entities.Contract;
import hr.java.payroll.entities.Employee;
import hr.java.payroll.exception.InvalidEmailException;
import hr.java.payroll.exception.MissingRequiredFieldException;
import hr.java.payroll.utils.*;
import hr.java.payroll.entities.generics.repository.EmployeeRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Controller class for handling the login functionality in the Payroll Management System.
 * It performs validation of email and password input, checks the credentials against the database,
 * and handles login authentication.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final EmployeeRepository<Employee> employeeRepository = new EmployeeRepository<>();
    private final EmployeeDatabaseManager employeeDatabaseManager = new EmployeeDatabaseManager();
    private final ContractPartTimeDatabaseManager contractPartTimeDatabaseManager = new ContractPartTimeDatabaseManager();
    private  final ContractFullTimeDatabaseManager contractFullTimeDatabaseManager = new ContractFullTimeDatabaseManager();

    @FXML private TextField emailTextField;
    @FXML private PasswordField passwordPasswordField;

    /**
     * Validates the user's email and password input and performs login authentication.
     * If authentication is successful, the user is logged in. If not, an error message is displayed.
     */
    public void login() {
        String email = emailTextField.getText().trim();
        String password = passwordPasswordField.getText().trim();

        try {
            List<? extends Contract> contracts = Stream.concat(
                    contractFullTimeDatabaseManager.getAllFullTimeContracts().stream(),
                    contractPartTimeDatabaseManager.getAllPartTimeContracts().stream()
            ).toList();
            Set<Employee> employeesFromDatabase = employeeDatabaseManager.getEmployees((List<Contract>) contracts);
            employeeRepository.save(employeeDatabaseManager.getEmployees((List<Contract>) contracts));
            List<String> employeesFromTxtFile = employeeRepository.acquire();

            InputCheck.emailCheck(email, employeesFromDatabase, "Email is required!");
            Validator.nullInput(password, "Password is required!");

            boolean isLoginSuccessful = LoginService.loginAuthentication(email, password, employeesFromTxtFile, employeesFromDatabase);

            if (!isLoginSuccessful) {
                Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "", "Incorrect email or password!");
            }

        } catch (MissingRequiredFieldException | InvalidEmailException e) {
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "", e.getMessage());
            log.warn("Error occurred while login: {}", e.getMessage(), e);
        }
    }
}
