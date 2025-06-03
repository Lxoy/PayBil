package hr.java.payroll.controller;

import hr.java.payroll.utils.EmployeeSession;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

import java.util.AbstractMap;
import java.util.Map;

/**
 * This class handles the admin menu actions for the Payroll Management System.
 * It initializes the menu items and assigns actions to each item based on the role of the logged-in user.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class AdminMenuController {
    @FXML private MenuItem employeeList;
    @FXML private MenuItem employeeAdd;
    @FXML private MenuItem employeeEdit;
    @FXML private MenuItem contractFullTimeList;
    @FXML private MenuItem contractFullTimeAdd;
    @FXML private MenuItem contractFullTimeEdit;
    @FXML private MenuItem contractPartTimeList;
    @FXML private MenuItem contractPartTimeAdd;
    @FXML private MenuItem contractPartTimeEdit;
    @FXML private MenuItem payrollHistory;
    @FXML private MenuItem generatePayslip;
    @FXML private MenuItem changesHistory;
    @FXML private MenuItem signOut;

    /**
     * Initializes the menu and sets the actions for each menu item.
     * Based on the logged-in employee's role, the appropriate menu items are available.
     *
     * If the logged-in employee's role is USER, the user-specific view is shown.
     */

    public void initialize() {
        Map<MenuItem, String> menuActions = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(employeeList, "/hr/java/payroll/main/employeeList.fxml"),
                new AbstractMap.SimpleEntry<>(employeeAdd, "/hr/java/payroll/main/employeeAdd.fxml"),
                new AbstractMap.SimpleEntry<>(employeeEdit, "/hr/java/payroll/main/employeeEdit.fxml"),
                new AbstractMap.SimpleEntry<>(contractFullTimeList, "/hr/java/payroll/main/contractFullTimeList.fxml"),
                new AbstractMap.SimpleEntry<>(contractFullTimeAdd, "/hr/java/payroll/main/contractFullTimeAdd.fxml"),
                new AbstractMap.SimpleEntry<>(contractFullTimeEdit, "/hr/java/payroll/main/contractFullTimeEdit.fxml"),
                new AbstractMap.SimpleEntry<>(contractPartTimeList, "/hr/java/payroll/main/contractPartTimeList.fxml"),
                new AbstractMap.SimpleEntry<>(contractPartTimeAdd, "/hr/java/payroll/main/contractPartTimeAdd.fxml"),
                new AbstractMap.SimpleEntry<>(contractPartTimeEdit, "/hr/java/payroll/main/contractPartTimeEdit.fxml"),
                new AbstractMap.SimpleEntry<>(payrollHistory, "/hr/java/payroll/main/payrollHistory.fxml"),
                new AbstractMap.SimpleEntry<>(generatePayslip, "/hr/java/payroll/main/generatePayslip.fxml"),
                new AbstractMap.SimpleEntry<>(changesHistory, "/hr/java/payroll/main/changesHistory.fxml"),
                new AbstractMap.SimpleEntry<>(signOut, "/hr/java/payroll/main/login.fxml")
        );

        menuActions.forEach(this::setMenuAction);
    }

    /**
     * Sets the action for a menu item to switch to the corresponding scene.
     * If the logout menu item is selected, the employee is logged out.
     *
     * @param menuItem the menu item to set the action for
     * @param fxmlFile the FXML file to switch to when the menu item is selected
     */
    private void setMenuAction(MenuItem menuItem, String fxmlFile) {
        menuItem.setOnAction(event -> {
            if(fxmlFile.equals("/hr/java/payroll/main/login.fxml")) {
                EmployeeSession.getInstance().logout();
            }
            SceneController.switchToScene(fxmlFile);
        });
    }
}
