package com.ants.common.enums;


import com.ants.common.annotation.service.Source;
import com.ants.core.module.PluginManager;
import com.ants.plugin.db.*;

import javax.sql.DataSource;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-05-17
 */
public enum DataSourceType {

    /**
     * 缺省
     */
    NONE,

    /**
     * C3P0
     */
    C3P0,
    /**
     * DBCP
     */
    DBCP,
    /**
     * HIKARICP
     */
    HIKARICP,
    /**
     * DRUID
     */
    DRUID;

    public static DataSource getDataSource(Source source) {
        DataSource dataSource = null;
        if (source.type() == DataSourceType.HIKARICP) {
            HikariCpPlugin classObject = "".equals(source.value()) ?
                    PluginManager.findFirstSourcePluginObject(HikariCpPlugin.class)
                    : PluginManager.findSourcePluginObject(HikariCpPlugin.class, source.value());
            if (classObject == null) {
                HikariCpPlugin sourcePluginObject = PluginManager.findRandomSourcePluginObject(HikariCpPlugin.class);
                if (sourcePluginObject != null) {
                    dataSource = sourcePluginObject.getDataSource();
                }
            } else {
                dataSource = PluginManager.findPluginObject(HikariCpPlugin.class).getDataSource();
            }
        } else if (source.type() == DataSourceType.C3P0) {
            C3p0Plugin classObject = "".equals(source.value()) ?
                    PluginManager.findFirstSourcePluginObject(C3p0Plugin.class)
                    : PluginManager.findSourcePluginObject(C3p0Plugin.class, source.value());
            if (classObject == null) {
                C3p0Plugin sourcePluginObject = PluginManager.findRandomSourcePluginObject(C3p0Plugin.class);
                if (sourcePluginObject != null) {
                    dataSource = sourcePluginObject.getDataSource();
                }
            } else {
                dataSource = classObject.getDataSource();
            }
        } else if (source.type() == DataSourceType.DBCP) {
            DbcpPlugin classObject = "".equals(source.value()) ?
                    PluginManager.findFirstSourcePluginObject(DbcpPlugin.class) :
                    PluginManager.findSourcePluginObject(DbcpPlugin.class, source.value());
            if (classObject == null) {
                DbcpPlugin sourcePluginObject = PluginManager.findRandomSourcePluginObject(DbcpPlugin.class);
                if (sourcePluginObject != null) {
                    dataSource = sourcePluginObject.getDataSource();
                }
            } else {
                dataSource = classObject.getDataSource();
            }
        } else if (source.type() == DataSourceType.DRUID) {
            DruidPlugin classObject = "".equals(source.value()) ?
                    PluginManager.findFirstSourcePluginObject(DruidPlugin.class)
                    : PluginManager.findSourcePluginObject(DruidPlugin.class, source.value());
            if (classObject == null) {
                DruidPlugin sourcePluginObject = PluginManager.findRandomSourcePluginObject(DruidPlugin.class);
                if (sourcePluginObject != null) {
                    dataSource = sourcePluginObject.getDataSource();
                }
            } else {
                dataSource = classObject.getDataSource();
            }
        }
        return dataSource;
    }

    public static Db getNativeDb(String name) {
        DbPlugin dbPlugin = "".equals(name) ? PluginManager.findFirstSourcePluginObject(DbPlugin.class)
                : PluginManager.findSourcePluginObject(DbPlugin.class, name);
        if (dbPlugin == null) {
            throw new IllegalArgumentException("没有找到 > " + name + " 数据源名称!");
        }
        return dbPlugin.getDb();
    }

}
