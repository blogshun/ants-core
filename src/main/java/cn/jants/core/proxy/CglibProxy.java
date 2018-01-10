package cn.jants.core.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

/**
 * CGLIB动态代理
 *
 * @author MrShun
 * @version 1.0
 */
public class CglibProxy implements InvocationHandler {

    private Object target;

    /**
     * 创建一个织入器
     */
    private CglibProxy(Object target){
        this.target = target;
    }

    /**
     * 创建代理对象
     *
     * @return
     */
    public static <T> T createProxy(Object target) {
        //创建一个织入器
        Enhancer enhancer = new Enhancer();
        // 2.设置被代理类字节码，CGLIB根据字节码生成被代理类的子类
        enhancer.setSuperclass(target.getClass());
        // 3.//设置回调函数，即一个方法拦截
        enhancer.setCallback(new CglibProxy(target));
        // 4.创建代理:
        return (T) enhancer.create();
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //取消方法类型检查提高性能
        method.setAccessible(true);
        CacheManager cacheManager = new CacheManager(target, method, args);
        if (cacheManager.isOpened() && cacheManager.existCache()) {
            return cacheManager.getResult();
        }
        TransactionManager tx = new TransactionManager(target, method);
        Object result;
        try {
            result = AopManager.handler(target, method, args);
            tx.commit();
            if (cacheManager.isOpened()) {
                cacheManager.setCache(result);
            }
        } catch (Exception e) {
            tx.rollback();
            throw e.getCause();
        }
        return result;
    }
}
