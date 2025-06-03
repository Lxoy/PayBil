package hr.java.payroll.controller;

import hr.java.payroll.database.ContractFullTimeDatabaseManager;
import hr.java.payroll.database.ContractPartTimeDatabaseManager;
import hr.java.payroll.database.EmployeeDatabaseManager;
import hr.java.payroll.database.PayrollDatabaseManager;
import hr.java.payroll.entities.*;
import hr.java.payroll.exception.DatabaseException;
import hr.java.payroll.utils.Var;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * Controller class for generating and sending payslips for employees.
 * It interacts with the database to fetch employee, contract, and payroll data,
 * and handles the generation of payslips, saving them to the database,
 * and sending them by email.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class GeneratePayslipController {
    private static final Logger log = LoggerFactory.getLogger(GeneratePayslipController.class);
    PayrollDatabaseManager payrollDatabaseManager = new PayrollDatabaseManager();
    ContractPartTimeDatabaseManager contractPartTimeDatabaseManager = new ContractPartTimeDatabaseManager();
    ContractFullTimeDatabaseManager contractFullTimeDatabaseManager = new ContractFullTimeDatabaseManager();
    EmployeeDatabaseManager employeeDatabaseManager = new EmployeeDatabaseManager();

    @FXML private Label numberOfPayslipsTextField;

    /**
     * Initializes the controller by setting up a timeline to update the payslip count every second.
     */
    public void initialize() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updatePayslipCount()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Updates the displayed count of generated payslips by fetching the data from the payroll history database.
     */
    private void updatePayslipCount() {
        try {
            List<Payslip> payslips = payrollDatabaseManager.getPayrollHistory();
            int count = payslips.size();

            Platform.runLater(() -> numberOfPayslipsTextField.setText("Number of payslips generated so far: " + count));
        } catch (DatabaseException e) {
            log.error("Error occurred while fetching payslips from database: ", e);
        }
    }

    /**
     * Generates payslips for all employees and saves them to the database and sends them via email.
     * It runs in separate threads for generating payslips, saving them to the database, and sending emails.
     */
    public void generateAndSend() {
        CountDownLatch payslipGenerated = new CountDownLatch(1);

        List<? extends Contract> contracts = Stream.concat(
                contractPartTimeDatabaseManager.getAllPartTimeContracts().stream(),
                contractFullTimeDatabaseManager.getAllFullTimeContracts().stream()
        ).toList();

        Set<Employee> employees = employeeDatabaseManager.getEmployees((List<Contract>) contracts);
        AtomicReference<List<Payslip>> payslips = new AtomicReference<>();

        Thread generateThread = new Thread(() -> {
            try {
                List<Payslip> generatedPayslips = generatePayslips(employees);
                payslips.set(generatedPayslips);
                payslipGenerated.countDown();
            } catch (Exception e) {
                log.error("Error while generating payslips", e);
            }
        });

        Thread saveThread = new Thread(() -> {
            try {
                payslipGenerated.await();
                savePayslipsToDatabase(payslips.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted while waiting to save payslips", e);
            }
        });

        Thread emailThread = new Thread(() -> {
            try {
                payslipGenerated.await();
                sendPayslipsByEmail(payslips.get(), employees);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted while waiting to send emails", e);
            }
        });
        hr.java.payroll.utils.Message.showAlert(Alert.AlertType.INFORMATION, Var.INFO_TITLE, "", "Generating payslips was successfully completed.");
        generateThread.start();
        saveThread.start();
        emailThread.start();
    }

    /**
     * Generates a list of payslips for a set of employees.
     * The payslips are generated based on the type of contract (full-time or part-time) of each employee.
     *
     * @param employees A set of employees for whom the payslips are to be generated.
     * @return A list of generated payslips.
     */
    private List<Payslip> generatePayslips(Set<Employee> employees) {
        Long payslipID = 0L;
        LocalDate now = LocalDate.now();
        YearMonth previousMonth = YearMonth.from(now.minusMonths(1));
        List<Payslip> payslips = new ArrayList<>();

        for (Employee employee : employees) {
            payslipID++;
            Payslip payslip;
            if (employee.getContract() instanceof ContractFullTime contractFullTime) {
                payslip = new Payslip(payslipID, employee.getId(), employee.getGrossSalary(), employee.getNetSalary(),
                        contractFullTime.getBonus(), new BigDecimal(140), previousMonth, now);
                payslips.add(payslip);
            }
            if(employee.getContract() instanceof ContractPartTime contractPartTime) {
                payslip = new Payslip(payslipID++, employee.getId(), employee.getGrossSalary(), employee.getNetSalary(),
                        BigDecimal.ZERO, contractPartTime.getHoursWorked(), previousMonth, now);
                payslips.add(payslip);
            }
        }
        return payslips;
    }

    /**
     * Saves the generated payslips to the database.
     *
     * @param payslips The list of payslips to be saved.
     */
    private void savePayslipsToDatabase(List<Payslip> payslips) {
        for (Payslip payslip : payslips) {
            payrollDatabaseManager.savePayrollHistory(payslip);
        }
    }

    /**
     * Sends the generated payslips via email to the respective employees.
     * Each employee will receive an email containing their payslip information.
     *
     * @param payslips The list of generated payslips to be sent.
     * @param employees A set of employees to whom the payslips will be sent.
     */
    private void sendPayslipsByEmail(List<Payslip> payslips, Set<Employee> employees) {
        for (Payslip payslip : payslips) {
            Optional<Employee> sendToEmployee = employees.stream()
                    .filter(e -> e.getId().compareTo(payslip.employeeId()) == 0)
                    .findFirst();
            sendToEmployee.ifPresent(employee -> sendEmail(employee.getEmail(), "Payslip for " + payslip.payrollPeriod(), payslip.toString()));
        }
    }

    /**
     * Sends an email with the specified subject and message to the specified recipient.
     *
     * @param to The recipient's email address.
     * @param subject The subject of the email.
     * @param message The body of the email.
     */
    private void sendEmail(String to, String subject, String message) {
        String from = Var.EMAIL_USERNAME;
        String domain = to.split("@")[1];
        String smtpServer = Var.SMTPSERVERS.get(domain);
        if (smtpServer != null) {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(Var.EMAIL_USERNAME, Var.EMAIL_THING);
                        }
                    });

            try {
                Message mail = new MimeMessage(session);
                mail.setFrom(new InternetAddress(from));
                mail.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                mail.setSubject(subject);
                mail.setText(message);
                Transport.send(mail);
            } catch (MessagingException e) {
                log.error("Error occurred while sending email to: {}", to, e);
            }
        }
        else{
            log.error("No stmp server found for: {}", domain);
        }
    }
}
