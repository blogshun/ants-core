package com.ants.plugin.sqlmap;

import com.ants.plugin.db.Db;

import java.util.List;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017/12/12
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
