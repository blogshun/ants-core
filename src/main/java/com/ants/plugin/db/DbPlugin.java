package com.ants.plugin.db;

import com.ants.common.bean.JsonMap;
import com.ants.core.ext.Plugin;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-12-03
 */
public class DbPlugin extends CommonProperty implements Plugin {

    private Db db;

    //TODO 可以继续扩展属性

    public DbPlugin(String url, String driverClassName, String username, String password) {
        super(url, driverClassName, username, password);
    }

    public DbPlugin(String name, String url, String driverClassName, String username, String password) {
        super(name, url, driverClassName, username, password);
    }

    @Override
    public boolean start() {
        this.db = new Db(getName(), getUrl(), getDriverClassName(), getUsername(), getPassword());
        JsonMap obj = db.query("select 1");
        System.err.println("test db dataSource > " + obj.getStr("1", "null"));
        return true;
    }

    @Override
    public boolean destroy() {
        return true;
    }

    public Db getDb() {
        return db;
    }
}
