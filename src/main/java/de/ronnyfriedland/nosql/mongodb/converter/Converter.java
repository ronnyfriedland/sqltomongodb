package de.ronnyfriedland.nosql.mongodb.converter;

/**
 * Converts given data of type T and converts the data (depends on the implementation) to the result type.
 *
 * @author ronnyfriedland
 *
 * @param <T> the source type
 * @param <S> the result type
 */
public interface Converter<T, S> {

    /**
     * Convert source to the target result type.
     *
     * @param source the source to convert
     * @return the (expected) result
     */
    S convert(T source);
}
