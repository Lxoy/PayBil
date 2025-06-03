package hr.java.payroll.entities;

import hr.java.payroll.enums.Position;
import hr.java.payroll.interfaces.Payable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Abstract class representing a contract for an employee.
 * This class implements the Payable interface and is extended by specific contract types like full-time and part-time.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public abstract class Contract extends Entity implements Payable{
    private String name;
    private Position position;
    private BigDecimal baseSalary;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Constructs a Contract object with the specified details.
     *
     * @param id        the unique identifier of the contract
     * @param name      the name of the contract
     * @param position  the position of the employee under this contract
     * @param baseSalary the base salary for this contract
     * @param startDate the start date of the contract
     * @param endDate   the end date of the contract
     */
    Contract(Long id, String name, Position position, BigDecimal baseSalary, LocalDate startDate, LocalDate endDate) {
        super(id);
        this.name = name;
        this.position = position;
        this.baseSalary = baseSalary;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Gets the name of the contract.
     *
     * @return the name of the contract
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the contract.
     *
     * @param name the name to set for the contract
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the position of the employee under this contract.
     *
     * @return the position of the employee
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the position of the employee under this contract.
     *
     * @param position the position to set for the employee
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Gets the base salary of the employee under this contract.
     *
     * @return the base salary
     */
    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    /**
     * Sets the base salary for this contract.
     *
     * @param baseSalary the base salary to set
     */
    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    /**
     * Gets the start date of the contract.
     *
     * @return the start date of the contract
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the contract.
     *
     * @param startDate the start date to set
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date of the contract.
     *
     * @return the end date of the contract
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the contract.
     *
     * @param endDate the end date to set
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Returns the type of contract based on the specific implementation (Full-time or Part-time).
     *
     * @return the type of contract as a string
     */
    public String getType() {
        if (this instanceof ContractPartTime contractPartTime) {
            return contractPartTime.getContractType();
        }
        if (this instanceof ContractFullTime contractFullTime) {
            return contractFullTime.getContractType();
        }

        return "/";
    }
}
