package cn.jants.plugin.sqlmap;

import cn.jants.common.annotation.service.Mapper;
import cn.jants.core.module.DbManager;
import cn.jants.plugin.db.Db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2018-01-17
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
        //代理之前
        System.out.println("代理之前...");
        System.out.println(args);
        String sqlkey = mapperName.concat(".").concat(method.getName());
        String optionType = SqlXmlParser.getType(sqlkey);
        Mapper mapper = targetCls.getAnnotation(Mapper.class);
        Db db = DbManager.get(mapper.value());
        Class<?> returnType = method.getReturnType();
        SqlParams sqlParams = SqlXmlParser.getPreparedStatement(sqlkey, args);
        //查询操作
        if ("select".equals(optionType)) {
            if (returnType == List.class) {
                result = db.list(sqlParams.getSql(), sqlParams.getParams());
            } else {
                result = db.query(sqlParams.getSql(), sqlParams.getParams());
            }
        }
        //保存操作
        else if ("insert".equals(optionType)) {

        }
        //更新操作
        else if ("update".equals(optionType)) {

        }
        return result;
    }
}
