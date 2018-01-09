package com.ants.plugin.db;

import com.alibaba.fastjson.JSON;
import com.ants.core.ext.Plugin;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-05-18
 */
public class DbcpPlugin extends CommonProperty implements Plugin {

    private DataSource dataSource;

    //TODO 可以继续扩展属性

    public DbcpPlugin(String url, String driverClassName, String username, String password) {
        super(url, driverClassName, username, password);
    }

    public DbcpPlugin(String name, String url, String driverClassName, String username, String password) {
        super(name, url, driverClassName, username, password);
    }

    @Override
    public boolean start() {
        try {
            Properties config = JSON.parseObject(JSON.toJSONString(this), Properties.class);
            dataSource = BasicDataSourceFactory.createDataSource(config);
            test("test dbcp dataSource !", dataSource);
        } catch (Exception e) {
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

    public DataSource getDataSource(Properties properties) {
        try {
            Properties config = JSON.parseObject(JSON.toJSONString(this), Properties.class);
            properties.putAll(config);
            return BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
