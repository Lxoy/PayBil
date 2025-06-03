package hr.java.payroll.controller;

import hr.java.payroll.database.PayrollDatabaseManager;
import hr.java.payroll.entities.Payslip;
import hr.java.payroll.utils.EmployeeSession;
import hr.java.payroll.utils.Var;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Comparator;
import java.util.Optional;

/**
 * Controller class responsible for displaying the user view of an employee.
 * It shows the employee's name, last salary, and contract period. It also provides options to edit data and sign out.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class UserViewController {
    PayrollDatabaseManager payrollDatabaseManager = new PayrollDatabaseManager();
    @FXML private Label userViewHi;
    @FXML private Label userViewSalary;
    @FXML private Label userViewContract;

    /**
     * Initializes the user view by populating labels with the logged-in employee's information.
     * Displays the employee's name, last salary, and contract period.
     * If no employee is logged in, placeholder text is shown.
     */
    public void initialize() {
        if (EmployeeSession.getInstance().getLoggedInEmployee() == null) {
            userViewHi.setText("No employee logged in");
            userViewSalary.setText("Last salary: /");
            userViewContract.setText("Contract period: /");
            return;
        }

        userViewHi.setText("Hello " + EmployeeSession.getInstance().getLoggedInEmployee().getFirstName() + " " + EmployeeSession.getInstance().getLoggedInEmployee().getLastName() + "!");

        Long loggedInEmployeeId = EmployeeSession.getInstance().getLoggedInEmployee().getId();

        Optional<Payslip> lastPayslip = payrollDatabaseManager.getPayrollHistory().stream()
                .filter(payslip -> payslip.employeeId().equals(loggedInEmployeeId))
                .max(Comparator.comparing(Payslip::id));


        userViewSalary.setText(lastPayslip.map(p -> "Last salary: €" + p.netSalary())
                .orElse("Last salary: /"));

        userViewContract.setText("Contract period: " +
                EmployeeSession.getInstance().getLoggedInEmployee().getContract().getStartDate().format(Var.DTF) +
                " - " +
                EmployeeSession.getInstance().getLoggedInEmployee().getContract().getEndDate().format(Var.DTF));
    }

    /**
     * Navigates to the user edit scene where the employee can edit their personal data.
     */
    public void editData(){
        SceneController.switchToScene("/hr/java/payroll/main/userEdit.fxml");
    }

    /**
     * Signs out the current logged-in employee and navigates to the login scene.
     */
    public void signOut(){
        EmployeeSession.getInstance().logout();
        SceneController.switchToScene("/hr/java/payroll/main/login.fxml");
    }
}
