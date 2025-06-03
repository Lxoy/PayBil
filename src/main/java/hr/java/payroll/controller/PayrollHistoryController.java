package hr.java.payroll.controller;

import hr.java.payroll.database.PayrollDatabaseManager;
import hr.java.payroll.entities.Payslip;
import hr.java.payroll.exception.InvalidInputException;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for managing payroll history and search functionality.
 * This class is responsible for initializing the payroll history table view,
 * searching and filtering payroll records, and clearing the input fields.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class PayrollHistoryController {
    private static final Logger log = LoggerFactory.getLogger(PayrollHistoryController.class);
    PayrollDatabaseManager payrollDatabaseManager = new PayrollDatabaseManager();

    @FXML private TableView<Payslip> payrollHistoryTableView;
    @FXML private TableColumn<Payslip, String> payrollHistoryIDTableColumn;
    @FXML private TableColumn<Payslip, String> payrollHistoryEmployeeIDTableColumn;
    @FXML private TableColumn<Payslip, String> payrollHistoryGrossSalaryTableColumn;
    @FXML private TableColumn<Payslip, String> payrollHistoryNetSalaryTableColumn;
    @FXML private TableColumn<Payslip, String> payrollHistoryBonusTableColumn;
    @FXML private TableColumn<Payslip, String> payrollHistoryHoursWorkedTableColumn;
    @FXML private TableColumn<Payslip, String> payrollHistorPayrollPeriodTableColumn;
    @FXML private TableColumn<Payslip, String> payrollHistoryPaymentDateTableColumn;

    @FXML private TextField payrollHistoryEmployeeIDTextField;
    @FXML private DatePicker payrollHistoryPaymentDateDatePicker;
    @FXML private TextField payrollHistoryGrossSalaryTextField;
    @FXML private TextField payrollHistoryNetSalaryTextField;
    @FXML private TextField payrollHistoryBonusTextField;
    @FXML private TextField payrollHistoryHoursWorkedTextField;

    /**
     * Initializes the payroll history table with column mappings and calls the search method.
     * Sets up the columns to display specific attributes of each payslip.
     */
    public void initialize() {
        payrollHistoryIDTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().id().toString()));
        payrollHistoryEmployeeIDTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().employeeId().toString()));
        payrollHistoryGrossSalaryTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().grossSalary().toString()));
        payrollHistoryNetSalaryTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().netSalary().toString()));
        payrollHistoryBonusTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().bonus().toString()));
        payrollHistoryHoursWorkedTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().hoursWorked().toString()));
        payrollHistorPayrollPeriodTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedPayrollPeriod()));
        payrollHistoryPaymentDateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormattedPaymentDate()));
        search();
    }

    /**
     * Searches for payroll history based on the input criteria and displays the results in the table.
     * Validates the input fields and filters the results based on employee ID, salary, bonus, hours worked, and payment date.
     */
    public void search() {
        try {
            InputCheck.integerCheck(payrollHistoryEmployeeIDTextField.getText(), "Invalid employee id input.");
            InputCheck.bigDecimalCheck(payrollHistoryGrossSalaryTextField.getText(), "Invalid gross salary input.");
            InputCheck.bigDecimalCheck(payrollHistoryNetSalaryTextField.getText(), "Invalid net salary input.");
            InputCheck.bigDecimalCheck(payrollHistoryBonusTextField.getText(), "Invalid bonus input.");
            InputCheck.bigDecimalCheck(payrollHistoryHoursWorkedTextField.getText(), "Invalid hours worked input.");
        } catch (InvalidInputException e) {
            Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", e.getMessage());
            log.warn("Invalid input: {}", e.getMessage(), e);
            return;
        }

        Long employeeId = payrollHistoryEmployeeIDTextField.getText().isEmpty() ? null : Long.parseLong(payrollHistoryEmployeeIDTextField.getText());
        LocalDate paymentDate = payrollHistoryPaymentDateDatePicker.getValue();
        BigDecimal grossSalary = payrollHistoryGrossSalaryTextField.getText().isEmpty() ? null : new BigDecimal(payrollHistoryGrossSalaryTextField.getText());
        BigDecimal netSalary = payrollHistoryNetSalaryTextField.getText().isEmpty() ? null : new BigDecimal(payrollHistoryNetSalaryTextField.getText());
        BigDecimal bonus = payrollHistoryBonusTextField.getText().isEmpty() ? null : new BigDecimal(payrollHistoryBonusTextField.getText());
        BigDecimal hoursWorked = payrollHistoryHoursWorkedTextField.getText().isEmpty() ? null : new BigDecimal(payrollHistoryHoursWorkedTextField.getText());

        List<Payslip> payslips = payrollDatabaseManager.getPayrollHistory().stream()
                .filter(payslip  -> employeeId == null || payslip.employeeId().compareTo(employeeId) == 0)
                .filter(payslip -> paymentDate == null || payslip.paymentDate().isEqual(paymentDate))
                .filter(payslip -> grossSalary == null || payslip.grossSalary().compareTo(grossSalary) == 0)
                .filter(payslip -> netSalary == null || payslip.netSalary().compareTo(netSalary) == 0)
                .filter(payslip -> bonus == null || payslip.bonus().compareTo(bonus) == 0)
                .filter(payslip -> hoursWorked == null || payslip.hoursWorked().compareTo(hoursWorked) == 0)
                .toList();

        ObservableList<Payslip> observableList = FXCollections.observableArrayList(payslips);
        payrollHistoryTableView.setItems(observableList);
    }

    /**
     * Clears the input fields and resets the search results.
     */
    public void clear(){
        payrollHistoryEmployeeIDTextField.clear();
        payrollHistoryPaymentDateDatePicker.setValue(null);
        payrollHistoryPaymentDateDatePicker.setPromptText("Payment Date");
        payrollHistoryGrossSalaryTextField.clear();
        payrollHistoryNetSalaryTextField.clear();
        payrollHistoryBonusTextField.clear();
        payrollHistoryHoursWorkedTextField.clear();
        search();
    }
}
