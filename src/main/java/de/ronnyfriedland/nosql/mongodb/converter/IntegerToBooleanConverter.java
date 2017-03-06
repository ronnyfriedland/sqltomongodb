package de.ronnyfriedland.nosql.mongodb.converter;

import org.apache.commons.lang3.BooleanUtils;

public class IntegerToBooleanConverter implements Converter<Integer, Boolean> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean convert(final Integer source) {
        if (null == source) {
            return Boolean.FALSE;
        }
        return BooleanUtils.toBooleanObject(source);
    }

}
