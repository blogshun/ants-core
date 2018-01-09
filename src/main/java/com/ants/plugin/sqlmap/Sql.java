package com.ants.plugin.sqlmap;


/**
 * @author MrShun
 * @version 1.0
 */
public class Sql {

    /**
     * 查询sql语句
     *
     * @param key
     * @param params
     * @return
     */
    public static SqlParams getSql(String key, Object params) {
        return SqlXmlParser.getPreparedStatement(key, params);
    }

    public static SqlParams getSql(String key) {
        return SqlXmlParser.getPreparedStatement(key, null);
    }
}