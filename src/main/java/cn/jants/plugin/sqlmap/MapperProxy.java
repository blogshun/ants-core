package cn.jants.plugin.sqlmap;

import cn.jants.common.bean.JsonMap;
import cn.jants.common.bean.PageConditions;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.module.DbManager;
import cn.jants.plugin.db.Db;
import cn.jants.plugin.sqlmap.annotation.Mapper;
import cn.jants.plugin.sqlmap.annotation.P;
import cn.jants.plugin.sqlmap.annotation.Sql;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 */
public class MapperProxy implements InvocationHandler {

    private String mapperName;

    private Class<?> targetCls;

    public MapperProxy(String mapperName, Class targetCls) {
        this.mapperName = mapperName;
        this.targetCls = targetCls;
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{targetCls}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        String optionType, resultType;
        String sqlKey = mapperName.concat(".").concat(method.getName());
        Mapper mapper = targetCls.getDeclaredAnnotation(Mapper.class);
        Db db = DbManager.get(mapper.value());
        Class<?> methodReturnType = method.getReturnType();

        SqlParams sqlParams;
        Sql sql = method.getDeclaredAnnotation(Sql.class);
        if (sql == null || StrUtil.isBlank(sql.value())) {
            TagElement tagElement = SqlParser.getOptionType(sqlKey);
            optionType = tagElement.getOptionType();
            resultType = tagElement.getResultType();
            if (args == null) {
                sqlParams = SqlParser.getPreparedStatement(sqlKey);
            } else if (args.length == 1) {
                Object param = args[0];
                Class<?> cls = param.getClass();
                if (cls.getClassLoader() != null && !(param instanceof Map)) {
                    JsonMap map = JsonMap.newJsonMap();
                    Field[] fields = cls.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        Object value = field.get(param);
                        if (StrUtil.notNull(value)) {
                            map.put(field.getName(), value);
                        }
                    }
                    sqlParams = SqlParser.getPreparedStatement(sqlKey, map);
                } else {
                    sqlParams = SqlParser.getPreparedStatement(sqlKey, param);
                }
            } else {
                JsonMap params = JsonMap.newJsonMap();
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < args.length; i++) {
                    P p = parameters[i].getDeclaredAnnotation(P.class);
                    if (p != null) {
                        params.put(p.value(), args[i]);
                    } else {
                        throw new RuntimeException("接口参数超过1个必须采用@P注解绑定!");
                    }
                }
                sqlParams = SqlParser.getPreparedStatement(sqlKey, params);
            }

        } else {
            sqlParams = new SqlParams(sql.value(), args);
            resultType = sql.resultType();
            optionType = sql.type().getValue();
        }


        //查询操作
        if ("select".equals(optionType)) {
            //当是List集合时
            if (methodReturnType == List.class) {
                if (StrUtil.isBlank(resultType)) {
                    throw new RuntimeException("List返回值必须配置resultType类型!");
                }
                if ("map".equals(resultType) || "hasmap".equals(resultType) || "jsonmap".equals(resultType)) {
                    PageConditions pageConditions = Paging.getPageConditions();
                    if (pageConditions == null) {
                        result = db.list(sqlParams.getSql(), sqlParams.getParams());
                    } else {
                        result = db.page(sqlParams.getSql(), pageConditions);
                    }
                } else if ("long".equals(resultType) || "integer".equals(resultType)
                        || "string".equals(resultType)) {
                    result = db.listOne(sqlParams.getSql(), sqlParams.getParams());
                } else {
                    String idKey = mapperName.concat(".").concat(resultType);
                    String type = SqlParser.getResultType(idKey);
                    Class cls = Class.forName(StrUtil.isBlank(type) ? resultType : type);
                    PageConditions pageConditions = Paging.getPageConditions();
                    if (pageConditions == null) {
                        result = db.list(sqlParams.getSql(), cls, sqlParams.getParams());
                    } else {
                        result = db.page(sqlParams.getSql(), cls, pageConditions);
                    }
                }
            }
            //当是JsonMap时
            else if (methodReturnType == JsonMap.class || methodReturnType == HashMap.class || methodReturnType == Map.class) {
                result = db.query(sqlParams.getSql(), sqlParams.getParams());
            }
            //当是基本数据类型时
            else if (methodReturnType == Long.class || methodReturnType == long.class
                    || methodReturnType == Integer.class || methodReturnType == int.class
                    || methodReturnType == String.class) {
                result = db.queryOne(sqlParams.getSql(), sqlParams.getParams());
            }
            //当是实体类时
            else if (methodReturnType.getClassLoader() != null) {
                result = db.query(sqlParams.getSql(), methodReturnType, sqlParams.getParams());
            }

        }
        //保存操作
        else if ("insert".equals(optionType)) {
            if (methodReturnType == void.class) {
                db.insert(sqlParams.getSql(), sqlParams.getParams());
            } else {
                result = db.insertReturnKey(sqlParams.getSql(), sqlParams.getParams());
            }
        }
        //更新操作
        else if ("update".equals(optionType) || "delete".equals(optionType)) {
            if (methodReturnType == void.class) {
                db.update(sqlParams.getSql(), sqlParams.getParams());
            } else {
                result = db.update(sqlParams.getSql(), sqlParams.getParams());
            }
        }
        return result;
    }
}
