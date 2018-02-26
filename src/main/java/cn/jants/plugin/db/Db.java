package cn.jants.plugin.db;

import cn.jants.common.bean.JsonMap;
import cn.jants.common.bean.Page;
import cn.jants.common.bean.PageConditions;
import cn.jants.common.bean.Prop;
import cn.jants.common.enums.TxLevel;
import cn.jants.common.exception.SQLParamsException;
import cn.jants.common.utils.StrCaseUtil;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.context.AppConstant;
import cn.jants.core.module.DbManager;
import cn.jants.core.utils.ParamTypeUtil;
import cn.jants.plugin.orm.Criteria;
import cn.jants.plugin.orm.Table;
import cn.jants.plugin.orm.enums.OrderBy;
import cn.jants.plugin.sqlmap.SqlStatement;
import cn.jants.plugin.sqlmap.SqlParams;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author MrShun
 * @version 1.0
 *          Date 2017-09-06
 */
public class Db<T> {

    private static final Logger logger = LoggerFactory.getLogger(Db.class);

    protected ThreadLocal<Connection> connections = new ThreadLocal<>();

    protected DataSource dataSource;

    private String name, url, driverClassName, username, password;

    private Connection conn = null;

    /**
     * 默认原生Db配置
     */
    public Db() {
        this.name = DbManager.DEFAULT_NAME;
        this.url = Prop.getStr("ants.db.url");
        this.driverClassName = Prop.getStr("ants.db.driver-class-name");
        this.username = Prop.getStr("ants.db.username");
        this.password = Prop.getStr("ants.db.password");
        String dataSourceTypeStr = Prop.getStr("ants.db.data-source");
        if ("druid".equalsIgnoreCase(dataSourceTypeStr)) {
            Properties properties = Prop.getProperties("ants.db.data-source.druid");
            dataSource = new DruidPlugin(url, driverClassName, username, password).getDataSource(properties);
        } else if ("c3p0".equalsIgnoreCase(dataSourceTypeStr)) {
            dataSource = C3p0Plugin.getDataSource(url, driverClassName, username, password);
        } else if ("dbcp".equalsIgnoreCase(dataSourceTypeStr)) {
            Properties properties = Prop.getProperties("ants.db.data-source.dbcp");
            dataSource = new DbcpPlugin(url, driverClassName, username, password).getDataSource(properties);
        } else if ("hikari".equalsIgnoreCase(dataSourceTypeStr)) {
            Properties properties = Prop.getProperties("ants.db.data-source.hikari");
            dataSource = new HikariCpPlugin(url, driverClassName, username, password).getDataSource(properties);
        }
        getConnection();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 数据源连接带连接池
     *
     * @param dataSource 连接池
     */
    public Db(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        if (dataSource == null) {
            try {
                Class.forName(driverClassName);
                conn = DriverManager.getConnection(url, username, password);
                connections.set(conn);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                logger.debug("原生加载 com.mysql.jdbc.Driver 失败!");
            } catch (SQLException e) {
                e.printStackTrace();
                logger.debug("原生连接数据库失败, 请认真检查配置!");
            }

        } else {
            try {
                conn = dataSource.getConnection();
                connections.set(conn);
            } catch (SQLException e) {
                e.printStackTrace();
                logger.debug("获取数据源失败, 请认真检查配置!");
            }
        }
        return conn;
    }

    /**
     * 原生连接不带连接池
     *
     * @param name            链接名称
     * @param url             链接地址
     * @param driverClassName 驱动名称
     * @param username        数据库用户名
     * @param password        数据库密码
     */
    public Db(String name, String url, String driverClassName, String username, String password) {
        this.name = name;
        this.url = url;
        this.driverClassName = driverClassName;
        this.username = username;
        this.password = password;
        getConnection();
    }

    /**
     * 开启事物
     *
     * @param dataSource    数据源
     * @param currentSource 当前数据源名称
     * @param level
     */
    public void startTx(DataSource dataSource, String currentSource, TxLevel level) {
        if (dataSource == null && conn == null) {
            throw new RuntimeException(currentSource + " 没有配置数据源, 错误!");
        }
        Connection conn = connections.get();
        try {
            //如果在当前线程中没有绑定相应的connection
            if (dataSource != null && conn == null) {
                conn = dataSource.getConnection();
            } else if (dataSource == null && conn == null) {
                conn = getConnection();
            }
            connections.set(conn);
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(level.level());
            logger.debug(currentSource + "start transactional level {} [{}] !", level, conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void commit() throws SQLException {
        Connection conn = connections.get();
        if (conn != null) {
            conn.commit();
            close(conn);
            logger.debug("Commit transactional {} !", conn);
        }
    }

    public void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                //从ThreadLocal中清除Connection
                connections.remove();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(PreparedStatement ps) {
        close(null, ps);
    }

    public void close(ResultSet rs, PreparedStatement ps) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rollback() {
        Connection conn = connections.get();
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.debug("RollBack transactional {} !", conn);
    }


    /**
     * 批处理
     *
     * @param sql    预处理语句
     * @param params 参数
     * @return
     */
    public int[] batch(String sql, Object[]... params) {
        int[] rows = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            boolean check = false;
            Connection conn = connections.get();
            if (conn == null) {
                conn = getConnection();
                check = true;
            }
            if (sql == null) {
                if (conn != null) {
                    conn.close();
                }
                throw new SQLException("Null SQL statement");
            }
            ps = conn.prepareStatement(sql);
            if (params != null && params.length != 0) {
                for (int i = 0; i < params.length; i++) {
                    fillStatement(sql, ps, params[i]);
                    ps.addBatch();
                }
            }
            int[] res = ps.executeBatch();
//            if (check) {
//                close(conn);
//            }
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLParamsException(e);
        } finally {
            close(rs, ps);
        }
    }

    /**
     * 批处理返回主键
     *
     * @param sql    预处理语句
     * @param params 参数
     * @return
     */
    public Long[] batchReturnKey(String sql, Object[]... params) {
        Long[] keys = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            boolean check = false;
            Connection conn = connections.get();
            if (conn == null) {
                conn = getConnection();
                check = true;
            }
            if (sql == null) {
                if (conn != null) {
                    conn.close();
                }
                throw new SQLException("Null SQL statement");
            }
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (params != null && params.length != 0) {
                for (int i = 0; i < params.length; i++) {
                    fillStatement(sql, ps, params[i]);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            rs = ps.getGeneratedKeys();
            if (rs.first()) {
                keys = new Long[rs.getRow()];
                int i = 0;
                while (rs.next()) {
                    keys[i] = rs.getLong(1);
                    i++;
                }
            }
//            if (check) {
//                close(conn);
//            }
            return keys;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLParamsException(e);
        } finally {
            close(rs, ps);
        }
    }

    /**
     * 保存数据带放回主键Key
     *
     * @param sql    预处理语句
     * @param params 参数
     * @return
     */
    public Long insertReturnKey(String sql, Object... params) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            boolean check = false;
            Connection conn = connections.get();
            if (conn == null) {
                conn = getConnection();
                check = true;
            }
            if (sql == null) {
                if (conn != null) {
                    conn.close();
                }
                throw new SQLException("Null SQL statement");
            }
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            fillStatement(sql, ps, params);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.first()) {
                return rs.getLong(1);
            }
//            if (check) {
//                close(conn);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLParamsException(e);
        } finally {
            close(rs, ps);
        }
        return null;
    }

    /**
     * 保存数据不带放回主键Key
     *
     * @param sql    预处理语句
     * @param params 参数
     */
    public void insert(String sql, Object... params) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            boolean check = false;
            Connection conn = connections.get();
            if (conn == null) {
                conn = getConnection();
                check = true;
            }
            if (sql == null) {
                if (conn != null) {
                    conn.close();
                }
                throw new SQLException("Null SQL statement");
            }
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            fillStatement(sql, ps, params);
            ps.execute();
//            if (check) {
//                close(conn);
//            }
        } catch (SQLException e) {
            throw new SQLParamsException(e);
        } finally {
            close(ps);
        }
    }

    /**
     * 修改数据
     *
     * @param sql    预处理语句
     * @param params 参数
     * @return
     */
    public int update(String sql, Object... params) {
        PreparedStatement ps = null;
        try {
            boolean check = false;
            Connection conn = connections.get();
            if (conn == null) {
                conn = getConnection();
                check = true;
            }
            if (sql == null) {
                if (conn != null) {
                    conn.close();
                }
                throw new SQLException("Null SQL statement");
            }
            ps = conn.prepareStatement(sql);
            fillStatement(sql, ps, params);
            int res = ps.executeUpdate();
//            if (check) {
//                close(conn);
//            }
            return res;
        } catch (SQLException e) {
            throw new SQLParamsException(e);
        } finally {
            close(ps);
        }
    }

    public int update(String sql) {
        return update(sql, null);
    }

    /**
     * 查询单条数据放入到JsonMap中
     *
     * @param sql    预处理语句
     * @param params 对象参数数组
     * @return
     */
    public JsonMap query(String sql, Object... params) {
        JsonMap result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            boolean check = false;
            Connection conn = connections.get();
            if (conn == null) {
                conn = getConnection();
                check = true;
            }
            if (sql == null) {
                if (conn != null) {
                    conn.close();
                }
                throw new SQLException("Null SQL statement");
            }
            ps = conn.prepareStatement(sql);
            fillStatement(sql, ps, params);
            rs = ps.executeQuery();
            if (rs.first()) {
                result = JsonMap.newJsonMap();
                //获得列集
                ResultSetMetaData rsm = rs.getMetaData();
                for (int j = 1; j <= rsm.getColumnCount(); j++) {
                    String columnName = rsm.getColumnLabel(j);
                    Object val = rs.getObject(columnName);
                    result.set(AppConstant.HUMP ? StrCaseUtil.toCamelCase(columnName) : columnName, val);
                }
            }
//            if (check) {
//                close(conn);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLParamsException(e);
        } finally {
            close(rs, ps);
        }
        return result;
    }

    public JsonMap query(String sql) {
        return query(sql, new Object[]{});
    }

    /**
     * 查询一个字段值
     *
     * @param sql    sql语句
     * @param params 条件
     * @return
     */
    public Object queryOne(String sql, Object... params) {
        Object result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            boolean check = false;
            Connection conn = connections.get();
            if (conn == null) {
                conn = getConnection();
                check = true;
            }
            if (sql == null) {
                if (conn != null) {
                    conn.close();
                }
                throw new SQLException("Null SQL statement");
            }
            ps = conn.prepareStatement(sql);
            fillStatement(sql, ps, params);
            rs = ps.executeQuery();
            if (rs.first()) {
                result = rs.getObject(1);
            }
//            if (check) {
//                close(conn);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLParamsException(e);
        } finally {
            close(rs, ps);
        }
        return result;
    }


    /**
     * 查询列表结果填充JsonMap
     *
     * @param sql    预处理语句
     * @param params
     * @return
     */
    public List<JsonMap> list(String sql, Object... params) {
        List<JsonMap> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            boolean check = false;
            Connection conn = connections.get();
            if (conn == null) {
                conn = getConnection();
                check = true;
            }
            if (sql == null) {
                if (conn != null) {
                    conn.close();
                }
                throw new SQLException("Null SQL statement");
            }
            ps = conn.prepareStatement(sql);
            fillStatement(sql, ps, params);
            rs = ps.executeQuery();
            //获得列集
            ResultSetMetaData rsm = rs.getMetaData();
            while (rs.next()) {
                JsonMap jsonMap = JsonMap.newJsonMap();
                for (int j = 1; j <= rsm.getColumnCount(); j++) {
                    String columnName = rsm.getColumnLabel(j);
                    Object val = rs.getObject(columnName);
                    jsonMap.set(AppConstant.HUMP ? StrCaseUtil.toCamelCase(columnName) : columnName, val);
                }
                result.add(jsonMap);
            }
//            if (check) {
//                close(conn);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLParamsException(e);
        } finally {
            close(rs, ps);
        }
        return result;
    }

    public List<JsonMap> list(String sql) {
        return list(sql, new Object[]{});
    }

    public List<Object> listOne(String sql, Object... params) {
        List<Object> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            boolean check = false;
            Connection conn = connections.get();
            if (conn == null) {
                conn = getConnection();
                check = true;
            }
            if (sql == null) {
                if (conn != null) {
                    conn.close();
                }
                throw new SQLException("Null SQL statement");
            }
            ps = conn.prepareStatement(sql);
            fillStatement(sql, ps, params);
            rs = ps.executeQuery();
            //获得列集
            ResultSetMetaData rsm = rs.getMetaData();
            while (rs.next()) {
                Object object = rs.getObject(rsm.getColumnLabel(1));
                result.add(object);
            }
//            if (check) {
//                close(conn);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLParamsException(e);
        } finally {
            close(rs, ps);
        }
        return result;
    }

    /**
     * 查询单条对象将结果反射到对象里面
     *
     * @param sql    预处理语句
     * @param cls    class对象
     * @param params 对象参数数组
     * @return
     */
    public T query(String sql, Class<T> cls, Object... params) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        T obj = null;
        try {
            boolean check = false;
            Connection conn = connections.get();
            if (conn == null) {
                conn = getConnection();
                check = true;
            }
            if (sql == null) {
                if (conn != null) {
                    conn.close();
                }
                throw new SQLException("Null SQL statement");
            }
            ps = conn.prepareStatement(sql);
            fillStatement(sql, ps, params);
            rs = ps.executeQuery();
            if (rs.first()) {
                //获得列集
                ResultSetMetaData rsm = rs.getMetaData();
                obj = cls.newInstance();
                Class<?> superclass = cls.getSuperclass();
                if (superclass.getDeclaredAnnotation(Table.class) != null) {
                    setColumns(rs, rsm, superclass.getDeclaredFields(), obj);
                } else {
                    setColumns(rs, rsm, cls.getDeclaredFields(), obj);
                }

            } else {
                obj = null;
            }
//            if (check) {
//                close(conn);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLParamsException(e);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            close(rs, ps);
        }
        return obj;
    }

    public T query(String sql, Class<T> cls) {
        return query(sql, cls, null);
    }

    /**
     * 查询列表反射填充对象
     *
     * @param sql    预处理语句
     * @param cls    对象class
     * @param params 对象参数数组
     * @return
     */
    public List<T> list(String sql, Class<T> cls, Object... params) {
        List<T> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            boolean check = false;
            Connection conn = connections.get();
            if (conn == null) {
                conn = getConnection();
                check = true;
            }
            if (sql == null) {
                if (conn != null) {
                    conn.close();
                }
                throw new SQLException("Null SQL statement");
            }
            ps = conn.prepareStatement(sql);
            fillStatement(sql, ps, params);
            rs = ps.executeQuery();
            //获得列集
            ResultSetMetaData rsm = rs.getMetaData();
            Class<?> superclass = cls.getSuperclass();
            Table tableAnnotation = superclass.getDeclaredAnnotation(Table.class);
            while (rs.next()) {
                T obj = cls.newInstance();
                if (tableAnnotation != null) {
                    setColumns(rs, rsm, superclass.getDeclaredFields(), obj);
                } else {
                    setColumns(rs, rsm, cls.getDeclaredFields(), obj);
                }
                System.out.println("come me ~~");
                result.add(obj);
            }
//            if (check) {
//                close(conn);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLParamsException(e);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            close(rs, ps);
        }
        return result;
    }

    public List<T> list(String sql, Class<T> obj) {
        return list(sql, obj, null);
    }


    public Page page(String sql, Integer pageIndex, Integer pageSize, Object... params) {
        PageConditions pageConditions = new PageConditions(pageIndex, pageSize);
        pageConditions.setParams(params);
        return page(sql, null, pageConditions);
    }

    public Page<T> page(String sql, Class<T> cls, Integer pageIndex, Integer pageSize, Object... params) {
        PageConditions pageConditions = new PageConditions(pageIndex, pageSize);
        pageConditions.setParams(params);
        return page(sql, cls, pageConditions);
    }

    public Page page(String sql, PageConditions pageConditions) {
        return page(sql, null, pageConditions);
    }

    public Page<T> page(String sql, Class<T> cls, PageConditions pageConditions) {
        Integer pageIndex = pageConditions.getPageNum();
        Integer pageSize = pageConditions.getPageSize();
        int page = (pageIndex == null || pageIndex <= 1) ? 1 : pageIndex;
        int size = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        StringBuffer sb = new StringBuffer(sql);
        String orderField = pageConditions.getOrderField();
        OrderBy sortType = pageConditions.getSortType();
        if (StrUtil.notBlank(orderField) && sortType != null) {
            sb.append(" order by " + orderField + " " + sortType);
        }
        sb.append(" limit " + ((page - 1) * size) + "," + size);
        Object[] params = pageConditions.getParams();
        List data = (cls == null) ? list(sb.toString(), params) : list(sb.toString(), cls, params);
        //sql语句转大写
        String countSql = sb.toString().toUpperCase();
        if (countSql.indexOf("GROUP") != -1 || sb.indexOf("DISTINCT") != -1) {
            countSql = "select count(1) as count from (select count(1) " + sql.substring(sql.toUpperCase().indexOf("FROM"), sql.length()) + ") as temp";
        } else {
            countSql = "select count(1) as count " + sql.substring(sql.toUpperCase().indexOf("FROM"), sql.length());
        }
        long rows = query(countSql, params).getLong("count");
        int total = (int) (rows / size + (rows % size == 0 ? 0 : 1));
        if (AppConstant.DEBUG) {
            logger.debug("\nSQL    : {}\nParams : {}\n", sb.toString(), params == null ? "" : JSON.toJSON(params));
            logger.debug("\nSQL    : {}\nParams : {}\n", countSql, params == null ? "" : JSON.toJSON(params));
        }
        return new Page(page, size, data, rows, total);
    }

    /**
     * 私有方法填充参数
     *
     * @param sql    sql语句
     * @param ps     预处理对象
     * @param params 参数
     * @throws SQLException
     */
    private void fillStatement(String sql, PreparedStatement ps, Object... params) throws SQLException {
        if (params == null || params.length == 0) {
            return;
        }
        for (int i = 1; i <= params.length; i++) {
            ps.setObject(i, params[i - 1]);
        }
        if (AppConstant.DEBUG) {
            logger.debug("\nSQL    : {}\nParams : {}\n", sql, JSON.toJSON(params));
        }
    }

    /**
     * 私有方法填充对象
     *
     * @param rs     结果集
     * @param rsm    结果集数据
     * @param fields 字段
     * @param obj    对象
     * @throws SQLException
     */
    private void setColumns(ResultSet rs, ResultSetMetaData rsm, Field[] fields, Object obj) throws SQLException {
        for (int j = 1; j <= rsm.getColumnCount(); j++) {
            String columnName = rsm.getColumnLabel(j);
            Object val = rs.getObject(columnName);
            for (Field field : fields) {
                if (StrCaseUtil.toUnderlineName(columnName).equals(StrCaseUtil.toUnderlineName(field.getName()))) {
                    field.setAccessible(true);
                    try {
                        Class<?> type = field.getType();
                        if(type == Long.class){
                            val = rs.getLong(columnName);
                        }
                        field.set(obj, val);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }


    /********************* 分割线扩展处理 ********************/
    /**
     * ORM封装
     *
     * @param cls
     * @return
     */
    public Criteria<T> createCriteria(Class<T> cls) {
        return new Criteria(cls, this);
    }

    /**
     * 根据主键Id查询记录
     *
     * @param id 主键ID
     * @return
     */
    public JsonMap findById(String statement, Object id) {
        if (!ParamTypeUtil.isBaseDataType(id.getClass())) {
            throw new RuntimeException(id + " 不是基本数据类型!");
        }
        SqlParams sqlParams = SqlStatement.getSql(statement, id);
        return query(sqlParams.getSql(), sqlParams.getParams());
    }

    public T findById(String statement, Class<T> cls, Object id) {
        if (!ParamTypeUtil.isBaseDataType(id.getClass())) {
            throw new RuntimeException(id + " 不是基本数据类型!");
        }
        SqlParams sqlParams = SqlStatement.getSql(statement, id);
        return query(sqlParams.getSql(), cls, sqlParams.getParams());
    }

    /**
     * 查询一条记录
     *
     * @param statement sql配置
     * @param params    条件参数
     * @return
     */
    public JsonMap find(String statement, Map params) {
        SqlParams sqlParams = SqlStatement.getSql(statement, params);
        return query(sqlParams.getSql(), sqlParams.getParams());
    }

    public T find(String statement, Class<T> cls, Map params) {
        SqlParams sqlParams = SqlStatement.getSql(statement, params);
        return query(sqlParams.getSql(), cls, sqlParams.getParams());
    }

    /**
     * 查询列表
     *
     * @return
     */
    public List<JsonMap> findList(String statement, Map params) {
        SqlParams sqlParams = SqlStatement.getSql(statement, params);
        return list(sqlParams.getSql(), sqlParams.getParams());
    }

    public List<JsonMap> findList(String statement) {
        return findList(statement, null);
    }

    public List<T> findList(String statement, Class<T> cls, Map params) {
        SqlParams sqlParams = SqlStatement.getSql(statement, params);
        return list(sqlParams.getSql(), cls, sqlParams.getParams());
    }

    /**
     * 分页查询
     *
     * @param index 当前页数
     * @param size  每页大小
     * @return
     */
    public Page findPage(String statement, Integer index, Integer size, Map params) {
        SqlParams sqlParams = SqlStatement.getSql(statement, params);
        return page(sqlParams.getSql(), index, size, sqlParams.getParams());
    }

    public Page<T> findPage(String statement, Integer index, Integer size) {
        return findPage(statement, index, size, null);
    }

    /**
     * 分页查询
     *
     * @param index 当前页数
     * @param size  每页大小
     * @return
     */
    public Page<T> findPage(String statement, Class<T> cls, Integer index, Integer size, Map params) {
        SqlParams sqlParams = SqlStatement.getSql(statement, params);
        return page(sqlParams.getSql(), cls, index, size, sqlParams.getParams());
    }

    /**
     * 保存记录
     *
     * @param statement
     * @param params
     */
    public void save(String statement, Map params) {
        SqlParams sqlParams = SqlStatement.getSql(statement, params);
        insert(sqlParams.getSql(), sqlParams.getParams());
    }

    /**
     * 保存记录, 返回主键
     *
     * @param statement
     * @param params
     */
    public Long saveReturnKey(String statement, Map params) {
        SqlParams sqlParams = SqlStatement.getSql(statement, params);
        return insertReturnKey(sqlParams.getSql(), sqlParams.getParams());
    }

    /**
     * 根据主键修改数据
     *
     * @param statement
     * @return
     */
    public int updateById(String statement, Object id) {
        SqlParams sqlParams = SqlStatement.getSql(statement, id);
        return update(sqlParams.getSql(), sqlParams.getParams());
    }

    /**
     * 条件删除数据
     *
     * @param statement
     * @param params
     * @return
     */
    public int updateZ(String statement, Map params) {
        SqlParams sqlParams = SqlStatement.getSql(statement, params);
        return update(sqlParams.getSql(), sqlParams.getParams());
    }

    public int updateZ(String statement) {
        return updateZ(statement, null);
    }

}
