package de.ronnyfriedland.nosql.mongodb.converter;

public class StringToIntegerConverter implements Converter<String, Integer> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer convert(final String source) {
        return Integer.valueOf(source);
    }

}
