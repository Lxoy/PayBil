package hr.java.payroll.database;

import hr.java.payroll.entities.ContractPartTime;
import hr.java.payroll.enums.Position;
import hr.java.payroll.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for part-time contracts, including fetching, saving, editing, and deleting.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class ContractPartTimeDatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(ContractPartTimeDatabaseManager.class);
    private final DatabaseManager databaseManager;

    /**
     * Initializes the database manager.
     */
    public ContractPartTimeDatabaseManager() {
        this.databaseManager = new DatabaseManager();
    }

    /**
     * Retrieves all part-time contracts from the database.
     *
     * @return a list of part-time contracts.
     */
    public List<ContractPartTime> getAllPartTimeContracts() {
        String contractQuery = "SELECT ID, NAME, POSITION, START_DATE, END_DATE FROM CONTRACT";
        List<ContractPartTime> partTimeContracts = new ArrayList<>();

        try (Connection connection = databaseManager.connectToDatabase();
             Statement contractStatement = connection.createStatement()) {
            ResultSet contractResultSet = contractStatement.executeQuery(contractQuery);

            while (contractResultSet.next()) {
                Long id = contractResultSet.getLong("id");
                String name = contractResultSet.getString("name");
                Position position = Position.valueOf(contractResultSet.getString("position").toUpperCase());
                LocalDate startDate = contractResultSet.getDate("start_date").toLocalDate();
                LocalDate endDate = contractResultSet.getDate("end_date").toLocalDate();

                ContractPartTime contractPartTime = getPartTimeContractDetails(connection, id);

                if (contractPartTime != null) {
                    partTimeContracts.add(new ContractPartTime.ContractPartTimeBuilder()
                            .setId(id)
                            .setName(name)
                            .setPosition(position)
                            .setStartDate(startDate)
                            .setEndDate(endDate)
                            .setBaseSalary(contractPartTime.getHoursWorked(), contractPartTime.getHourlyRate())
                            .setHoursWorked(contractPartTime.getHoursWorked())
                            .setHourlyRate(contractPartTime.getHourlyRate())
                            .build());
                }
            }
            return partTimeContracts;
        } catch (SQLException | IOException e) {
            log.error("Error occurred while fetching part-time contracts.", e);
        }
        return partTimeContracts;
    }

    /**
     * Retrieves part-time contract details for a specific contract.
     *
     * @param connection the database connection.
     * @param contractId the ID of the contract.
     * @return the part-time contract details or null if not found.
     */
    private ContractPartTime getPartTimeContractDetails(Connection connection, Long contractId) {
        String contractPartTimeQuery = "SELECT HOURS_WORKED, HOURLY_RATE FROM CONTRACT_PART_TIME WHERE ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(contractPartTimeQuery)) {
            stmt.setLong(1, contractId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    BigDecimal hoursWorked = resultSet.getBigDecimal("hours_worked");
                    BigDecimal hourlyRate = resultSet.getBigDecimal("hourly_rate");
                    return new ContractPartTime.ContractPartTimeBuilder()
                            .setHoursWorked(hoursWorked)
                            .setHourlyRate(hourlyRate)
                            .build();
                }
            }
        } catch (SQLException e) {
            log.error("Error while fetching part-time contract details for ID: {}", contractId, e);
        }
        return null;
    }

    /**
     * Saves a part-time contract to the database.
     *
     * @param contractPartTime the contract to save.
     */
    public void savePartTimeContract(ContractPartTime contractPartTime) {
        String contractQuery = "INSERT INTO CONTRACT (NAME, POSITION, BASE_SALARY, START_DATE, END_DATE) VALUES(?, ?, ?, ?, ?)";
        String contractPartTimeQuery = "INSERT INTO CONTRACT_PART_TIME (ID, HOURS_WORKED, HOURLY_RATE) VALUES(?, ?, ?)";

        try (Connection connection = databaseManager.connectToDatabase();
             PreparedStatement contractPreparedStatement = connection.prepareStatement(contractQuery, Statement.RETURN_GENERATED_KEYS)) {
            contractPreparedStatement.setString(1, contractPartTime.getName());
            contractPreparedStatement.setString(2, contractPartTime.getPosition().name());
            contractPreparedStatement.setBigDecimal(3, contractPartTime.getBaseSalary());
            contractPreparedStatement.setDate(4,  java.sql.Date.valueOf(contractPartTime.getStartDate()));
            contractPreparedStatement.setDate(5, java.sql.Date.valueOf(contractPartTime.getEndDate()));
            contractPreparedStatement.executeUpdate();

            try (ResultSet contractResultSet = contractPreparedStatement.getGeneratedKeys()) {
                if (contractResultSet.next()) {
                    Long contractId = contractResultSet.getLong(1);
                    try(PreparedStatement partTimePreparedStatement = connection.prepareStatement(contractPartTimeQuery)){
                        partTimePreparedStatement.setLong(1, contractId);
                        partTimePreparedStatement.setBigDecimal(2, contractPartTime.getHoursWorked());
                        partTimePreparedStatement.setBigDecimal(3, contractPartTime.getHourlyRate());
                        partTimePreparedStatement.executeUpdate();
                    }
                } else{
                    log.error("Generated key not found while saving contract: {}", contractPartTime);
                    throw new SQLException("Generated key not found");
                }
            }
        } catch (SQLException | IOException e) {
            log.error("Error occurred while saving part-time contracts.", e);
        }
    }

    /**
     * Deletes a part-time contract from the database.
     *
     * @param contract the contract to delete.
     * @throws DatabaseException if the contract does not exist or an error occurs.
     */
    public void deleteContract(ContractPartTime contract) throws DatabaseException {
        String contractQuery = "DELETE FROM CONTRACT WHERE ID = ?";
        try (Connection connection = databaseManager.connectToDatabase();
             PreparedStatement contractPreparedStatement = connection.prepareStatement(contractQuery)) {

            contractPreparedStatement.setLong(1, contract.getId());
            int rowsAffected = contractPreparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new DatabaseException("No contract found with the provided ID.");
            }
        } catch (SQLException | IOException e) {
            throw new DatabaseException("Error accessing the database while deleting the contract.", e);
        }
    }

    /**
     * Edits an existing part-time contract in the database.
     *
     * @param contract the contract to edit.
     */
    public void editPartTimeContract(ContractPartTime contract) {
        String contractQuery = "UPDATE CONTRACT SET NAME = ?, POSITION = ?, BASE_SALARY = ?, START_DATE = ?, END_DATE = ? WHERE ID = ?";
        String contractPartTimeQuery = "UPDATE CONTRACT_PART_TIME SET HOURS_WORKED = ?, HOURLY_RATE = ? WHERE ID = ?";
        try (Connection connection = databaseManager.connectToDatabase();
             PreparedStatement contractPreparedStatement = connection.prepareStatement(contractQuery);
             PreparedStatement contractPartTimePreparedStatement = connection.prepareStatement(contractPartTimeQuery)) {

            contractPreparedStatement.setString(1, contract.getName());
            contractPreparedStatement.setString(2, contract.getPosition().name());
            contractPreparedStatement.setBigDecimal(3, contract.getBaseSalary());
            contractPreparedStatement.setDate(4,  java.sql.Date.valueOf(contract.getStartDate()));
            contractPreparedStatement.setDate(5,  java.sql.Date.valueOf(contract.getEndDate()));
            contractPreparedStatement.setLong(6, contract.getId());
            contractPreparedStatement.executeUpdate();
            contractPartTimePreparedStatement.setBigDecimal(1, contract.getHoursWorked());
            contractPartTimePreparedStatement.setBigDecimal(2, contract.getHourlyRate());
            contractPartTimePreparedStatement.setLong(3, contract.getId());
            contractPartTimePreparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            log.error("Error accessing the database while editing the part-time contract.", e);
        }
    }
}
