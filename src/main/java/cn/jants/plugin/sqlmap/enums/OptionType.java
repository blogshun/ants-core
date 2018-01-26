package cn.jants.plugin.sqlmap.enums;

/**
 * @author MrShun
 * @version 1.0
 */
public enum OptionType {

    SELECT("select"),
    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete");

    private String value;

    OptionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
