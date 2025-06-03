package hr.java.payroll.utils;

import hr.java.payroll.entities.Employee;
import hr.java.payroll.exception.InvalidEmailException;
import hr.java.payroll.exception.InvalidInputException;
import hr.java.payroll.exception.MissingRequiredFieldException;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Utility class for input validation and password hashing.
 * Provides methods to validate strings, BigDecimal, integers, and email addresses, as well as password hashing.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class InputCheck {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private InputCheck() {}

    /**
     * Checks if the provided string contains any numeric characters.
     * Throws an {@link InvalidInputException} if the string contains numbers.
     *
     * @param stringInput the string to be checked
     * @param errorMessage the error message to be passed if validation fails
     */
    public static void stringCheck(String stringInput, String errorMessage) {
        if (stringInput.matches(".*\\d.*")) {
            throw new InvalidInputException(errorMessage);
        }
    }

    /**
     * Checks if the provided string can be parsed as a {@link BigDecimal}.
     * Throws an {@link InvalidInputException} if the string cannot be parsed into a BigDecimal.
     *
     * @param stringInput the string to be checked
     * @param errorMessage the error message to be passed if validation fails
     */
    public static void bigDecimalCheck(String stringInput, String errorMessage) {
        try {
            if(!stringInput.isEmpty()) {
                new BigDecimal(stringInput);
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException(errorMessage);
        }
    }

    /**
     * Checks if the provided string can be parsed as an integer.
     * Throws an {@link InvalidInputException} if the string cannot be parsed into an integer.
     *
     * @param stringInput the string to be checked
     * @param errorMessage the error message to be passed if validation fails
     */
    public static void integerCheck(String stringInput, String errorMessage) {
        try {
            if(!stringInput.isEmpty()) {
                Integer.parseInt(stringInput);
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException(errorMessage);
        }
    }

    /**
     * Validates the provided email address against a set of employees and ensures it is not empty.
     * Throws an {@link InvalidEmailException} if the email is not found in the system.
     * Throws a {@link MissingRequiredFieldException} if the email is null or empty.
     *
     * @param email the email address to be validated
     * @param employees the set of employees to check for existing email addresses
     * @param errorMessage1 the error message to be passed if the email is invalid
     * @throws InvalidEmailException if the email does not exist in the system
     * @throws MissingRequiredFieldException if the email is missing or empty
     */
    public static void emailCheck(String email, Set<Employee> employees, String errorMessage1) throws InvalidEmailException, MissingRequiredFieldException {
        Validator.nullInput(email, errorMessage1);

        boolean emailNotFound = !email.equals(Var.EMPLOYEE_ADMIN_USERNAME);
        if(emailNotFound){
            for (Employee employee : employees) {
                if(employee.getEmail().equals(email)) {
                    emailNotFound = false;
                    break;
                }
            }
        }
        if (emailNotFound) {
            throw new InvalidEmailException("There is no account with that email");
        }
    }

    /**
     * Hashes the provided password using BCrypt hashing algorithm.
     *
     * @param password the password to be hashed
     * @return the hashed password
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
}
