package cn.jants.plugin.db;

import cn.jants.core.ext.Plugin;
import cn.jants.core.module.DbManager;
import com.alibaba.fastjson.JSON;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author MrShun
 * @version 1.0
 */
public class HikariCpPlugin extends CommonProperty implements Plugin {


    private HikariDataSource dataSource = null;

    //TODO 可以继续扩展属性

    public HikariCpPlugin(String url, String driverClassName, String username, String password) {
        super(url, driverClassName, username, password);
    }

    public HikariCpPlugin(String name, String url, String driverClassName, String username, String password) {
        super(name, url, driverClassName, username, password);
    }

    @Override
    public boolean start() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(this.getDriverClassName());
        hikariConfig.setJdbcUrl(this.getUrl());
        hikariConfig.setUsername(this.getUsername());
        hikariConfig.setPassword(this.getPassword());

        hikariConfig.setIdleTimeout(60000);
        hikariConfig.setConnectionTimeout(60000);
        hikariConfig.setValidationTimeout(3000);
        hikariConfig.setMaxLifetime(60000);
        dataSource = new HikariDataSource(hikariConfig);
        Db db = test("HikariCp DataSource", dataSource);
        DbManager.add(getName(), db);
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
        HikariConfig hikariConfig = JSON.parseObject(JSON.toJSONString(properties), HikariConfig.class);
        hikariConfig.setDriverClassName(getDriverClassName());
        hikariConfig.setJdbcUrl(getUrl());
        hikariConfig.setUsername(getUsername());
        hikariConfig.setPassword(getPassword());
        return new HikariDataSource(hikariConfig);
    }
}
