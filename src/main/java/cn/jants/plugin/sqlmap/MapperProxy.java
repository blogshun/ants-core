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
        TagElement tagElement = SqlXmlParser.getOptionType(sqlkey);
        Mapper mapper = targetCls.getDeclaredAnnotation(Mapper.class);
        Db db = DbManager.get(mapper.value());
        Class<?> methodReturnType = method.getReturnType();
        SqlParams sqlParams = SqlXmlParser.getPreparedStatement(sqlkey, args);

        String optionType = tagElement.getOptionType();
        String returnType = tagElement.getReturnType();
        //查询操作
        if ("select".equals(optionType)) {
//            if("jsonmap".equals(returnType)){
//
//            }else if("string".equals(returnType)){
//
//            }else if("int".equals(returnType)){
//
//            }else if("long".equals(returnType)){
//
//            }
            if (methodReturnType == List.class) {
                result = db.list(sqlParams.getSql(), sqlParams.getParams());
            } else {
                result = db.query(sqlParams.getSql(), sqlParams.getParams());
            }

        }
        //保存操作
        else if ("insert".equals(optionType)) {

        }
        //更新操作
        else if ("update".equals(optionType) || "delete".equals(optionType)) {

        }
        return result;
    }
}
