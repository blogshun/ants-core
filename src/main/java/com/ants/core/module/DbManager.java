package com.ants.core.module;

import com.ants.plugin.db.Db;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MrShun
 * @version 1.0
 */
final public class DbManager {

    private final static ConcurrentMap<String, Db> DBS = new ConcurrentHashMap<>();

    public static void add(String name, Db db) {
        if (db == null) {
            throw new IllegalArgumentException("db can not be null");
        }
        DBS.put(name, db);
    }

    public static Db get(String name) {
        return DBS.get(name);
    }

    public static boolean containsKey(String name){
        return DBS.containsKey(name);
    }
}
