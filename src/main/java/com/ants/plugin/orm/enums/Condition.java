package com.ants.plugin.orm.enums;

/**
 * @author MrShun
 * @version 1.0
 */
public enum Condition {

    EQ(" %s = ?"),

    GT(" %s > ?"),

    GTEQ(" %s >= ?"),

    LT(" %s < ?"),

    LTEQ(" %s <= ?"),

    NE(" %s <> ?"),

    NULL(" %s is NULL"),

    IN(" %s in"),

    BETAND(" %s between ? and ?"),

    LIKE(" %s like ?"),

    EMBED(" %s");

    private String value;

    Condition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
