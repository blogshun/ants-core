package com.ants.common.bean;

import com.ants.core.ext.Interceptor;
import com.ants.core.module.InterceptorManager;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-05-13
 */
public class Invocation {

    /**
     * 代理目标对象
     */
    private Object object;

    /**
     * 代理目标method
     */
    private Method method;

    /**
     * 代理目标args
     */
    private Object[] args;

    /**
     * 当前拦截器在注解里面的序号
     */
    private int index;

    private Class<? extends Interceptor>[] interceptors;

    public Invocation(Object object, Method method, Object[] args, int index, Class<? extends Interceptor>[] interceptors) {
        this.object = object;
        this.method = method;
        this.args = args;
        this.index = index;
        this.interceptors = interceptors;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Object invoke() throws InvocationTargetException, IllegalAccessException {

        Object result = null;
        //不是最后一个拦截器, 执行下一个拦截器
        if (index < interceptors.length) {
            Interceptor currentInterceptor = InterceptorManager.getSameClass(interceptors[index]);
            try {
                //拦截器列表里面没有发现, 实例化默认构造实例
                if (currentInterceptor == null) {
                    currentInterceptor = interceptors[index].newInstance();
                }
            } catch (InstantiationException e) {
                throw new RuntimeException("拦截器实例化失败, 如果有构造方法必须得在Config里面配置!");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            result = currentInterceptor.intercept(new Invocation(object, method, args, index + 1, interceptors));
        } else { //直接执行目标方法
            result = method.invoke(object, args);
        }
        return result;
    }
}
