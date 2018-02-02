package cn.jants.core.module;

import cn.jants.common.utils.StrUtil;
import cn.jants.plugin.db.Db;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MrShun
 * @version 1.0
 */
final public class DbManager {

    public final static String DEFAULT_NAME = "_default";

    private final static ConcurrentMap<String, Db> DBS = new ConcurrentHashMap<>();

    public static void add(String name, Db db) {
        if (db == null) {
            throw new IllegalArgumentException("db can not be null");
        }
        DBS.put(name, db);
    }

    public static Db get(String name) {
        Db db = DBS.get(StrUtil.isBlank(name) ? DEFAULT_NAME : name);
        if (db == null) {
            db = new Db();
            DBS.put(DEFAULT_NAME, db);
        }
        return db;
    }

    public static boolean containsKey(String name) {
        return DBS.containsKey(name);
    }
}
