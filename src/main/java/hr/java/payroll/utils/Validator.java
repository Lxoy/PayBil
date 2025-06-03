package hr.java.payroll.utils;

import hr.java.payroll.entities.Employee;
import hr.java.payroll.entities.serializer.ChangeLog;
import hr.java.payroll.entities.serializer.ChangeLogSerializer;
import hr.java.payroll.exception.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

/**
 * Utility class for input validation and data formatting in the payroll system.
 * Provides methods to validate various inputs such as null values, strings, emails, and passwords.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class Validator {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Validator() {}

    /**
     * Checks if the provided input is null or blank (for strings), and throws a {@link MissingRequiredFieldException} if true.
     *
     * @param input the input to check
     * @param errorMessage the error message to include in the exception if validation fails
     */
    public static void nullInput(Object input, String errorMessage) {
        if (input == null) {
            throw new MissingRequiredFieldException(errorMessage);
        }
        if (input instanceof String stringInstance && stringInstance.isBlank()){
                throw new MissingRequiredFieldException(errorMessage);
            }
    }

    /**
     * Checks if the provided input is null or blank (for strings) and throws a {@link MissingRequiredFieldException}.
     * Uses a default error message defined in {@link Var#FIELDS_MUST_BE_FILLED}.
     *
     * @param input the input to check
     */
    public static void nullInput(Object input) {
        if (input == null) {
            throw new MissingRequiredFieldException(Var.FIELDS_MUST_BE_FILLED);
        }
        if (input instanceof String stringInstance && stringInstance.isBlank()){
            throw new MissingRequiredFieldException(Var.FIELDS_MUST_BE_FILLED);
        }
    }

    /**
     * Validates if the provided string input contains any digits and throws an {@link InvalidInputException} if it does.
     *
     * @param stringInput the string input to validate
     * @param errorMessage the error message to include in the exception if validation fails
     * @throws MissingRequiredFieldException if the input is null or blank
     * @throws InvalidInputException if the input contains digits
     */
    public static void stringInput(Object stringInput, String errorMessage) throws MissingRequiredFieldException {
        Validator.nullInput(stringInput);
        if (((String) stringInput).matches(".*\\d.*")) {
            throw new InvalidInputException(errorMessage);
        }
    }

    /**
     * Validates the email format and checks if the email is already used by another employee.
     * Throws an {@link InvalidEmailException} if the email is invalid or already in use.
     *
     * @param email the email to validate
     * @param employees the set of existing employees to check for duplicate emails
     * @param errorMessage the error message to include in the exception if validation fails
     * @throws InvalidEmailException if the email is invalid or already in use
     * @throws MissingRequiredFieldException if the email is null or blank
     */
    public static void emailValidator(Object email, Set<Employee> employees, String errorMessage) throws InvalidEmailException, MissingRequiredFieldException {
        Validator.nullInput(email, Var.FIELDS_MUST_BE_FILLED);
        if(!Var.EMAIL_PATTERN.matcher(((String)email)).matches()){
            throw new InvalidEmailException(errorMessage);
        }
        List<String> employeeEmails = employees.stream().map(Employee::getEmail).toList();
        for(String employeeMail : employeeEmails){
            if(((String)email).equals(employeeMail)){
                throw new InvalidEmailException("That email is already in use.");
            }
        }
    }

    /**
     * Validates the password format and checks if it meets the specified rules.
     * Throws an {@link InvalidPasswordException} if the password is invalid.
     *
     * @param password the password to validate
     * @param errorMessage the error message to include in the exception if validation fails
     * @throws InvalidPasswordException if the password is invalid
     * @throws MissingRequiredFieldException if the password is null or blank
     */
    public static void passwordValidator(Object password, String errorMessage) throws InvalidPasswordException, MissingRequiredFieldException {
        Validator.nullInput(password, Var.FIELDS_MUST_BE_FILLED);
        for (var rule : Var.RULES.entrySet()) {
            if (rule.getKey().test(((String)password))) {
                throw new InvalidPasswordException(rule.getValue());
            }
        }
        if (!Var.PASSWORD_PATTERN.matcher(((String)password)).matches()) {
            throw new InvalidPasswordException(errorMessage);
        }
    }

    /**
     * Checks if the given date of birth is valid.
     * Ensures the date is not null and that the person is at least 16 years old.
     * Throws an exception if the date is missing or if the age requirement is not met.
     *
     * @param dateOfBirth   the date of birth to validate
     * @param errorMessage  the error message to use if the age is below 16
     * @throws MissingRequiredFieldException if the date of birth is null
     * @throws InvalidInputException if the person is younger than 16
     */
    public static void dateOfBirthValidator(Object dateOfBirth, String errorMessage) throws MissingRequiredFieldException {
        Validator.nullInput(dateOfBirth, Var.FIELDS_MUST_BE_FILLED);
        if (dateOfBirth instanceof LocalDate dob) {
            long age = ChronoUnit.YEARS.between(dob, LocalDate.now());
            if (age < 16) {
                throw new InvalidInputException(errorMessage);
            }
        }
    }

    /**
     * Validates that the contract start date is not after the end date.
     *
     * @param startDate   the start date of the contract
     * @param endDate     the end date of the contract
     * @param errorMessage the error message to be thrown if validation fails
     * @throws InvalidInputException if the start date is after the end date
     */
    public static void contractDate(LocalDate startDate, LocalDate endDate, String errorMessage) {
        if(startDate.isAfter(endDate)){
            throw new InvalidInputException(errorMessage);
        }
    }

    /**
     * Validates the base salary value.
     *
     * @param baseSalary   the base salary as an Object (expected to be a String)
     * @param errorMessage the error message to be thrown if validation fails
     * @throws InvalidInputException if the salary is negative or exceeds the defined minimal salary
     */
    public static void baseSalary(Object baseSalary, String errorMessage) {
        Validator.nullInput(baseSalary, Var.FIELDS_MUST_BE_FILLED);
        try {
            BigDecimal value = new BigDecimal(((String)baseSalary));
            if (value.compareTo(Var.MINIMAL_SALARY) < 0) {
                throw new InvalidInputException("Base salary must be greater than " + Var.MINIMAL_SALARY);
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException(errorMessage);
        }
    }

    /**
     * Validates the bonus amount.
     *
     * @param bonus        the bonus value as an Object (expected to be a String)
     * @param errorMessage the error message to be thrown if validation fails
     * @throws MissingRequiredFieldException if the bonus field is null
     * @throws InvalidInputException if the bonus is negative or exceeds the limit of 99,999
     */
    public static void bonus(Object bonus, String errorMessage) throws MissingRequiredFieldException {
        Validator.nullInput(bonus, Var.FIELDS_MUST_BE_FILLED);
        try {
            BigDecimal value = new BigDecimal(((String)bonus));
            if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.valueOf(100000)) >= 0) {
                throw new InvalidInputException("Bonus must be between 0 and 99999");
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException(errorMessage);
        }
    }

    /**
     * Validates the number of hours worked.
     *
     * @param hoursWorked  the hours worked as an Object (expected to be a String)
     * @param errorMessage the error message to be thrown if validation fails
     * @throws InvalidInputException if the hours worked are negative or exceed 140
     */
    public static void hoursWorked(Object hoursWorked, String errorMessage) {
        Validator.nullInput(hoursWorked, Var.FIELDS_MUST_BE_FILLED);
        try {
            BigDecimal value = new BigDecimal(((String)hoursWorked));
            if (value.compareTo(BigDecimal.valueOf(0)) < 0 || value.compareTo(BigDecimal.valueOf(140)) > 0) {
                throw new InvalidInputException("Hours worked must be between 0 and 140");
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException(errorMessage);
        }
    }


    /**
     * Validates the hourly rate.
     *
     * @param hourlyRate   the hourly rate as an Object (expected to be a String)
     * @param errorMessage the error message to be thrown if validation fails
     * @throws InvalidInputException if the hourly rate is negative or exceeds 99
     */
    public static void hourlyRate(Object hourlyRate, String errorMessage) {
        Validator.nullInput(hourlyRate, Var.FIELDS_MUST_BE_FILLED);
        try {
            BigDecimal value = new BigDecimal(((String)hourlyRate));
            if (value.compareTo(BigDecimal.valueOf(0)) < 0 || value.compareTo(BigDecimal.valueOf(99)) > 0) {
                throw new InvalidInputException("Hourly rate must be between 0 and 99");
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException(errorMessage);
        }
    }

    /**
     * Formats a {@link LocalDate} to a string in the "dd/MM/yyyy" format.
     *
     * @param date the date to format
     * @return the formatted date as a string
     */
    public static String changeDateFormatDDMMYYYY(LocalDate date) {
        return date.format(Var.DTF);
    }

    /**
     * Serializes a change log entry if the data has been edited.
     *
     * @param fieldChanged the name of the field that has changed
     * @param oldValue the previous value of the field
     * @param newValue the new value of the field
     */
    public static void isDataEdited(String fieldChanged, Object oldValue, Object newValue){
        if (oldValue != null && newValue != null && !oldValue.toString().equals(newValue.toString())) {
            ChangeLogSerializer.serializeChange(new ChangeLog(fieldChanged, oldValue.toString(), newValue.toString(), EmployeeSession.getInstance().getLoggedInEmployee().getRole().name(), LocalDateTime.now()));
        }
    }

}
