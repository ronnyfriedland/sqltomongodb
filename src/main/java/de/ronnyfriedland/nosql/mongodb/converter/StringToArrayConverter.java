package de.ronnyfriedland.nosql.mongodb.converter;

import java.util.Optional;

/**
 * @author ronnyfriedland
 */
public class StringToArrayConverter implements Converter<String, String[]> {

    private final String arrayDelimiter;

    /**
     * Creates a new {@link StringToArrayConverter} instance.
     *
     * @param delimiter the array delimiter (optional)
     */
    public StringToArrayConverter(final Optional<String> delimiter) {
        this.arrayDelimiter = delimiter.orElse(",");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] convert(final String source) {
        if (null != source) {
            String[] sources = source.split(arrayDelimiter);
            if (0 < sources.length) {
                return sources;
            } else {
                return new String[] { source };
            }
        }
        return null;
    }

}
