package com.ants.plugin.sqlmap;

import java.io.Serializable;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017-09-09
 */
public class SqlParams implements Serializable {

    /**
     * 预处理sql 语句
     */
    private String sql;

    /**
     * 数据对象
     */
    private Object[] params;

    public SqlParams(String sql, Object[] params) {
        this.sql = sql;
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
