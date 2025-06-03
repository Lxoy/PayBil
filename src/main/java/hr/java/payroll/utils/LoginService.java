package hr.java.payroll.utils;

import hr.java.payroll.controller.SceneController;
import hr.java.payroll.entities.Employee;
import hr.java.payroll.entities.generics.Pair;
import hr.java.payroll.enums.Role;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Utility class for handling the login authentication process.
 * Provides methods for verifying employee credentials and managing the login flow.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class LoginService {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private LoginService() {}

    /**
     * Authenticates the login attempt by verifying the entered email and password.
     * If the credentials match the admin account or an employee's credentials, the login is successful.
     * Based on the role of the logged-in user, the appropriate menu is displayed.
     *
     * @param enteredEmail the email entered by the user
     * @param enteredPassword the password entered by the user
     * @param employeesFromTxtFile the list of employee credentials from the text file
     * @param employeesFromDatabase the set of employees from the database
     * @return true if the login is successful, false otherwise
     */
    public static boolean loginAuthentication(String enteredEmail, String enteredPassword, List<String> employeesFromTxtFile, Set<Employee> employeesFromDatabase) {
        if (enteredEmail.equals(Var.EMPLOYEE_ADMIN_USERNAME) && enteredPassword.equals(Var.EMPLOYEE_ADMIN_PASSWORD)) {
            EmployeeSession.getInstance().login(Var.ADMIN);
            SceneController.switchToScene("/hr/java/payroll/main/adminMenu.fxml");
            return true;
        }

        Optional<Pair<String, String>> employeeCredentials = findEmployeeCredentials(employeesFromTxtFile, enteredEmail);

        if (employeeCredentials.isPresent() && BCrypt.checkpw(enteredPassword, employeeCredentials.get().getSecond())) {
            Employee employee = employeesFromDatabase.stream()
                    .filter(e -> e.getEmail().equals(employeeCredentials.get().getFirst()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            EmployeeSession.getInstance().login(employee);
            loginScreenBypass(employeesFromDatabase, employee.getEmail());
            return true;
        }

        return false;
    }

    /**
     * Finds the employee credentials from the list of employees.
     * The list should contain pairs of email and password.
     *
     * @param employees the list of employee credentials (email:password)
     * @param enteredEmail the email entered by the user
     * @return an Optional containing the employee credentials if found, otherwise an empty Optional
     */
    private static Optional<Pair<String, String>> findEmployeeCredentials(List<String> employees, String enteredEmail) {
        return employees.stream()
                .map(employee -> employee.split(":"))
                .filter(parts -> parts.length == 2 && parts[0].equals(enteredEmail))
                .map(parts -> new Pair<>(parts[0], parts[1]))
                .findFirst();
    }

    /**
     * Bypasses the login screen and redirects the user to the appropriate menu based on their role.
     * If the user is an admin, they are directed to the admin menu. Otherwise, they are directed to the user view.
     *
     * @param employees the set of employees from the database
     * @param enteredEmail the email of the logged-in employee
     */
    private static void loginScreenBypass(Set<Employee> employees, String enteredEmail) {
        String scene = employees.stream().filter(employee -> employee.getEmail().equals(enteredEmail)).anyMatch(employee -> employee.getRole() == Role.ADMIN)
                ? "/hr/java/payroll/main/adminMenu.fxml"
                : "/hr/java/payroll/main/userView.fxml";

        SceneController.switchToScene(scene);
    }
}
