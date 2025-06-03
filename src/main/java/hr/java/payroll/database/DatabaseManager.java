package hr.java.payroll.database;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * Manages database connections by reading connection details from a properties file.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class DatabaseManager {
    /**
     * Establishes a connection to the database using credentials from a properties file.
     *
     * @return a {@link Connection} object representing the database connection.
     * @throws IOException if there is an issue reading the properties file.
     * @throws SQLException if there is an issue with the database connection.
     */
    public Connection connectToDatabase() throws IOException, SQLException {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("database.properties")) {
            properties.load(reader);
        }
        return DriverManager.getConnection(properties.getProperty("databaseUrl"),
                properties.getProperty("username"), properties.getProperty("password"));
    }
}
