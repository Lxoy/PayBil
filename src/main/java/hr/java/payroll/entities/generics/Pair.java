package hr.java.payroll.entities.generics;

/**
 * A generic class that represents a pair of objects.
 * It holds two related objects, a first and a second, of types {@code T} and {@code U}.
 *
 * @param <T> the type of the first element.
 * @param <U> the type of the second element.
 *
 * @author Lovro Bilanović
 * @version 1.0
 */
public class Pair<T, U> {
    private final T first;
    private final U second;

    /**
     * Constructs a new pair with the given first and second elements.
     *
     * @param first  the first element of the pair.
     * @param second the second element of the pair.
     */
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element of the pair.
     *
     * @return the first element of the pair.
     */
    public T getFirst() {
        return first;
    }

    /**
     * Returns the second element of the pair.
     *
     * @return the second element of the pair.
     */
    public U getSecond() {
        return second;
    }
}
