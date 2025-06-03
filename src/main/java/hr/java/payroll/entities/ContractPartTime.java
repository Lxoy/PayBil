package hr.java.payroll.entities;

import hr.java.payroll.enums.Position;
import hr.java.payroll.interfaces.ContractType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a part-time employment contract.
 * This class extends {@link Contract} and implements the {@link ContractType} interface.
 * It includes properties for hours worked and hourly rate, with methods for calculating gross and net salaries.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public final class ContractPartTime extends Contract implements ContractType{
    private BigDecimal hoursWorked;
    private BigDecimal hourlyRate;

    /**
     * Private constructor for {@link ContractPartTime} using the builder pattern.
     *
     * @param builder the builder used to create the contract
     */
    private ContractPartTime(ContractPartTimeBuilder builder) {
        super(builder.id, builder.name, builder.position, builder.baseSalary, builder.startDate, builder.endDate);
        this.hoursWorked = builder.hoursWorked;
        this.hourlyRate = builder.hourlyRate;
    }

    /**
     * Builder class for constructing a {@link ContractPartTime} object.
     */
    public static class ContractPartTimeBuilder {
        Long id;
        String name;
        Position position;
        BigDecimal baseSalary;
        LocalDate startDate;
        LocalDate endDate;
        BigDecimal hoursWorked;
        BigDecimal hourlyRate;

        /**
         * Sets the ID for the contract.
         *
         * @param id the unique identifier of the contract
         * @return the builder object
         */
        public ContractPartTimeBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the name of the contract.
         *
         * @param name the name of the contract
         * @return the builder object
         */
        public ContractPartTimeBuilder setName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the position of the employee.
         *
         * @param position the position of the employee
         * @return the builder object
         */
        public ContractPartTimeBuilder setPosition(Position position) {
            this.position = position;
            return this;
        }

        /**
         * Sets the base salary for the contract.
         * The base salary is calculated as the product of hours worked and hourly rate.
         *
         * @param hoursWorked the number of hours worked
         * @param hourlyRate the hourly rate
         * @return the builder object
         */
        public ContractPartTimeBuilder setBaseSalary(BigDecimal hoursWorked, BigDecimal hourlyRate) {
            this.baseSalary = hoursWorked.multiply(hourlyRate);
            return this;
        }

        /**
         * Sets the start date of the contract.
         *
         * @param startDate the start date of the contract
         * @return the builder object
         */
        public ContractPartTimeBuilder setStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        /**
         * Sets the end date of the contract.
         *
         * @param endDate the end date of the contract
         * @return the builder object
         */
        public ContractPartTimeBuilder setEndDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        /**
         * Sets the number of hours worked for the contract.
         *
         * @param hoursWorked the number of hours worked
         * @return the builder object
         */
        public ContractPartTimeBuilder setHoursWorked(BigDecimal hoursWorked) {
            this.hoursWorked = hoursWorked;
            return this;
        }

        /**
         * Sets the hourly rate for the contract.
         *
         * @param hourlyRate the hourly rate
         * @return the builder object
         */
        public ContractPartTimeBuilder setHourlyRate(BigDecimal hourlyRate) {
            this.hourlyRate = hourlyRate;
            return this;
        }

        /**
         * Builds a {@link ContractPartTime} object using the specified properties.
         *
         * @return the created {@link ContractPartTime} object
         */
        public ContractPartTime build() {
            return new ContractPartTime(this);
        }
    }

    /**
     * Gets the number of hours worked for this part-time contract.
     *
     * @return the hours worked
     */
    public BigDecimal getHoursWorked() {
        return hoursWorked;
    }

    /**
     * Gets the hourly rate for this part-time contract.
     *
     * @return the hourly rate
     */
    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    /**
     * Returns the type of contract, which is "Part Time".
     *
     * @return the string "Part Time"
     */
    @Override
    public String getContractType() {
        return "Part Time";
    }

    /**
     * Calculates the gross salary for this part-time contract.
     * The gross salary is calculated by multiplying the number of hours worked by the hourly rate.
     *
     * @return the calculated gross salary
     */
    @Override
    public BigDecimal calculateGrossSalary() {
        return hourlyRate.multiply(hoursWorked);
    }

    /**
     * Calculates the net salary for this part-time contract.
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
