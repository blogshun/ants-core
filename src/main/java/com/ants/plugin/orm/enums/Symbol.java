package com.ants.plugin.orm.enums;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-09-07
 */
public enum Symbol {

    AND(" and"),

    OR(" or");

    private String value;

    Symbol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
