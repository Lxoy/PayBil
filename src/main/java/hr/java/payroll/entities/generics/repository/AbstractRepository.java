package hr.java.payroll.entities.generics.repository;

import hr.java.payroll.entities.Entity;

import java.util.List;
import java.util.Set;

/**
 * Abstract class for repositories that handle entities of type T.
 * Provides methods for saving entities and acquiring data.
 *
 * @param <T> the type of entity, which extends {@link Entity}.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public abstract class AbstractRepository<T extends Entity> {
    /**
     * Saves a set of entities.
     *
     * @param entities a set of entities to be saved.
     */
    abstract void save(Set<T> entities);

    /**
     * Acquires a list of strings representing data from the repository.
     *
     * @return a list of strings representing the data.
     */
    abstract List<String> acquire();
}
