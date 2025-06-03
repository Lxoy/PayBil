package hr.java.payroll.utils;

import hr.java.payroll.entities.Employee;
import hr.java.payroll.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * This class contains static variables and constants used throughout the payroll system.
 * These variables include configuration settings, regular expressions, and other system constants.
 * It also handles the loading of email configuration properties.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class Var {
    private static final Logger log = LoggerFactory.getLogger(Var.class);

    public static final BigDecimal MINIMAL_SALARY = BigDecimal.valueOf(970);
    public static final BigDecimal LOWER_TAX_RATE = BigDecimal.valueOf(0.2);
    public static final BigDecimal HIGHER_TAX_RATE = BigDecimal.valueOf(0.3);
    public static final BigDecimal TAX_THRESHOLD = BigDecimal.valueOf(5000);

    public static final String INFO_TITLE = "System Notification";
    public static final String ERROR_TITLE = "System Error";
    public static final String WARNING_TITLE = "System Warning";
    public static final String CONFIRM_TITLE = "System Confirmation";

    public static final String FIELDS_MUST_BE_FILLED = "All fields must be filled.";

    public static final DateTimeFormatter SERIALIZABLE_DTF = DateTimeFormatter.ofPattern("dd. MM. yyyy HH:mm:ss");
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd. MM. yyyy");

    public static final String EMPLOYEE_LOGIN_FILE_PATH = "dat/users.txt";
    public static final String BINARY_CHANGES_FILE_PATH = "dat/changes.dat";

    public static final String EMPLOYEE_ADMIN_USERNAME = "admin";
    public static final String EMPLOYEE_ADMIN_PASSWORD = "admin";
    public static final Employee ADMIN = new Employee.EmployeeBuilder()
            .setEmail(EMPLOYEE_ADMIN_USERNAME)
            .setRole(Role.ADMIN)
            .build();


    private static final Properties properties = new Properties();

    static {
        try (FileReader reader = new FileReader("email.properties")) {
            properties.load(reader);
        } catch (IOException e) {
            log.error("File not found: {}", e.getMessage());
        }
    }

    public static final String EMAIL_USERNAME = properties.getProperty("username");
    public static final String EMAIL_THING = properties.getProperty("password");

    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!€]).{8,}$";
    public static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public static final Map<Predicate<String>, String> RULES = Map.of(
            p -> p == null, "Password cannot be null!",
            p -> !p.matches(".*[A-Z].*"), "Password must contain at least one uppercase letter!",
            p -> !p.matches(".*[a-z].*"), "Password must contain at least one lowercase letter!",
            p -> !p.matches(".*\\d.*"), "Password must contain at least one digit!",
            p -> !p.matches(".*[@#$%^&+=!€].*"), "Password must contain at least one special character (@#$%^&+=!€)!",
            p -> p.length() < 8, "Password must be at least 8 characters long!"
    );

    public static final Map<String, String> SMTPSERVERS = Map.of(
            "gmail.com", "smtp.gmail.com",
            "yahoo.com", "smtp.mail.yahoo.com",
            "office365.com", "smtp.office365.com",
            "tvz.hr", "smtp.office365.com"
    );

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Var() {}
}
