package com.ants.restful.bind;


import com.ants.restful.bind.utils.ClassUtils;
import com.ants.restful.bind.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-05-04
 */
public abstract class BridgeMethodResolver {
    public BridgeMethodResolver() {
    }

    public static Method findBridgedMethod(Method bridgeMethod) {
        if (bridgeMethod != null && bridgeMethod.isBridge()) {
            ArrayList candidateMethods = new ArrayList();
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(bridgeMethod.getDeclaringClass());
            Method[] bridgedMethod = methods;
            int var4 = methods.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Method candidateMethod = bridgedMethod[var5];
                if (isBridgedCandidateFor(candidateMethod, bridgeMethod)) {
                    candidateMethods.add(candidateMethod);
                }
            }

            if (candidateMethods.size() == 1) {
                return (Method) candidateMethods.get(0);
            } else {
                Method var7 = searchCandidates(candidateMethods, bridgeMethod);
                if (var7 != null) {
                    return var7;
                } else {
                    return bridgeMethod;
                }
            }
        } else {
            return bridgeMethod;
        }
    }

    private static boolean isBridgedCandidateFor(Method candidateMethod, Method bridgeMethod) {
        return !candidateMethod.isBridge() && !candidateMethod.equals(bridgeMethod) && candidateMethod.getName().equals(bridgeMethod.getName()) && candidateMethod.getParameterTypes().length == bridgeMethod.getParameterTypes().length;
    }

    private static Method searchCandidates(List<Method> candidateMethods, Method bridgeMethod) {
        if (candidateMethods.isEmpty()) {
            return null;
        } else {
            Method previousMethod = null;
            boolean sameSig = true;

            Method candidateMethod;
            for (Iterator var4 = candidateMethods.iterator(); var4.hasNext(); previousMethod = candidateMethod) {
                candidateMethod = (Method) var4.next();
                if (isBridgeMethodFor(bridgeMethod, candidateMethod, bridgeMethod.getDeclaringClass())) {
                    return candidateMethod;
                }

                if (previousMethod != null) {
                    sameSig = sameSig && Arrays.equals(candidateMethod.getGenericParameterTypes(), previousMethod.getGenericParameterTypes());
                }
            }

            return sameSig ? (Method) candidateMethods.get(0) : null;
        }
    }

    static boolean isBridgeMethodFor(Method bridgeMethod, Method candidateMethod, Class<?> declaringClass) {
        if (isResolvedTypeMatch(candidateMethod, bridgeMethod, declaringClass)) {
            return true;
        } else {
            Method method = findGenericDeclaration(bridgeMethod);
            return method != null && isResolvedTypeMatch(method, candidateMethod, declaringClass);
        }
    }

    private static Method findGenericDeclaration(Method bridgeMethod) {
        for (Class superclass = bridgeMethod.getDeclaringClass().getSuperclass(); superclass != null && !Object.class.equals(superclass); superclass = superclass.getSuperclass()) {
            Method interfaces = searchForMatch(superclass, bridgeMethod);
            if (interfaces != null && !interfaces.isBridge()) {
                return interfaces;
            }
        }

        Class[] var8 = ClassUtils.getAllInterfacesForClass(bridgeMethod.getDeclaringClass());
        Class[] var3 = var8;
        int var4 = var8.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            Class ifc = var3[var5];
            Method method = searchForMatch(ifc, bridgeMethod);
            if (method != null && !method.isBridge()) {
                return method;
            }
        }

        return null;
    }

    private static boolean isResolvedTypeMatch(Method genericMethod, Method candidateMethod, Class<?> declaringClass) {
        Type[] genericParameters = genericMethod.getGenericParameterTypes();
        Class[] candidateParameters = candidateMethod.getParameterTypes();
        if (genericParameters.length != candidateParameters.length) {
            return false;
        } else {
            for (int i = 0; i < candidateParameters.length; ++i) {
                ResolvableType genericParameter = ResolvableType.forMethodParameter(genericMethod, i, declaringClass);
                Class candidateParameter = candidateParameters[i];
                if (candidateParameter.isArray() && !candidateParameter.getComponentType().equals(genericParameter.getComponentType().resolve(Object.class))) {
                    return false;
                }

                if (!candidateParameter.equals(genericParameter.resolve(Object.class))) {
                    return false;
                }
            }

            return true;
        }
    }

    private static Method searchForMatch(Class<?> type, Method bridgeMethod) {
        return ReflectionUtils.findMethod(type, bridgeMethod.getName(), bridgeMethod.getParameterTypes());
    }

    public static boolean isVisibilityBridgeMethodPair(Method bridgeMethod, Method bridgedMethod) {
        return bridgeMethod == bridgedMethod ? true : Arrays.equals(bridgeMethod.getParameterTypes(), bridgedMethod.getParameterTypes()) && bridgeMethod.getReturnType().equals(bridgedMethod.getReturnType());
    }
}

