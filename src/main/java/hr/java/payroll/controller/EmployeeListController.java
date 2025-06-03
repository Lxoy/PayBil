package hr.java.payroll.controller;

import hr.java.payroll.database.ContractFullTimeDatabaseManager;
import hr.java.payroll.database.ContractPartTimeDatabaseManager;
import hr.java.payroll.database.EmployeeDatabaseManager;
import hr.java.payroll.entities.Contract;
import hr.java.payroll.entities.Employee;
import hr.java.payroll.enums.Gender;
import hr.java.payroll.enums.Role;
import hr.java.payroll.exception.DatabaseException;
import hr.java.payroll.exception.InvalidInputException;
import hr.java.payroll.entities.generics.repository.EmployeeRepository;
import hr.java.payroll.utils.EmployeeSession;
import hr.java.payroll.utils.InputCheck;
import hr.java.payroll.utils.Message;
import hr.java.payroll.utils.Var;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Controller class for managing employee records in the payroll system.
 * It allows users to search, clear, and delete employee entries from the employee list.
 * It interacts with the database managers for full-time and part-time contracts, as well as employee data.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class EmployeeListController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeListController.class);
    ContractPartTimeDatabaseManager contractPartTimeDatabaseManager = new ContractPartTimeDatabaseManager();
    ContractFullTimeDatabaseManager contractFullTimeDatabaseManager = new ContractFullTimeDatabaseManager();
    EmployeeDatabaseManager employeeDatabaseManager = new EmployeeDatabaseManager();
    EmployeeRepository<Employee> employeeRepository = new EmployeeRepository<>();

    @FXML private TableView<Employee> employeListTableView;
    @FXML private TableColumn<Employee, String> employeeListIdTableColumn;
    @FXML private TableColumn<Employee, String> employeeListFirstNameTableColumn;
    @FXML private TableColumn<Employee, String> employeeListLastNameTableColumn;
    @FXML private TableColumn<Employee, String> employeeListAgeTableColumn;
    @FXML private TableColumn<Employee, String> employeeListGenderTableColumn;
    @FXML private TableColumn<Employee, String> employeeListEmailTableColumn;
    @FXML private TableColumn<Employee, String> employeeListContractIDTableColumn;
    @FXML private TableColumn<Employee, String> employeeListContractTypeTableColumn;
    @FXML private TableColumn<Employee, String> employeeListRoleTableColumn;

    @FXML private TextField employeeListFirstNameTextField;
    @FXML private TextField employeeListLastNameTextField;
    @FXML private TextField employeeListAgeTextField;
    @FXML private ComboBox<Gender> employeeListGenderComboBox;
    @FXML private TextField employeeListContractIDTextField;
    @FXML private ComboBox<Role> employeeListRoleComboBox;

    /**
     * Initializes the Employee List Controller, setting up the combo boxes, table columns, and initial data.
     */
    public void initialize() {
        employeeListGenderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        employeeListRoleComboBox.setItems(FXCollections.observableArrayList(Role.values()));

        employeeListIdTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        employeeListFirstNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        employeeListLastNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        employeeListAgeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAge().toString()));
        employeeListGenderTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender().name()));
        employeeListEmailTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        employeeListContractIDTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContract().getId().toString()));
        employeeListContractTypeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContract().getType()));
        employeeListRoleTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().name()));
        search();
        if(EmployeeSession.getInstance().getLoggedInEmployee().getRole().equals(Role.USER)){
            EmployeeSession.getInstance().logout();
            SceneController.switchToScene("/hr/java/payroll/main/login.fxml");
        }
    }

    /**
     * Searches for employees based on the entered filter criteria (first name, last name, age, gender, contract ID, and role).
     * Validates the input values and applies the search filters.
     */
    public void search() {
        List<? extends Contract> contracts = Stream.concat(
                contractPartTimeDatabaseManager.getAllPartTimeContracts().stream(),
                contractFullTimeDatabaseManager.getAllFullTimeContracts().stream()
        ).toList();

        try {
            InputCheck.stringCheck(employeeListFirstNameTextField.getText(), "Invalid first name input.");
            InputCheck.stringCheck(employeeListLastNameTextField.getText(), "Invalid last name input.");
            InputCheck.integerCheck(employeeListAgeTextField.getText(), "Invalid age input.");
            InputCheck.integerCheck(employeeListContractIDTextField.getText(), "Invalid contract ID input.");
        } catch (InvalidInputException e) {
            Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", e.getMessage());
            log.warn("Invalid input error: {}", e.getMessage());
            return;
        }

        String firstName = employeeListFirstNameTextField.getText().trim().toLowerCase();
        String lastName = employeeListLastNameTextField.getText().trim().toLowerCase();
        Integer age = employeeListAgeTextField.getText().isEmpty() ? null : Integer.parseInt(employeeListAgeTextField.getText().trim());
        Gender gender = employeeListGenderComboBox.getValue();
        Long contractID = employeeListContractIDTextField.getText().isEmpty() ? null : Long.valueOf(employeeListContractIDTextField.getText().trim());
        Role role = employeeListRoleComboBox.getValue();

        List<Employee> employees = employeeDatabaseManager.getEmployees((List<Contract>) contracts).stream()
                .filter(employee -> firstName.isEmpty() || employee.getFirstName().toLowerCase().contains(firstName))
                .filter(employee -> lastName.isEmpty() || employee.getLastName().toLowerCase().contains(lastName))
                .filter(employee -> age == null || age.equals(employee.getAge()))
                .filter(employee -> gender == null || gender.equals(employee.getGender()))
                .filter(employee -> contractID == null || contractID.equals(employee.getContract().getId()))
                .filter(employee -> role == null || role.equals(employee.getRole()))
                .toList();

        ObservableList<Employee> observableList = FXCollections.observableArrayList(employees);

        employeListTableView.setItems(observableList);
        employeeRepository.save(employeeDatabaseManager.getEmployees((List<Contract>) contracts));
        if(EmployeeSession.getInstance().getLoggedInEmployee().getRole().equals(Role.USER)){
            EmployeeSession.getInstance().logout();
            SceneController.switchToScene("/hr/java/payroll/main/login.fxml");
        }
    }

    /**
     * Clears all input fields and resets the search filters.
     */
    public void clear(){
        employeeListFirstNameTextField.clear();
        employeeListLastNameTextField.clear();
        employeeListAgeTextField.clear();
        employeeListGenderComboBox.getSelectionModel().clearSelection();
        employeeListGenderComboBox.setValue(null);
        employeeListGenderComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Gender item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "Gender" : item.toString());
            }
        });
        employeeListContractIDTextField.clear();
        employeeListRoleComboBox.setValue(null);
        employeeListRoleComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Role item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "Role" : item.toString());
            }
        });
        search();
    }

    /**
     * Deletes the selected employee from the employee list after confirmation.
     * Prevents the logged-in employee from deleting their own record.
     */
    public void delete(){
        Employee selectedEmployee = employeListTableView.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            Message.showAlert(Alert.AlertType.WARNING, Var.WARNING_TITLE, "No employee selected", "Please select a employee to delete.");
            return;
        }

        if(selectedEmployee.getEmail().equals(EmployeeSession.getInstance().getLoggedInEmployee().getEmail())){
            Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "Wrong employee selected", "You cannot delete yourself!");
            return;
        }

        Alert alert = Message.createAlert(Alert.AlertType.CONFIRMATION, Var.CONFIRM_TITLE, "Delete Employee", "Are you sure you want to delete the selected employee? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                employeeDatabaseManager.deleteEmployee(selectedEmployee);
                search();
            } catch (DatabaseException e) {
                Message.showAlert(Alert.AlertType.ERROR, Var.ERROR_TITLE, "Employee Deletion Failed", e.getMessage());
                log.error("Failed to delete employee with ID {}: {}", selectedEmployee.getId(), e.getMessage());
            }
        }
    }
}
