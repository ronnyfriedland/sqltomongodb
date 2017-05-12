package de.ronnyfriedland.nosql.mongodb.configuration;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * @author ronnyfriedland
 */
public class Column {

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    private String sourceColumn;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    private String targetField;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    private String type;

    @NotNull
    @JacksonXmlProperty(isAttribute = true)
    private String arrayDelimiter;

    @JacksonXmlProperty(isAttribute = true, localName = "pk")
    private boolean identityField;

    @JacksonXmlProperty(isAttribute = true, localName = "gridfs")
    private boolean storeInGridfs;

    @JacksonXmlProperty(isAttribute = true, localName = "gridfsIdSourceColumn")
    private String gridfsId;

    public String getSourceColumn() {
        return sourceColumn;
    }

    public String getTargetField() {
        return targetField;
    }

    public boolean isIdentityField() {
        return identityField;
    }

    public boolean isStoreInGridfs() {
        return storeInGridfs;
    }

    public void setIdentityField(final boolean identityField) {
        this.identityField = identityField;
    }

    public void setSourceColumn(final String sourceColumn) {
        this.sourceColumn = sourceColumn;
    }

    public void setTargetField(final String targetField) {
        this.targetField = targetField;
    }

    public void setStoreInGridfs(final boolean storeInGridfs) {
        this.storeInGridfs = storeInGridfs;
    }

    public String getGridfsId() {
        return gridfsId;
    }

    public void setGridfsId(final String gridfsId) {
        this.gridfsId = gridfsId;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getArrayDelimiter() {
        return arrayDelimiter;
    }

    public void setArrayDelimiter(final String arrayDelimiter) {
        this.arrayDelimiter = arrayDelimiter;
    }
}
