package cn.jants.core.ext;

import cn.jants.common.bean.Invocation;

import java.lang.reflect.InvocationTargetException;

/**
 * @author MrShun
 * @version 1.0
 */
public interface Interceptor {

    /**
     * 执行方法拦截器
     *
     * @param invo 目标对象
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    Object intercept(Invocation invo) throws InvocationTargetException, IllegalAccessException;
}
