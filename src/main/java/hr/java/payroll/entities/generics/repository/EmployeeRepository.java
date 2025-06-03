package hr.java.payroll.entities.generics.repository;

import hr.java.payroll.database.ContractFullTimeDatabaseManager;
import hr.java.payroll.database.ContractPartTimeDatabaseManager;
import hr.java.payroll.database.EmployeeDatabaseManager;
import hr.java.payroll.entities.Contract;
import hr.java.payroll.entities.Employee;
import hr.java.payroll.utils.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Repository for managing Employee entities. Provides methods for saving and acquiring employee data.
 * Also ensures that a login file for employees exists.
 *
 * @param <T> the type of employee entity, extending {@link Employee}.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class EmployeeRepository<T extends Employee> extends AbstractRepository<T> {
    private static final Logger log = LoggerFactory.getLogger(EmployeeRepository.class);
    private static final ContractPartTimeDatabaseManager contractPartTimeDatabaseManager = new ContractPartTimeDatabaseManager();
    private static final ContractFullTimeDatabaseManager contractFullTimeDatabaseManager = new ContractFullTimeDatabaseManager();
    private static final EmployeeDatabaseManager employeeDatabaseManager = new EmployeeDatabaseManager();

    /**
     * Initializes the repository and ensures that the employee login file exists.
     */
    public EmployeeRepository() {
        ensureFileExists();
    }

    /**
     * Saves a set of employee entities to a file.
     *
     * @param entities a set of employee entities to be saved.
     */
    @Override
    public void save(Set<T> entities) {
        try (PrintWriter writer = new PrintWriter(Var.EMPLOYEE_LOGIN_FILE_PATH)) {
            for (T entity : entities) {
                writer.println(entity.getEmail() + ":" + entity.getPassword());
            }
        } catch (FileNotFoundException e) {
            log.error("Error while saving employee data to file. Please check the file path or permissions.", e);
        }
    }

    /**
     * Acquires employee data from the login file.
     *
     * @return a list of strings containing employee email and password pairs.
     */
    @Override
    public List<String> acquire() {
        List<String> employees = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Path.of(Var.EMPLOYEE_LOGIN_FILE_PATH))) {
            stream.forEach(fileRow -> {
                String[] employeeRow = fileRow.split(":");
                if (employeeRow.length == 2) {
                    employees.add(employeeRow[0] + ":" + employeeRow[1]);
                }
            });
        } catch (IOException e) {
            log.error("Error while getting employee data to file.", e);
        }
        return employees;
    }

    /**
     * Ensures the existence of the employee login file. If it does not exist or is empty,
     * it creates the file and populates it with employee data from the database.
     */
    private void ensureFileExists() {
        File file = new File(Var.EMPLOYEE_LOGIN_FILE_PATH);
        List<? extends Contract> contracts = Stream.concat(
                contractFullTimeDatabaseManager.getAllFullTimeContracts().stream(),
                contractPartTimeDatabaseManager.getAllPartTimeContracts().stream()
        ).toList();
        Set<Employee> employeesFromDatabase = employeeDatabaseManager.getEmployees((List<Contract>) contracts);
        if (!file.exists() || file.length() == 0) {
            try (PrintWriter writer = new PrintWriter(Var.EMPLOYEE_LOGIN_FILE_PATH)) {
                for(Employee employee : employeesFromDatabase) {
                    writer.println(employee.getEmail() + ":" + employee.getPassword());
                }
            } catch (FileNotFoundException e) {
                log.error("Error creating user file", e);
            }
        }
    }
}
