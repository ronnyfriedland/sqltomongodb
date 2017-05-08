package de.ronnyfriedland.nosql.mongodb.converter;

/**
 * @author ronnyfriedland
 */
public class StringToArrayConverter implements Converter<String, String[]> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] convert(final String source) {
        if (null != source) {
            return new String[] { source };
        }
        return null;
    }


}
