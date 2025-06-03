package hr.java.payroll.utils;

import hr.java.payroll.entities.Employee;

/**
 * Singleton class that manages the login state of an employee.
 * Provides methods for logging in, logging out, and checking the current login status.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class EmployeeSession {
    private static EmployeeSession instance = getInstance();
    private Employee loggedInEmployee;

    /**
     * Returns the single instance of the EmployeeSession class.
     * Implements the Singleton design pattern to ensure only one instance exists.
     *
     * @return the instance of EmployeeSession
     */
    public static EmployeeSession getInstance(){
        if(instance == null){
            instance = new EmployeeSession();
        }
        return instance;
    }

    /**
     * Logs in an employee by setting the logged-in employee.
     *
     * @param employee the employee to log in
     */
    public void login(Employee employee) {
        this.loggedInEmployee = employee;
    }

    /**
     * Logs out the currently logged-in employee by setting the logged-in employee to null.
     */
    public void logout() {
        this.loggedInEmployee = null;
    }

    /**
     * Returns the currently logged-in employee.
     *
     * @return the logged-in employee, or null if no employee is logged in
     */
    public Employee getLoggedInEmployee() {
        return loggedInEmployee;
    }
}
