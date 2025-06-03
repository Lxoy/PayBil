package hr.java.payroll.database;

import hr.java.payroll.entities.Payslip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages payroll history data in the database, including fetching and saving payslips.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class PayrollDatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(PayrollDatabaseManager.class);
    private final DatabaseManager databaseManager;

    /**
     * Constructor that initializes the database manager.
     */
    public PayrollDatabaseManager() {
        this.databaseManager = new DatabaseManager();
    }

    /**
     * Fetches the payroll history from the database.
     *
     * @return a list of payslips.
     */
    public List<Payslip> getPayrollHistory(){
        String payrollQuery = "SELECT ID, EMPLOYEE_ID, GROSS_SALARY, NET_SALARY, BONUS, HOURS_WORKED, PAYROLL_PERIOD, PAYMENT_DATE FROM PAYROLL_HISTORY";
        List<Payslip> payslips = new ArrayList<>();

        try (Connection connection = databaseManager.connectToDatabase();
             Statement payrollStatement = connection.createStatement();
             ResultSet payrollResultSet = payrollStatement.executeQuery(payrollQuery)) {

            while (payrollResultSet.next()) {
                Long id = payrollResultSet.getLong("id");
                Long employeeId = payrollResultSet.getLong("employee_id");
                BigDecimal grossSalary = payrollResultSet.getBigDecimal("gross_salary");
                BigDecimal netSalary = payrollResultSet.getBigDecimal("net_salary");
                BigDecimal bonus = payrollResultSet.getBigDecimal("bonus");
                BigDecimal hoursWorked = payrollResultSet.getBigDecimal("hours_worked");
                YearMonth payrollPeriod = YearMonth.parse(payrollResultSet.getString("payroll_period"));
                LocalDate paymentDate = LocalDate.parse(payrollResultSet.getString("payment_date"));
                payslips.add(new Payslip(id, employeeId, grossSalary, netSalary, bonus, hoursWorked, payrollPeriod, paymentDate));
            }
            return payslips;
        } catch (SQLException | IOException e) {
            log.error("Error occurred while fetching payroll history.", e);
        }
        return payslips;
    }

    /**
     * Saves a new payslip to the payroll history in the database.
     *
     * @param payslip the payslip to be saved.
     */
    public void savePayrollHistory(Payslip payslip) {
        String payrollQuery = "INSERT INTO PAYROLL_HISTORY (EMPLOYEE_ID, GROSS_SALARY, NET_SALARY, BONUS, HOURS_WORKED, PAYROLL_PERIOD, PAYMENT_DATE) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = databaseManager.connectToDatabase();
             PreparedStatement payrollPreparedStatement = connection.prepareStatement(payrollQuery)) {
            payrollPreparedStatement.setLong(1, payslip.employeeId());
            payrollPreparedStatement.setBigDecimal(2, payslip.grossSalary());
            payrollPreparedStatement.setBigDecimal(3, payslip.netSalary());
            payrollPreparedStatement.setBigDecimal(4, payslip.bonus());
            payrollPreparedStatement.setBigDecimal(5, payslip.hoursWorked());
            payrollPreparedStatement.setString(6, payslip.payrollPeriod().toString());
            payrollPreparedStatement.setString(7, payslip.paymentDate().toString());
            payrollPreparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            log.error("Error occurred while saving payslip to payroll history", e);
        }
    }
}
