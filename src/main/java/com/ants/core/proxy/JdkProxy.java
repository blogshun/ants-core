package com.ants.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017/12/23
 */
public class JdkProxy implements InvocationHandler {

    private Object target;

    public JdkProxy(Object target) {
        this.target = target;
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                target.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //代理之前
        System.out.println("代理之前...");
        Object result = method.invoke(target, args);
        System.out.println("result:" + result);
        System.err.println("代理之后...");
        return result;
    }
}
