/**
 * Module for the Payroll Management System.
 * This module includes the core logic for managing payroll, handling employee data,
 * processing payments, and integrating with other services like email and databases.
 *
 * @version 1.0
 */
module hr.java.payroll.entities.payrollmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;
    requires java.sql;
    requires mail;
    requires org.slf4j;
    requires jdk.compiler;

    exports hr.java.payroll.main;
    exports hr.java.payroll.controller to javafx.fxml;

    opens hr.java.payroll.controller to javafx.fxml;
}