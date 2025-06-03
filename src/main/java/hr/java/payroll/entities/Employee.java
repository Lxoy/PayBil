package hr.java.payroll.entities;

import hr.java.payroll.enums.Gender;
import hr.java.payroll.enums.Role;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Represents an employee in the payroll system.
 * This class includes personal information such as name, email, date of birth, and gender,
 * as well as the employee's contract and role in the organization.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class Employee extends Entity {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final LocalDate dateOfBirth;
    private final Gender gender;
    private final Contract contract;
    private final Role role;

    /**
     * Private constructor for {@link Employee} using the builder pattern.
     *
     * @param builder the builder used to create the employee
     */
    private Employee(EmployeeBuilder builder) {
        super(builder.id);
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.password = builder.password;
        this.dateOfBirth = builder.dateOfBirth;
        this.gender = builder.gender;
        this.contract = builder.contract;
        this.role = builder.role;
    }

    /**
     * Builder class for constructing an {@link Employee} object.
     */
    public static class EmployeeBuilder {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private LocalDate dateOfBirth;
        private Gender gender;
        private Contract contract;
        private Role role;

        /**
         * Sets the ID for the employee.
         *
         * @param id the unique identifier of the employee
         * @return the builder object
         */
        public EmployeeBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the first name of the employee.
         *
         * @param firstName the first name of the employee
         * @return the builder object
         */
        public EmployeeBuilder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        /**
         * Sets the last name of the employee.
         *
         * @param lastName the last name of the employee
         * @return the builder object
         */
        public EmployeeBuilder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        /**
         * Sets the email address of the employee.
         *
         * @param email the email address of the employee
         * @return the builder object
         */
        public EmployeeBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        /**
         * Sets the password for the employee.
         *
         * @param password the password for the employee
         * @return the builder object
         */
        public EmployeeBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * Sets the date of birth of the employee.
         *
         * @param dateOfBirth the date of birth of the employee
         * @return the builder object
         */
        public EmployeeBuilder setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        /**
         * Sets the gender of the employee.
         *
         * @param gender the gender of the employee
         * @return the builder object
         */
        public EmployeeBuilder setGender(Gender gender) {
            this.gender = gender;
            return this;
        }

        /**
         * Sets the contract for the employee.
         *
         * @param contract the contract of the employee
         * @return the builder object
         */
        public EmployeeBuilder setContract(Contract contract) {
            this.contract = contract;
            return this;
        }

        /**
         * Sets the role of the employee.
         *
         * @param role the role of the employee
         * @return the builder object
         */
        public EmployeeBuilder setRole(Role role) {
            this.role = role;
            return this;
        }

        /**
         * Builds an {@link Employee} object using the specified properties.
         *
         * @return the created {@link Employee} object
         */
        public Employee build() {
            return new Employee(this);
        }
    }

    /**
     * Compares this employee to another object for equality.
     * Two employees are considered equal if they have the same email address.
     *
     * @param o the object to compare this employee to
     * @return true if the employee is equal to the specified object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(email, employee.email);
    }

    /**
     * Computes the hash code for this employee based on their email address.
     *
     * @return the hash code for this employee
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    /**
     * Gets the first name of the employee.
     *
     * @return the first name of the employee
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name of the employee.
     *
     * @return the last name of the employee
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the email address of the employee.
     *
     * @return the email address of the employee
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the password of the employee.
     *
     * @return the password of the employee
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the contract of the employee.
     *
     * @return the contract of the employee
     */
    public Contract getContract() {
        return contract;
    }

    /**
     * Gets the date of birth of the employee.
     *
     * @return the date of birth of the employee
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Calculates the age of the employee based on their date of birth.
     *
     * @return the age of the employee in years
     */
    public Integer getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Gets the gender of the employee.
     *
     * @return the gender of the employee
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Gets the role of the employee.
     *
     * @return the role of the employee
     */
    public Role getRole() {
        return role;
    }

    /**
     * Gets the gross salary of the employee.
     * The gross salary is calculated based on the employee's contract.
     *
     * @return the gross salary of the employee
     */
    public BigDecimal getGrossSalary() {
        return contract.calculateGrossSalary();
    }

    /**
     * Gets the net salary of the employee.
     * The net salary is calculated based on the employee's contract after tax deductions.
     *
     * @return the net salary of the employee
     */
    public BigDecimal getNetSalary() {
        return contract.calculateNetSalary();
    }
}
