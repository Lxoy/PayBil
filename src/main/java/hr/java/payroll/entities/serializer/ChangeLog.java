package hr.java.payroll.entities.serializer;

import hr.java.payroll.utils.Var;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A class representing a change log entry.
 * It stores information about a specific field change, including the old and new values, the role of the user making the change,
 * and the date and time of the change.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class ChangeLog implements Serializable {
    /**
     * The name of the field that was changed.
     */
    private String fieldChanged;

    /**
     * The previous value of the field.
     */
    private String oldValue;

    /**
     * The new value of the field.
     */
    private String newValue;

    /**
     * The role of the user who made the change.
     */
    private String role;

    /**
     * The date and time when the change was made.
     */
    private LocalDateTime changeDateTime;

    /**
     * Constructs a new ChangeLog entry with the specified details.
     *
     * @param fieldChanged the name of the field that was changed.
     * @param oldValue the previous value of the field.
     * @param newValue the new value of the field.
     * @param role the role of the user who made the change.
     * @param changeDateTime the date and time the change occurred.
     */
    public ChangeLog(String fieldChanged, String oldValue, String newValue, String role, LocalDateTime changeDateTime) {
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.role = role;
        this.changeDateTime = changeDateTime;
    }

    /**
     * Returns the name of the field that was changed.
     *
     * @return the name of the field that was changed.
     */
    public String getFieldChanged() {
        return fieldChanged;
    }

    /**
     * Returns the previous value of the changed field.
     *
     * @return the old value of the field.
     */
    public String getOldValue() {
        return oldValue;
    }

    /**
     * Returns the new value of the changed field.
     *
     * @return the new value of the field.
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * Returns the role of the user who made the change.
     *
     * @return the role of the user.
     */
    public String getRole() {
        return role;
    }

    /**
     * Returns the date and time of the change.
     *
     * @return the date and time the change occurred.
     */
    public LocalDateTime getChangeDateTime() {
        return changeDateTime;
    }

    /**
     * Returns the formatted date and time of the change, according to a predefined format.
     *
     * @return the formatted date and time of the change.
     */
    public String getFormattedDateTime() {
        return changeDateTime.format(Var.SERIALIZABLE_DTF);
    }
}
