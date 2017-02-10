package de.ronnyfriedland.nosql.mongodb.configuration;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Entity {

    @JacksonXmlProperty(isAttribute = true)
    private String source;

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Column> column = new ArrayList<>();

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public List<Column> getColumn() {
        return column;
    }

    public void setColumn(final List<Column> column) {
        this.column = column;
    }

    public void addColumn(final Column col) {
        column.add(col);
    }
}
