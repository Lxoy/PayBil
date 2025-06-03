package hr.java.payroll.entities;

import hr.java.payroll.utils.Var;

import java.math.BigDecimal;

/**
 * Utility class for tax calculations based on salary.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class TaxCalculator {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private TaxCalculator() {}

    /**
     * Calculates the tax rate based on the given gross salary.
     *
     * @param grossSalary the gross salary amount
     * @return the applicable tax rate
     */
    public static BigDecimal calculateTaxRate(BigDecimal grossSalary) {
        if(grossSalary.compareTo(Var.MINIMAL_SALARY) < 0) {
            return BigDecimal.ZERO;
        }
        return grossSalary.compareTo(Var.TAX_THRESHOLD) <= 0 ?  Var.LOWER_TAX_RATE : Var.HIGHER_TAX_RATE;
    }

    /**
     * Calculates the tax amount based on the given gross salary and tax rate.
     *
     * @param grossSalary the gross salary amount
     * @param taxRate the applicable tax rate
     * @return the calculated tax amount
     */
    public static BigDecimal calculateTax(BigDecimal grossSalary, BigDecimal taxRate) {
        return grossSalary.multiply(taxRate);
    }
}
