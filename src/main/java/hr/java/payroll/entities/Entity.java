package hr.java.payroll.entities;

/**
 * Represents a base entity with a unique identifier.
 * This abstract class provides a common structure for all entities in the system.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public abstract class Entity {

    /** The unique identifier of the entity. */
    protected Long id;

    /**
     * Constructs an entity with the specified ID.
     *
     * @param id the unique identifier of the entity
     */
    protected Entity(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the entity.
     *
     * @return the unique identifier of the entity
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the entity.
     *
     * @param id the new unique identifier of the entity
     */
    public void setId(Long id) {
        this.id = id;
    }
}
