package de.ronnyfriedland.nosql.mongodb.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Entity {

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    private String sourceSql;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    private String targetCollection;

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Column> column = new ArrayList<>();

    public String getSourceSql() {
        return sourceSql;
    }

    public void setSourceSql(final String sourceSql) {
        this.sourceSql = sourceSql;
    }

    public String getTargetCollection() {
        return targetCollection;
    }

    public void setTargetCollection(final String targetCollection) {
        this.targetCollection = targetCollection;
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
