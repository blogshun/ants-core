package cn.jants.core.module;


import cn.jants.core.ext.Interceptor;
import cn.jants.core.interceptor.GlobalInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 拦截器管理
 *
 * @author MrShun
 * @version 1.0
 */
final public class InterceptorManager {

    private final static List<Interceptor> INTERCEPTORS = new ArrayList<>();

    private static GlobalInterceptor global;


    public InterceptorManager add(Interceptor interceptor) {
        if (interceptor == null) {
            throw new IllegalArgumentException("interceptor can not be null");
        }
        INTERCEPTORS.add(interceptor);
        return this;
    }

    public InterceptorManager addGlobal(Interceptor interceptor, String prefixPackage, String prefixMethod) {
        global = new GlobalInterceptor(interceptor, prefixPackage, prefixMethod);
        INTERCEPTORS.add(global.getInterceptor());
        return this;
    }

    public static GlobalInterceptor getGlobalInterceptor(){
        return global;
    }

    public InterceptorManager addGlobal(Interceptor interceptor, String pkg){
        return addGlobal(interceptor, pkg, null);
    }

    /**
     * 在对象列表中查找类型一样的对象
     *
     * @param clsType 对象类型
     * @param <T>
     * @return
     */
    public static <T> T getSameClass(Class<T> clsType) {
        for (Object obj : INTERCEPTORS) {
            if (obj.getClass() == clsType) {
                return (T) obj;
            }
        }
        return null;
    }
}
