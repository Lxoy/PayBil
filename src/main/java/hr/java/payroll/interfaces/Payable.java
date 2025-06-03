package hr.java.payroll.interfaces;

import java.math.BigDecimal;

/**
 * Represents an entity that can calculate salary details.
 * Implementing classes should provide logic to calculate gross and net salary.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public interface Payable {
    /**
     * Calculates the gross salary.
     *
     * @return the gross salary as a {@link BigDecimal}
     */
    BigDecimal calculateGrossSalary();

    /**
     * Calculates the net salary after deductions.
     *
     * @return the net salary as a {@link BigDecimal}
     */
    BigDecimal calculateNetSalary();
}
