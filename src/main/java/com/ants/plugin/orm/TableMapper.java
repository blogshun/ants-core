package com.ants.plugin.orm;

import com.ants.common.utils.StrCaseUtil;
import com.ants.common.utils.StrUtil;
import com.ants.plugin.orm.enums.Condition;
import com.ants.restful.bind.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-07
 */
public class TableMapper {

    private final static ConcurrentMap<Class, TableBean> TABLES = new ConcurrentHashMap<>();

    /**
     * 根据class生成实体和表映射
     *
     * @param cls
     * @return
     */
    public static TableBean findTableBean(Class<?> cls) {
        if (TABLES.containsKey(cls)) {
            return TABLES.get(cls);
        } else {
            TableBean tableBean = new TableBean();
            Class<?> superclass = cls.getSuperclass();
            Table table = superclass.getDeclaredAnnotation(Table.class);
            Field[] fields;
            if (table != null) {
                fields = superclass.getDeclaredFields();
            } else {
                table = cls.getDeclaredAnnotation(Table.class);
                fields = cls.getDeclaredFields();
            }
            String tableName = table.name();
            tableBean.setTable(tableName);
            List list = new ArrayList<>();
            for (Field field : fields) {
                Column column = field.getDeclaredAnnotation(Column.class);
                if (column != null) {
                    String columnName = column.name();
                    list.add(columnName);
                    Id primaryKey = field.getDeclaredAnnotation(Id.class);
                    if (primaryKey != null) {
                        tableBean.setPrimaryKey(columnName);
                    }
                }
            }
            tableBean.setFields(list);
            TABLES.put(cls, tableBean);
            return tableBean;
        }
    }


    /**
     * 创建查询语句
     *
     * @param tableBean
     * @param conditions 条件属性对象
     */
    public static SqlParams createQuerySql(TableBean tableBean, Conditions conditions) {
        SqlParams sqlParams = new SqlParams();
        StringBuffer sql = new StringBuffer("select");
        sql.append(conditions.getLabel());
        sql.append(" from ");
        sql.append(tableBean.getTable() + " _");
        List<String> relations = conditions.getRelations();
        if (relations.size() > 0) {
            for (String relation : relations) {
                sql.append(" ").append(relation);
            }
        }
        //主键不为空, 则添加主键
        List params = new ArrayList();
        SqlParams condParams = makeConditionsSql(conditions, new StringBuffer(), sqlParams, params);
        sql.append(condParams.getSql());
        String groupByStr = conditions.getGroupBy();
        if (groupByStr != null) {
            sql.append(" group by ".concat(groupByStr));
        }
        String orderByStr = conditions.getOrderBy();
        if (orderByStr != null) {
            sql.append(orderByStr);
        }
        //处理分页
        if (StrUtil.notBlank(conditions.getLimit())) {
            sql.append(conditions.getLimit());
        }
        sqlParams.setSql(sql.toString());
        sqlParams.setParams(condParams.getParams());
        return sqlParams;
    }


    /**
     * 创建删除语句
     *
     * @param tableBean
     * @param conditions 条件属性对象
     */
    public static SqlParams createDeleteSql(TableBean tableBean, Conditions conditions) {
        SqlParams sqlParams = new SqlParams();
        if (tableBean == null) {
            throw new RuntimeException("没有找到ORM映射!");
        }
//        if (tableBean.getPrimaryKey() == null) {
//            throw new RuntimeException("没有找到@Id主键注解!");
//        }
        StringBuffer sql = new StringBuffer("delete from ");
        sql.append(tableBean.getTable());
        List params = new ArrayList();
        return makeConditionsSql(conditions, sql, sqlParams, params);
    }


    /**
     * 创建更新语句
     *
     * @param obj
     * @param conditions
     * @return
     */
    public static SqlParams createUpdateSql(Object obj, Conditions conditions) {
        SqlParams sqlParams = new SqlParams();
        Class<?> cls = obj.getClass();
        TableBean tableBean = findTableBean(cls);
        if (tableBean == null) {
            throw new RuntimeException(cls + " 没有找到ORM映射!");
        }
//        if (tableBean.getPrimaryKey() == null) {
//            throw new RuntimeException(cls + " 没有找到@Id主键注解!");
//        }
        if (cls.getDeclaredAnnotation(Table.class) == null) {
            throw new RuntimeException(obj + " 当前实体没有@Table注解, 不是实体映射类!");
        }
        StringBuffer sql = new StringBuffer("update ");
        sql.append(tableBean.getTable());
        List<String> fields = tableBean.getFields();
        List params = new ArrayList();
        for (int i = 0; i < fields.size(); i++) {
            if (i == 0) {
                sql.append(" set ");
            }
            String field = fields.get(i);
            Field declaredField = ReflectionUtils.findField(cls, StrCaseUtil.toCamelCase(field));
            ReflectionUtils.makeAccessible(declaredField);
            Object objValue = ReflectionUtils.getField(declaredField, obj);
            if (StrUtil.notNull(objValue)) {
                if (declaredField.getDeclaredAnnotation(Id.class) == null) {
                    sql.append(field).append("=?,");
                    params.add(objValue);
                } else {
                    conditions.and(field, Condition.EQ, objValue);
                }
            }
        }
        //删除最后一个","
        sql.delete(sql.length() - 1, sql.length());
        return makeConditionsSql(conditions, sql, sqlParams, params);
    }

    public static SqlParams createInsertSql(Object obj) {
        SqlParams sqlParams = new SqlParams();
        Class<?> cls = obj.getClass();
        TableBean tableBean = findTableBean(cls);
        if (tableBean == null) {
            throw new RuntimeException(cls + " 没有找到ORM映射!");
        }
//        if (tableBean.getPrimaryKey() == null) {
//            throw new RuntimeException(cls + " 没有找到@Id主键注解!");
//        }
        StringBuffer sql = new StringBuffer("insert into ");
        sql.append(tableBean.getTable()).append("(");
        List<String> fields = tableBean.getFields();
        StringBuffer valBuff = new StringBuffer();
        List params = new ArrayList();
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            Field declaredField = ReflectionUtils.findField(cls, StrCaseUtil.toCamelCase(field));
            ReflectionUtils.makeAccessible(declaredField);
            Object objValue = ReflectionUtils.getField(declaredField, obj);
            if (StrUtil.notNull(objValue)) {
                sql.append(field).append(",");
                valBuff.append("?").append(",");
                params.add(objValue);
            }
        }
        sql.delete(sql.length() - 1, sql.length());
        sql.append(") values(");

        valBuff.delete(valBuff.length() - 1, valBuff.length());
        sql.append(valBuff.toString());
        sql.append(")");
        sqlParams.setSql(sql.toString());
        sqlParams.setParams(params.toArray());
        return sqlParams;
    }

    /**
     * 生成条件sql, 到最后结果
     *
     * @param conditions
     * @param sql
     * @param sqlParams
     * @return
     */
    private static SqlParams makeConditionsSql(Conditions conditions, StringBuffer sql, SqlParams sqlParams, List params) {
        boolean isSelect = conditions.getRelations().size() > 0 ? true : false;
        List<Cond> conds = conditions.getConditions();
        if (conds.size() > 0) {
            sql.append(" where");
            for (int i = 0; i < conds.size(); i++) {
                Cond cond = conds.get(i);
                Object value = cond.getValue();
                //当数据值为数组时
                int inParamSize = 0;
                if (value.getClass().isArray()) {
                    Object[] objs = (Object[]) value;
                    for (Object objValue : objs) {
                        params.add(objValue);
                        inParamSize++;
                    }
                } else {
                    params.add(value);
                }
                if (i != 0) {
                    sql.append(cond.getSymbol().getValue());
                }
                String field = cond.getField();
                String qzStr = (isSelect ? (field.indexOf(".") != -1 ? "" : "_.") : "");
                if (cond.getCond() == Condition.IN) {
                    sql.append(" ").append(String.format(cond.getCond().getValue(), qzStr + StrCaseUtil.toUnderlineName(field)));
                    sql.append("(");
                    for (int j = 0; j < inParamSize; j++) {
                        sql.append("?,");
                    }
                    sql.delete(sql.length() - 1, sql.length());
                    sql.append(")");
                } else {
                    sql.append(" ").append(String.format(cond.getCond().getValue(), qzStr + StrCaseUtil.toUnderlineName(field)));
                }
            }
        }
        sqlParams.setSql(sql.toString());
        sqlParams.setParams(params.toArray());
        return sqlParams;
    }
}
