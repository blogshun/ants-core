package cn.jants.common.bean;

import cn.jants.common.utils.CollectionUtil;
import com.alibaba.fastjson.JSON;
import cn.jants.common.annotation.service.Aop;
import cn.jants.common.annotation.service.Uop;
import cn.jants.plugin.cache.Cacheable;
import cn.jants.restful.request.RequestMappingBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志打印工具
 * 注意：log4j配置文件中需要去掉当前日志工具输出
 *
 * @author MrShun
 * @version 1.0
 *          Date 2017-06-12
 */
public class Log {

    private static final String LOG_NAME = Log.class.getName();

    private static final Logger LOG = LoggerFactory.getLogger(Log.class);

    private static final ThreadLocal<SimpleDateFormat> SDF = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public static void info(String format, Object... arguments) {
        format = getStackMsg(Thread.currentThread().getStackTrace()).concat(":").concat(format);
        LOG.info(format, arguments);
    }

    public static void debug(String format, Object... arguments) {
        format = getStackMsg(Thread.currentThread().getStackTrace()).concat(":").concat(format);
        LOG.debug(format, arguments);
    }

    public static void error(String format, Object... arguments) {
        format = getStackMsg(Thread.currentThread().getStackTrace()).concat(":").concat(format);
        LOG.error(format, arguments);
    }

    public static void warn(String format, Object... arguments) {
        format = getStackMsg(Thread.currentThread().getStackTrace()).concat(":").concat(format);
        LOG.warn(format, arguments);
    }

    /**
     * Report the log
     *
     * @param target  目标url
     * @param request request
     * @param bean    mapping对象
     */
    public static final void action(String target, HttpServletRequest request, RequestMappingBean bean) {
        StringBuilder sb = new StringBuilder("\nAnts log report -------- ").append(SDF.get().format(new Date())).append(" ------------------------------\n");
        sb.append("Url         : ").append(request.getMethod()).append(" ").append(target).append("\n");
        Class<?> c = bean.getObject().getClass();
        sb.append("Controller  : ").append(String.valueOf(c.getName())).append(".".concat(bean.getMethod().getName())).append(" (").append(c.getSimpleName()).append(".java:1)").append("\n");
        Method m = bean.getMethod();
        //TODO 判断是否有上传文件打印出路径
        if ("application/json".equals(request.getContentType())) {
            sb.append("Parameter   : ").append(bean.getParams()).append("\n");
        } else {
            sb.append("Parameter   : ").append(JSON.toJSON(request.getParameterMap())).append("\n");
        }
        Cacheable cache = m.getAnnotation(Cacheable.class);
        if (cache != null) {
            sb.append("Cache       : ").append("true").append("\n");
        }
        Aop classAop = c.getAnnotation(Aop.class);
        Aop methodAop = m.getAnnotation(Aop.class);
        Uop methodUop = m.getAnnotation(Uop.class);
        if (classAop != null || methodAop != null) {
            Class[] interceptors = CollectionUtil.union(classAop == null ? null : classAop.value(), methodAop == null ? null : methodAop.value());
            if (interceptors != null && interceptors.length > 0) {
                interceptors = CollectionUtil.minus(interceptors, methodUop == null ? null : methodUop.value());
                if (interceptors.length > 0) {
                    sb.append("Interceptor : ");
                }
            }
            for (int i = 0; i < interceptors.length; i++) {
                if (i != 0) {
                    sb.append("              ");
                }
                sb.append(interceptors[i].getName()).append(" (").append(interceptors[i].getSimpleName()).append(".java:1)").append("\n");
            }

        }

        sb.append("---------------------------------------------------------------------------\n");

        System.out.println(sb.toString());
    }

    /**
     * 根据堆栈信息得到源代码行信息
     * <p>
     * 原理：本工具类的堆栈下一行即为源代码的最原始堆栈。
     *
     * @param ste 堆栈信息
     * @return 调用输出日志的代码所在的类.方法.代码行的相关信息
     * <p>
     * 如：com.MyClass 类里的 fun()方法调用了Logs.debug("test");
     * <p>
     * 则堆栈信息为: com.MyClass.fun(MyClass.java 代码行号)
     */
    private static String getStackMsg(StackTraceElement[] ste) {
        if (ste == null) {
            return null;
        }
        boolean srcFlag = false;
        for (int i = 0; i < ste.length; i++) {
            StackTraceElement s = ste[i];
            // 如果上一行堆栈代码是本类的堆栈，则该行代码则为源代码的最原始堆栈。
            if (srcFlag) {
                return s == null ? "" : s.toString();
            }
            // 定位本类的堆栈
            if (LOG_NAME.equals(s.getClassName())) {
                srcFlag = true;
            }
        }
        return null;
    }
}
