package hr.java.payroll.entities;

import hr.java.payroll.utils.Var;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Represents a payslip for an employee, containing salary details and payment information.
 * This record provides methods to retrieve formatted payroll period and payment date.
 *
 * @param id The ID of the payslip.
 * @param employeeId The ID of the employee associated with the payslip.
 * @param grossSalary The gross salary of the employee.
 * @param netSalary The net salary of the employee after deductions.
 * @param bonus The bonus awarded to the employee.
 * @param hoursWorked The number of hours worked by the employee.
 * @param payrollPeriod The payroll period for which the payslip is generated.
 * @param paymentDate The payment date for the payslip.
 *
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public record Payslip(
        Long id,
        Long employeeId,
        BigDecimal grossSalary,
        BigDecimal netSalary,
        BigDecimal bonus,
        BigDecimal hoursWorked,
        YearMonth payrollPeriod,
        LocalDate paymentDate
) {
    /**
     * Returns a formatted string representation of the payslip.
     *
     * @return A formatted payslip string with salary details.
     */
    @Override
    public String toString() {
        return String.format(
                "Payslip Details:%n%nGross Salary: %s%nNet Salary: %s%nBonus: %s%nHours Worked: %s%nPayroll Period: %s%nPayment Date: %s%n",
                grossSalary,
                netSalary,
                bonus,
                hoursWorked,
                payrollPeriod,
                paymentDate
        );
    }

    /**
     * Formats the payroll period as "MMM-yyyy" (e.g., "Jan-2024").
     *
     * @return The formatted payroll period.
     */
    public String getFormattedPayrollPeriod() {
        return payrollPeriod.getMonth().name().substring(0, 3) + "-" + payrollPeriod.getYear();
    }

    /**
     * Formats the payment date according to the predefined date-time format.
     *
     * @return The formatted payment date as a string.
     */
    public String getFormattedPaymentDate() {
        return paymentDate.format(Var.DTF);
    }
}
