package hr.java.payroll.entities;

import hr.java.payroll.enums.Position;
import hr.java.payroll.interfaces.ContractType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a full-time employment contract.
 * This class extends {@link Contract} and implements the {@link ContractType} interface.
 * It includes additional properties such as a bonus and methods for calculating gross and net salaries.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public final class ContractFullTime extends Contract implements ContractType {
    private BigDecimal bonus;

    /**
     * Private constructor for {@link ContractFullTime} using the builder pattern.
     *
     * @param builder the builder used to create the contract
     */
    private ContractFullTime(ContractFullTimeBuilder builder) {
       super(builder.id, builder.name, builder.position, builder.baseSalary, builder.startDate, builder.endDate);
       this.bonus = builder.bonus;
    }

    /**
     * Builder class for constructing a {@link ContractFullTime} object.
     */
    public static class ContractFullTimeBuilder {
        Long id;
        String name;
        Position position;
        BigDecimal baseSalary;
        LocalDate startDate;
        LocalDate endDate;
        BigDecimal bonus;

        /**
         * Sets the ID for the contract.
         *
         * @param id the unique identifier of the contract
         * @return the builder object
         */
        public ContractFullTimeBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the name of the contract.
         *
         * @param name the name of the contract
         * @return the builder object
         */
        public ContractFullTimeBuilder setName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the position of the employee.
         *
         * @param position the position of the employee
         * @return the builder object
         */
        public ContractFullTimeBuilder setPosition(Position position) {
            this.position = position;
            return this;
        }

        /**
         * Sets the base salary for the contract.
         *
         * @param baseSalary the base salary
         * @return the builder object
         */
        public ContractFullTimeBuilder setBaseSalary(BigDecimal baseSalary) {
            this.baseSalary = baseSalary;
            return this;
        }

        /**
         * Sets the start date of the contract.
         *
         * @param startDate the start date of the contract
         * @return the builder object
         */
        public ContractFullTimeBuilder setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        /**
         * Sets the end date of the contract.
         *
         * @param endDate the end date of the contract
         * @return the builder object
         */
        public ContractFullTimeBuilder setEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        /**
         * Sets the bonus for the contract.
         *
         * @param bonus the bonus amount
         * @return the builder object
         */
        public ContractFullTimeBuilder setBonus(BigDecimal bonus) {
            this.bonus = bonus;
            return this;
        }

        /**
         * Builds a {@link ContractFullTime} object using the specified properties.
         *
         * @return the created {@link ContractFullTime} object
         */
        public ContractFullTime build() {
            return new ContractFullTime(this);
        }
    }

    /**
     * Gets the bonus associated with this full-time contract.
     *
     * @return the bonus
     */
    public BigDecimal getBonus() {
        return bonus;
    }

    /**
     * Returns the type of contract, which is "Full Time".
     *
     * @return the string "Full Time"
     */
    @Override
    public String getContractType() {
        return "Full Time";
    }

    /**
     * Calculates the gross salary for this full-time contract.
     * The gross salary is the sum of the base salary and the bonus.
     *
     * @return the calculated gross salary
     */
    @Override
    public BigDecimal calculateGrossSalary() {
        return super.getBaseSalary().add(bonus);
    }

    /**
     * Calculates the net salary for this full-time contract.
     * The net salary is calculated by subtracting the tax from the gross salary.
     *
     * @return the calculated net salary
     */
    @Override
    public BigDecimal calculateNetSalary() {
        BigDecimal grossSalary = calculateGrossSalary();
        BigDecimal taxRate = TaxCalculator.calculateTaxRate(grossSalary);
        BigDecimal tax = TaxCalculator.calculateTax(grossSalary, taxRate);
        return calculateGrossSalary().subtract(tax);
    }
}
