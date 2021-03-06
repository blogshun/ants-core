package cn.jants.plugin.db;

import cn.jants.core.ext.Plugin;
import cn.jants.core.module.DbManager;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;

/**
 * @author MrShun
 * @version 1.0
 */
public class C3p0Plugin extends CommonProperty implements Plugin {


    private ComboPooledDataSource dataSource = null;

    //TODO 可以继续扩展属性

    public C3p0Plugin(String url, String driverClassName, String username, String password) {
        super(url, driverClassName, username, password);
    }

    public C3p0Plugin(String name, String url, String driverClassName, String username, String password) {
        super(name, url, driverClassName, username, password);
    }

    @Override
    public boolean start() throws SQLException {
        try {
            dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass(this.getDriverClassName());
            dataSource.setJdbcUrl(this.getUrl());
            dataSource.setUser(this.getUsername());
            dataSource.setPassword(this.getPassword());
            Db db = test("test c3p0 dataSource!", dataSource);
            DbManager.add(getName(), db);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean destroy() {
        try {
            dataSource.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource = null;
        }
        return true;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public static DataSource getDataSource(String url, String driverClassName, String username, String password) {
        try {
            ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
            comboPooledDataSource.setDriverClass(driverClassName);
            comboPooledDataSource.setJdbcUrl(url);
            comboPooledDataSource.setUser(username);
            comboPooledDataSource.setPassword(password);
            return comboPooledDataSource;
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        return null;
    }
}
