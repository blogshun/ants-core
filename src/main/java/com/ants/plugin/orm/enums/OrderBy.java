package com.ants.plugin.orm.enums;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-12-10
 */
public enum OrderBy {

    ASC(" order by %s asc"),

    DESC(" order by %s desc");

    private String value;

    OrderBy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
