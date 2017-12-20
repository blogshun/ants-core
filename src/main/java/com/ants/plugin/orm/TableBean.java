package com.ants.plugin.orm;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-07
 */
public class TableBean {

    private String table;

    private String primaryKey;

    private List<String> fields;

    public TableBean(){}

    public TableBean(String table, String primaryKey, List<String> fields) {
        this.table = table;
        this.primaryKey = primaryKey;
        this.fields = fields;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
