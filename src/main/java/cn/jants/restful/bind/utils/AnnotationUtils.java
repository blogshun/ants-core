package cn.jants.restful.bind.utils;

import cn.jants.restful.bind.BridgeMethodResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * @author Administrator
 */
public class AnnotationUtils {

    static final String VALUE = "value";

    private static final Map<Class, Boolean> annotatedInterfaceCache = new WeakHashMap();

    public static <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType) {
        Annotation annotation = getAnnotation(method, annotationType);
        Class cl = method.getDeclaringClass();
        if (annotation == null) {
            annotation = searchOnInterfaces(method, annotationType, cl.getInterfaces());
        }

        while (annotation == null) {
            cl = cl.getSuperclass();
            if (cl == null || cl == Object.class) {
                break;
            }

            try {
                Method equivalentMethod = cl.getDeclaredMethod(method.getName(), method.getParameterTypes());
                annotation = getAnnotation(equivalentMethod, annotationType);
                if (annotation == null) {
                    annotation = searchOnInterfaces(method, annotationType, cl.getInterfaces());
                }
            } catch (NoSuchMethodException var5) {
                ;
            }
        }

        return (A) annotation;
    }

    public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
        Method resolvedMethod = BridgeMethodResolver.findBridgedMethod(method);
        Annotation ann = resolvedMethod.getAnnotation(annotationType);
        if (ann == null) {
            Annotation[] var7;
            int var6 = (var7 = resolvedMethod.getAnnotations()).length;

            for (int var5 = 0; var5 < var6; ++var5) {
                Annotation metaAnn = var7[var5];
                ann = metaAnn.annotationType().getAnnotation(annotationType);
                if (ann != null) {
                    break;
                }
            }
        }

        return (A) ann;
    }

    private static <A extends Annotation> A searchOnInterfaces(Method method, Class<A> annotationType, Class[] ifcs) {
        Annotation annotation = null;
        Class[] var7 = ifcs;
        int var6 = ifcs.length;

        for (int var5 = 0; var5 < var6; ++var5) {
            Class iface = var7[var5];
            if (isInterfaceWithAnnotatedMethods(iface)) {
                try {
                    Method equivalentMethod = iface.getMethod(method.getName(), method.getParameterTypes());
                    annotation = getAnnotation(equivalentMethod, annotationType);
                } catch (NoSuchMethodException var9) {
                    ;
                }

                if (annotation != null) {
                    break;
                }
            }
        }

        return (A) annotation;
    }

    private static boolean isInterfaceWithAnnotatedMethods(Class<?> iface) {
        Map var1 = annotatedInterfaceCache;
        synchronized(annotatedInterfaceCache) {
            Boolean flag = (Boolean)annotatedInterfaceCache.get(iface);
            if(flag != null) {
                return flag.booleanValue();
            } else {
                boolean found = false;
                Method[] var7;
                int var6 = (var7 = iface.getMethods()).length;

                for(int var5 = 0; var5 < var6; ++var5) {
                    Method ifcMethod = var7[var5];
                    if(ifcMethod.getAnnotations().length > 0) {
                        found = true;
                        break;
                    }
                }

                annotatedInterfaceCache.put(iface, Boolean.valueOf(found));
                return found;
            }
        }
    }

}
