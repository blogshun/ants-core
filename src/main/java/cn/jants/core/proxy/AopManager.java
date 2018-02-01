package cn.jants.core.proxy;

import cn.jants.common.annotation.service.Aop;
import cn.jants.common.annotation.service.Uop;
import cn.jants.common.bean.Invocation;
import cn.jants.common.utils.CollectionUtil;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.ext.Interceptor;
import cn.jants.core.interceptor.GlobalInterceptor;
import cn.jants.core.module.InterceptorManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author MrShun
 * @version 1.0
 */
public class AopManager {

    private AopManager() {
    }

    public static Object handler(Object target, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        Object result;
        String packageName = target.getClass().getPackage().getName();
        String methodName = method.getName();
        GlobalInterceptor globalInterceptor = InterceptorManager.getGlobalInterceptor();
        if (globalInterceptor != null) {
            String prefixMethod = globalInterceptor.getPrefixMethod();
            if (packageName.startsWith(globalInterceptor.getPrefixPackage()) && methodName.startsWith(StrUtil.isBlank(prefixMethod) ? "" : prefixMethod)) {
                Interceptor interceptor = globalInterceptor.getInterceptor();
                Invocation invo = new Invocation(target, method, args, 0, new Class[]{interceptor.getClass()});
                result = invo.invoke();
            }
        }
        //=================== 处理AOP ======================
        //取方法上面的AOP注解
        Aop methodAop = method.getDeclaredAnnotation(Aop.class);
        //取方法上面的UOP注解
        Uop methodUop = method.getDeclaredAnnotation(Uop.class);
        //取类上面的AOP注解
        Aop classAop = target.getClass().getDeclaredAnnotation(Aop.class);

        if (methodAop != null || classAop != null || methodUop != null) {
            //方法上面的AOP注解优先级大于类上面的优先级
            Class[] values = CollectionUtil.union(classAop == null ? null : classAop.value(), methodAop == null ? null : methodAop.value());
            if (values != null && values.length > 0) {
                values = CollectionUtil.minus(values, methodUop == null ? null : methodUop.value());
            }
            //从0开始调用
            Invocation cinvo = new Invocation(target, method, args, 0, values);
            result = cinvo.invoke();
        } else {
            result = method.invoke(target, args);
        }
        return result;
    }
}
