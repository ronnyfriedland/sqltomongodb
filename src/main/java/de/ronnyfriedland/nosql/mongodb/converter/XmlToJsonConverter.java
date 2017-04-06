package de.ronnyfriedland.nosql.mongodb.converter;

import org.json.JSONObject;
import org.json.XML;

public class XmlToJsonConverter implements Converter<String, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String convert(final String source) {
        if (null != source) {
            JSONObject json = XML.toJSONObject(source);
            return json.toString();
        }
        return null;
    }

}