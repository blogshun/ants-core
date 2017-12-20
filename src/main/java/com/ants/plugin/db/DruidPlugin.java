package com.ants.plugin.db;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.fastjson.JSON;
import com.ants.core.ext.Plugin;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-05-17
 */
public class DruidPlugin extends CommonProperty implements Plugin {

    /**
     * 初始连接池大小、最小空闲连接数、最大活跃连接数
     */
    private int initialSize = 10;
    private int minIdle = 10;
    private int maxActive = 100;

    /**
     * 配置获取连接等待超时的时间
     */
    private long maxWait = DruidDataSource.DEFAULT_MAX_WAIT;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private long timeBetweenEvictionRunsMillis = DruidDataSource.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
    /**
     * 配置连接在池中最小生存的时间
     */
    private long minEvictableIdleTimeMillis = DruidDataSource.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
    /**
     * 配置发生错误时多久重连
     */
    private long timeBetweenConnectErrorMillis = DruidDataSource.DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS;

    /**
     * hsqldb - "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS"
     * Oracle - "select 1 from dual"
     * DB2 - "select 1 from sysibm.sysdummy1"
     * mysql - "select 1"
     */
    private String validationQuery = "select 1";
    private boolean testWhileIdle = true;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;

    /**
     * 是否打开连接泄露自动检测
     */
    private boolean removeAbandoned = false;
    /**
     * 连接长时间没有使用，被认为发生泄露时长
     */
    private long removeAbandonedTimeoutMillis = 300 * 1000;
    /**
     * 发生泄露时是否需要输出 log，建议在开启连接泄露检测时开启，方便排错
     */
    private boolean logAbandoned = false;

    /**
     * 是否缓存preparedStatement，即PSCache，对支持游标的数据库性能提升巨大，如 oracle、mysql 5.5 及以上版本建议为 true;
     */
    private boolean poolPreparedStatements = true;

    /**
     * 只要maxPoolPreparedStatementPerConnectionSize>0,poolPreparedStatements就会被自动设定为true，使用oracle时可以设定此值。
     */
    private int maxPoolPreparedStatementPerConnectionSize = -1;

    /**
     * 监控统计："stat"    防SQL注入："wall"     组合使用： "stat,wall"
     */
    private String filters = "stat,wall";
    private List<Filter> filterList;

    private DataSource dataSource = null;

    public DruidPlugin(String url, String driverClassName, String username, String password) {
        super(url, driverClassName, username, password);
    }

    public DruidPlugin(String name, String url, String driverClassName, String username, String password) {
        super(name, url, driverClassName, username, password);
    }

    public String getInitialSize() {
        return String.valueOf(initialSize);
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public String getMinIdle() {
        return String.valueOf(minIdle);
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public String getMaxActive() {
        return String.valueOf(maxActive);
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public String getMaxWait() {
        return String.valueOf(maxWait);
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public String getTimeBetweenEvictionRunsMillis() {
        return String.valueOf(timeBetweenEvictionRunsMillis);
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public String getMinEvictableIdleTimeMillis() {
        return String.valueOf(minEvictableIdleTimeMillis);
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public String getTimeBetweenConnectErrorMillis() {
        return String.valueOf(timeBetweenConnectErrorMillis);
    }

    public void setTimeBetweenConnectErrorMillis(long timeBetweenConnectErrorMillis) {
        this.timeBetweenConnectErrorMillis = timeBetweenConnectErrorMillis;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public String isTestWhileIdle() {
        return String.valueOf(testWhileIdle);
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public String isTestOnBorrow() {
        return String.valueOf(testOnBorrow);
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public String isTestOnReturn() {
        return String.valueOf(testOnReturn);
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public String isRemoveAbandoned() {
        return String.valueOf(removeAbandoned);
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public String getRemoveAbandonedTimeoutMillis() {
        return String.valueOf(removeAbandonedTimeoutMillis);
    }

    public void setRemoveAbandonedTimeoutMillis(long removeAbandonedTimeoutMillis) {
        this.removeAbandonedTimeoutMillis = removeAbandonedTimeoutMillis;
    }

    public String isLogAbandoned() {
        return String.valueOf(logAbandoned);
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public String getMaxPoolPreparedStatementPerConnectionSize() {
        return String.valueOf(maxPoolPreparedStatementPerConnectionSize);
    }

    public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

    @Override
    public boolean start() {
        try {
            Properties config = JSON.parseObject(JSON.toJSONString(this), Properties.class);
            this.dataSource = DruidDataSourceFactory.createDataSource(config);
            test("test druid dataSource!", dataSource);
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
            return DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
