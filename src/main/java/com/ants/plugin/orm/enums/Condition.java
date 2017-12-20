package com.ants.plugin.orm.enums;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-10
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

    LIKE_LEFT(" %s like '?%%'"),

    LIKE_CENTER(" %s like '%%?%%'"),

    LIKE_RIGHT(" %s like '%%?'");


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
