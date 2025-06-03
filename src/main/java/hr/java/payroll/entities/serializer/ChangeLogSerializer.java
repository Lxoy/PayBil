package hr.java.payroll.entities.serializer;

import hr.java.payroll.utils.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for serializing and deserializing change log entries.
 * This class handles writing and reading the list of change logs to and from a binary file.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class ChangeLogSerializer {
    private static final Logger log = LoggerFactory.getLogger(ChangeLogSerializer.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ChangeLogSerializer() {}

    /**
     * Serializes the given change log entry and appends it to the existing list of changes in the binary file.
     *
     * @param change the ChangeLog entry to be serialized and added to the file.
     */
    public static void serializeChange(ChangeLog change) {
        List<ChangeLog> existingChanges = deserializeChanges();
        existingChanges.add(change);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Var.BINARY_CHANGES_FILE_PATH))) {
            oos.writeObject(existingChanges);
        } catch (IOException e) {
            log.error("Error occurred while serializing changes", e);
        }
    }

    /**
     * Deserializes the list of change logs from the binary file.
     * If the file does not exist or an error occurs, an empty list is returned.
     *
     * @return the list of deserialized change logs.
     */
    public static List<ChangeLog> deserializeChanges() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Var.BINARY_CHANGES_FILE_PATH))) {
            return (List<ChangeLog>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error occurred while deserializing changes", e);
            return new ArrayList<>();
        }
    }
}
