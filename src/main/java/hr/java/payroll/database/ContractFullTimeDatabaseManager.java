package hr.java.payroll.database;

import hr.java.payroll.entities.ContractFullTime;
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
 * This class manages interactions with the database for full-time contracts.
 * It provides methods to retrieve, save, edit, and delete full-time contracts.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class ContractFullTimeDatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(ContractFullTimeDatabaseManager.class);
    private final DatabaseManager databaseManager;

    /**
     * Initializes a new instance of the ContractFullTimeDatabaseManager.
     */
    public ContractFullTimeDatabaseManager() {
        this.databaseManager = new DatabaseManager();
    }

    /**
     * Retrieves all full-time contracts from the database.
     *
     * @return A list of ContractFullTime objects representing all full-time contracts in the database.
     */
    public List<ContractFullTime> getAllFullTimeContracts() {
        String contractQuery = "SELECT ID, NAME, POSITION, BASE_SALARY, START_DATE, END_DATE FROM CONTRACT";
        List<ContractFullTime> fullTimeContracts = new ArrayList<>();

        try (Connection connection = databaseManager.connectToDatabase();
             Statement contractStatement = connection.createStatement()) {
            ResultSet contractResultSet = contractStatement.executeQuery(contractQuery);

            while (contractResultSet.next()) {
                Long id = contractResultSet.getLong("id");
                String name = contractResultSet.getString("name");
                Position position = Position.valueOf(contractResultSet.getString("position").toUpperCase());
                BigDecimal baseSalary = contractResultSet.getBigDecimal("base_salary");
                LocalDate startDate = contractResultSet.getDate("start_date").toLocalDate();
                LocalDate endDate = contractResultSet.getDate("end_date").toLocalDate();

                BigDecimal bonus = getBonusForContract(connection, id);

                fullTimeContracts.add(new ContractFullTime.ContractFullTimeBuilder()
                        .setId(id)
                        .setName(name)
                        .setPosition(position)
                        .setBaseSalary(baseSalary)
                        .setStartDate(startDate)
                        .setEndDate(endDate)
                        .setBonus(bonus)
                        .build());
            }
            return fullTimeContracts;
        } catch (SQLException | IOException e) {
            log.error("Error occurred while fetching full-time contracts.", e);
        }
        return fullTimeContracts;
    }

    /**
     * Retrieves the bonus for a given contract from the database.
     *
     * @param connection The database connection.
     * @param contractId The ID of the contract.
     * @return The bonus amount for the contract.
     */
    private BigDecimal getBonusForContract(Connection connection, Long contractId) {
        String contractFullTimeQuery = "SELECT BONUS FROM CONTRACT_FULL_TIME WHERE ID = ?";
        try (PreparedStatement restaurantChefStmt = connection.prepareStatement(contractFullTimeQuery)) {
            restaurantChefStmt.setLong(1, contractId);
            try (ResultSet restaurantChefResultSet = restaurantChefStmt.executeQuery()) {
                if (restaurantChefResultSet.next()) {
                    return restaurantChefResultSet.getBigDecimal("bonus");
                }
            }
        } catch (SQLException e) {
            log.error("Error while fetching bonus for contract ID: {}", contractId, e);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Saves a full-time contract to the database.
     *
     * @param contractFullTime The contract to be saved.
     */
    public void saveFullTimeContract(ContractFullTime contractFullTime) {
        String contractQuery = "INSERT INTO CONTRACT (NAME, POSITION, BASE_SALARY, START_DATE, END_DATE) VALUES(?, ?, ?, ?, ?)";
        String contractFullTimeQuery = "INSERT INTO CONTRACT_FULL_TIME (ID, BONUS) VALUES(?, ?)";

        try (Connection connection = databaseManager.connectToDatabase();
             PreparedStatement contractPreparedStatement = connection.prepareStatement(contractQuery, Statement.RETURN_GENERATED_KEYS)) {
            contractPreparedStatement.setString(1, contractFullTime.getName());
            contractPreparedStatement.setString(2, contractFullTime.getPosition().name());
            contractPreparedStatement.setBigDecimal(3, contractFullTime.getBaseSalary());
            contractPreparedStatement.setDate(4,  java.sql.Date.valueOf(contractFullTime.getStartDate()));
            contractPreparedStatement.setDate(5, java.sql.Date.valueOf(contractFullTime.getEndDate()));
            contractPreparedStatement.executeUpdate();

            try (ResultSet contractResultSet = contractPreparedStatement.getGeneratedKeys()) {
                if (contractResultSet.next()) {
                    Long contractId = contractResultSet.getLong(1);
                    try(PreparedStatement fullTimePreparedStatement = connection.prepareStatement(contractFullTimeQuery)){
                        fullTimePreparedStatement.setLong(1, contractId);
                        fullTimePreparedStatement.setBigDecimal(2, contractFullTime.getBonus());
                        fullTimePreparedStatement.executeUpdate();
                    }
                } else{
                    log.error("Generated key not found while saving contract: {}", contractFullTime);
                    throw new SQLException("Generated key not found");
                }
            }
        } catch (SQLException | IOException e) {
            log.error("Error occurred while saving full-time contracts.", e);
        }
    }

    /**
     * Deletes a full-time contract from the database.
     *
     * @param contract The contract to be deleted.
     * @throws DatabaseException if no contract is found with the provided ID.
     */
    public void deleteContract(ContractFullTime contract) {
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
     * Edits an existing full-time contract in the database.
     *
     * @param contract The contract with updated information.
     */
    public void editFullTimeContract(ContractFullTime contract) {
        String contractQuery = "UPDATE CONTRACT SET NAME = ?, POSITION = ?, BASE_SALARY = ?, START_DATE = ?, END_DATE = ? WHERE ID = ?";
        String contractFullTimeQuery = "UPDATE CONTRACT_FULL_TIME SET BONUS = ? WHERE ID = ?";
        try (Connection connection = databaseManager.connectToDatabase();
             PreparedStatement contractPreparedStatement = connection.prepareStatement(contractQuery);
             PreparedStatement contractFullTimePreparedStatement = connection.prepareStatement(contractFullTimeQuery)) {

            contractPreparedStatement.setString(1, contract.getName());
            contractPreparedStatement.setString(2, contract.getPosition().name());
            contractPreparedStatement.setBigDecimal(3, contract.getBaseSalary());
            contractPreparedStatement.setDate(4,  java.sql.Date.valueOf(contract.getStartDate()));
            contractPreparedStatement.setDate(5,  java.sql.Date.valueOf(contract.getEndDate()));
            contractPreparedStatement.setLong(6, contract.getId());
            contractPreparedStatement.executeUpdate();
            contractFullTimePreparedStatement.setBigDecimal(1, contract.getBonus());
            contractFullTimePreparedStatement.setLong(2, contract.getId());
            contractFullTimePreparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            log.error("Error accessing the database while editing the full-time contract.", e);
        }
    }
}
