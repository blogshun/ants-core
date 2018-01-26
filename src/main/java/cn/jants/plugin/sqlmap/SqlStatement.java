package cn.jants.plugin.sqlmap;


/**
 * @author MrShun
 * @version 1.0
 */
public class SqlStatement {

    /**
     * 查询sql语句
     *
     * @param key
     * @param params
     * @return
     */
    public static SqlParams getSql(String key, Object params) {
        return SqlParser.getPreparedStatement(key, params);
    }

    public static SqlParams getSql(String key) {
        return SqlParser.getPreparedStatement(key, null);
    }
}