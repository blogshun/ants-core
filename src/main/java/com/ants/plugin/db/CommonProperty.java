package com.ants.plugin.db;


import com.ants.common.bean.JsonMap;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author MrShun
 * @version 1.0
 */
public class CommonProperty {

    /**
     * 数据源连接URL, 数据库账号, 数据库密码, 驱动名称
     */
    private String url, username, password, driverClassName;

    /**
     * 数据源名称
     */
    private String name = "";

    public CommonProperty(String url, String driverClassName, String username, String password) {
        this.url = url;
        this.driverClassName = driverClassName;
        this.username = username;
        this.password = password;
    }

    public CommonProperty(String name, String url, String driverClassName, String username, String password) {
        this.name = name;
        this.url = url;
        this.driverClassName = driverClassName;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void test(String sourceName, DataSource dataSource) throws SQLException {
        Db db = new Db(dataSource);
        JsonMap obj = db.query("select 1");
        System.err.println(sourceName + " > " + obj.getStr("1", "null"));
    }
}
