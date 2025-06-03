package hr.java.payroll.database;

import hr.java.payroll.entities.Contract;
import hr.java.payroll.entities.Employee;
import hr.java.payroll.enums.Gender;
import hr.java.payroll.enums.Role;
import hr.java.payroll.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages employee data in the database, including fetching, saving, deleting, and editing employee records.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class EmployeeDatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(EmployeeDatabaseManager.class);
    private final DatabaseManager databaseManager;

    /**
     * Constructor that initializes the database manager.
     */
    public EmployeeDatabaseManager() {
        this.databaseManager = new DatabaseManager();
    }

    /**
     * Fetches all employees from the database and associates them with their contracts.
     *
     * @param allContracts a list of all contracts to match employees with.
     * @return a set of all employees.
     */
    public Set<Employee> getEmployees(List<Contract> allContracts) {
        String employeeQuery = "SELECT ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, DATE_OF_BIRTH, GENDER, CONTRACT_ID, ROLE FROM EMPLOYEE";
        Set<Employee> employees = new HashSet<>();

        try (Connection connection = databaseManager.connectToDatabase();
             Statement employeeStatement = connection.createStatement();
             ResultSet employeeResultSet = employeeStatement.executeQuery(employeeQuery)) {

            while (employeeResultSet.next()) {
                Long id = employeeResultSet.getLong("id");
                String firstName = employeeResultSet.getString("first_name");
                String lastName = employeeResultSet.getString("last_name");
                String email = employeeResultSet.getString("email");
                String password = employeeResultSet.getString("password");
                LocalDate dateOfBirth = employeeResultSet.getDate("date_of_birth").toLocalDate();
                Gender gender = Gender.valueOf(employeeResultSet.getString("gender"));
                Long contractId = employeeResultSet.getLong("contract_id");
                Role role = Role.valueOf(employeeResultSet.getString("role"));

                Optional<Contract> contract = allContracts.stream()
                        .filter(c -> c.getId().equals(contractId))
                        .findFirst();

                if (contract.isPresent()) {
                    employees.add(new Employee.EmployeeBuilder()
                            .setId(id)
                            .setFirstName(firstName)
                            .setLastName(lastName)
                            .setEmail(email)
                            .setPassword(password)
                            .setDateOfBirth(dateOfBirth)
                            .setGender(gender)
                            .setContract(contract.get())
                            .setRole(role)
                            .build());
                } else {
                    log.error("Contract id for employee not found.");
                }
            }
            return employees.stream().sorted(Comparator.comparing(Employee::getId)).collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (SQLException | IOException e) {
            log.error("Error occurred while fetching employees", e);
        }
        return employees;
    }

    /**
     * Saves a new employee to the database.
     *
     * @param employee the employee to be saved.
     */
    public void saveEmployee(Employee employee) {
        String employeeQuery = "INSERT INTO EMPLOYEE (FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, DATE_OF_BIRTH, GENDER, CONTRACT_ID, ROLE) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = databaseManager.connectToDatabase();
             PreparedStatement employeePreparedStatement = connection.prepareStatement(employeeQuery)) {
            employeePreparedStatement.setString(1, employee.getFirstName());
            employeePreparedStatement.setString(2, employee.getLastName());
            employeePreparedStatement.setString(3, employee.getEmail());
            employeePreparedStatement.setString(4, employee.getPassword());
            employeePreparedStatement.setString(5, employee.getDateOfBirth().toString());
            employeePreparedStatement.setString(6, employee.getGender().toString());
            employeePreparedStatement.setLong(7, employee.getContract().getId());
            employeePreparedStatement.setString(8, employee.getRole().toString());
            employeePreparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            log.error("Error occurred while saving employee.", e);
        }
    }

    /**
     * Deletes an employee from the database.
     *
     * @param employee the employee to be deleted.
     * @throws DatabaseException if an error occurs while deleting the employee from the database.
     */
    public void deleteEmployee(Employee employee){
        String employeeQuery = "DELETE FROM EMPLOYEE WHERE ID = ?";
        try (Connection connection = databaseManager.connectToDatabase();
             PreparedStatement employeePreparedStatement = connection.prepareStatement(employeeQuery)) {

            employeePreparedStatement.setLong(1, employee.getId());
            int rowsAffected = employeePreparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new DatabaseException("No employee found with the provided ID.");
            }
        } catch (SQLException | IOException e) {
            throw new DatabaseException("Error accessing the database while deleting the employee.", e);
        }
    }

    /**
     * Edits an existing employee's data in the database.
     *
     * @param employee the employee to be edited.
     */
    public void editEmployee(Employee employee) {
        String employeeQuery = "UPDATE EMPLOYEE SET FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ?, PASSWORD = ?, DATE_OF_BIRTH = ?, GENDER = ?, CONTRACT_ID = ?, ROLE = ? WHERE ID = ?";
        try (Connection connection = databaseManager.connectToDatabase();
             PreparedStatement employeePreparedStatement = connection.prepareStatement(employeeQuery)) {

            employeePreparedStatement.setString(1, employee.getFirstName());
            employeePreparedStatement.setString(2, employee.getLastName());
            employeePreparedStatement.setString(3, employee.getEmail());
            employeePreparedStatement.setString(4, employee.getPassword());
            employeePreparedStatement.setDate(5, java.sql.Date.valueOf(employee.getDateOfBirth()));
            employeePreparedStatement.setString(6, employee.getGender().toString());
            employeePreparedStatement.setLong(7, employee.getContract().getId());
            employeePreparedStatement.setString(8, employee.getRole().toString());
            employeePreparedStatement.setLong(9, employee.getId());
            employeePreparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            log.error("Error accessing the database while editing the employee", e);
        }
    }
}
