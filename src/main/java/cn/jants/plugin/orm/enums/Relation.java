package cn.jants.plugin.orm.enums;

/**
 * @author MrShun
 * @version 1.0
 */
public enum Relation {

    INNER(" inner join %s %s on %s=%s"),

    lEFT(" left join %s %s on %s=%s"),

    RIGHT(" right join %s %s on %s=%s"),

    FULL(" full join %s %s on %s=%s");

    private String value;

    Relation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
