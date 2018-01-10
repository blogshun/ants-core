package cn.jants.plugin.sqlmap;

import cn.jants.plugin.db.Db;

/**
 * @author MrShun
 * @version 1.0
 */
public class SqlMap<T> {

    private Db db;

    private String statement;

    private T obj;

    private Class<T> cls;

    public SqlMap(String statement, Class<T> cls, Db db) {
        this.statement = statement;
        this.db = db;
        if (cls != null) {
            try {
                this.cls = cls;
                this.obj = cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


}
