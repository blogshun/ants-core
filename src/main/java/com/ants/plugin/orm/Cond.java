package com.ants.plugin.orm;

import com.ants.plugin.orm.enums.Condition;
import com.ants.plugin.orm.enums.Symbol;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-09
 */
public class Cond {

    /**
     * 符号
     */
    private Symbol symbol;

    /**
     * 字段名称
     */
    private String field;

    /**
     * 条件
     */
    private Condition cond;

    /**
     * 值
     */
    private Object value;

    public Cond(Symbol symbol, String field, Condition cond, Object value) {
        this.symbol = symbol;
        this.field = field;
        this.cond = cond;
        this.value = value;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Condition getCond() {
        return cond;
    }

    public void setCond(Condition cond) {
        this.cond = cond;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
