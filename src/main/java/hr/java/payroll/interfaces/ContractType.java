package hr.java.payroll.interfaces;

import hr.java.payroll.entities.ContractFullTime;
import hr.java.payroll.entities.ContractPartTime;

/**
 * Represents a contract type in the payroll system.
 * This sealed interface is extended by {@link ContractFullTime} and {@link ContractPartTime}.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public sealed interface ContractType permits ContractFullTime, ContractPartTime {
    /**
     * Returns the type of the contract.
     *
     * @return the contract type as a string
     */
    String getContractType();
}
