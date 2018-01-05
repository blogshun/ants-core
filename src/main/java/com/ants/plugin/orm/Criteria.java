package com.ants.plugin.orm;

import com.ants.common.bean.JsonMap;
import com.ants.common.bean.Page;
import com.ants.plugin.db.Db;
import com.ants.plugin.orm.enums.Condition;

import java.util.List;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017-12-06
 */
public class Criteria<T> extends Conditions {

    private Db db;

    private Class<T> cls;

    public Criteria(Class<T> cls, Db db) {
        this.cls = cls;
        this.db = db;
    }

    /**
     * 保存数据
     *
     * @param obj
     */
    public void save(Object obj) {
        SqlParams sqlParams = TableMapper.createInsertSql(obj);
        db.insert(sqlParams.getSql(), sqlParams.getParams());
    }

    /**
     * 保存数据, 返回主键
     *
     * @return
     */
    public Long saveReturnKey(Object obj) {
        SqlParams sqlParams = TableMapper.createInsertSql(obj);
        return db.insertReturnKey(sqlParams.getSql(), sqlParams.getParams());
    }

    /**
     * 条件删除数据
     *
     * @return
     */
    public Integer delete() {
        TableBean tableBean = TableMapper.findTableBean(cls);
        SqlParams sqlParams = TableMapper.createDeleteSql(tableBean, this);
        int result = db.update(sqlParams.getSql(), sqlParams.getParams());
        //清除条件
        clear();
        return result;
    }

    /**
     * 根据主键删除数据
     *
     * @param id 主键ID
     * @return
     */
    public Integer deleteById(Object id) {
        if (id == null) {
            throw new RuntimeException("传递的主键为NULL");
        }
        TableBean tableBean = TableMapper.findTableBean(cls);
        and(tableBean.getPrimaryKey(), Condition.EQ, id);
        SqlParams sqlParams = TableMapper.createDeleteSql(tableBean, this);
        int result = db.update(sqlParams.getSql(), sqlParams.getParams());
        //清除条件
        clear();
        return result;
    }

    /**
     * 修改数据对象
     *
     * @param obj
     * @return
     */
    public Integer update(Object obj) {
        SqlParams sqlParams = TableMapper.createUpdateSql(obj, this);
        int result = db.update(sqlParams.getSql(), sqlParams.getParams());
        //清除条件
        clear();
        return result;
    }

    /**
     * 根据主键Id查询记录
     *
     * @param id 主键ID
     * @return
     */
    public T findById(Object id) {
        if (id == null) {
            throw new RuntimeException("传递的主键为NULL");
        }
        TableBean tableBean = TableMapper.findTableBean(cls);
        and(tableBean.getPrimaryKey(), Condition.EQ, id);
        SqlParams sqlParams = TableMapper.createQuerySql(tableBean, this);
        T result = (T) db.query(sqlParams.getSql(), cls, sqlParams.getParams());
        //清除条件
        clear();
        return result;
    }

    public JsonMap findMapById(Object id) {
        if (id == null) {
            throw new RuntimeException("传递的主键为NULL");
        }
        TableBean tableBean = TableMapper.findTableBean(cls);
        and(tableBean.getPrimaryKey(), Condition.EQ, id);
        SqlParams sqlParams = TableMapper.createQuerySql(tableBean, this);
        JsonMap result = db.query(sqlParams.getSql(), sqlParams.getParams());
        //清除条件
        clear();
        return result;
    }

    /**
     * 查询一条记录
     *
     * @return
     */
    public T find() {
        TableBean tableBean = TableMapper.findTableBean(cls);
        SqlParams sqlParams = TableMapper.createQuerySql(tableBean, this);
        T result = (T) db.query(sqlParams.getSql(), cls, sqlParams.getParams());
        //清除条件
        clear();
        return result;
    }

    public JsonMap findMap() {
        TableBean tableBean = TableMapper.findTableBean(cls);
        SqlParams sqlParams = TableMapper.createQuerySql(tableBean, this);
        JsonMap result = db.query(sqlParams.getSql(), sqlParams.getParams());
        //清除条件
        clear();
        return result;
    }

    /**
     * 统计数据
     *
     * @return
     */
    public Integer count() {
        label("count(0)");
        TableBean tableBean = TableMapper.findTableBean(cls);
        SqlParams sqlParams = TableMapper.createQuerySql(tableBean, this);
        JsonMap result = db.query(sqlParams.getSql(), sqlParams.getParams());
        //清除条件
        clear();
        return result.getInt("count", 0);
    }

    /**
     * 查询列表
     *
     * @return
     */
    public List<T> findList() {
        TableBean tableBean = TableMapper.findTableBean(cls);
        SqlParams sqlParams = TableMapper.createQuerySql(tableBean, this);
        List result = db.list(sqlParams.getSql(), cls, sqlParams.getParams());
        //清除条件
        clear();
        return result;
    }

    public List<JsonMap> findMapList() {
        TableBean tableBean = TableMapper.findTableBean(cls);
        SqlParams sqlParams = TableMapper.createQuerySql(tableBean, this);
        List result = db.list(sqlParams.getSql(), sqlParams.getParams());
        //清除条件
        clear();
        return result;
    }
    /**
     * 分页查询
     *
     * @param index 当前页数
     * @param size  每页大小
     * @return
     */
    public Page findPage(Integer index, Integer size) {
        TableBean tableBean = TableMapper.findTableBean(cls);
        SqlParams sqlParams = TableMapper.createQuerySql(tableBean, this);
        Page result = db.page(sqlParams.getSql(), cls, index, size, sqlParams.getParams());
        //清除条件
        clear();
        return result;
    }
}
