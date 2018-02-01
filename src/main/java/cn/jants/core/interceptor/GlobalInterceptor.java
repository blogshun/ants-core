package cn.jants.core.interceptor;

import cn.jants.core.ext.Interceptor;

/**
 * @author MrShun
 * @version 1.0
 */
public class GlobalInterceptor {

    /**
     * 拦截器
     */
    private Interceptor interceptor;

    /**
     * 前缀包名称
     */
    private String prefixPackage;

    /**
     * 前缀方法
     */
    private String prefixMethod = "";


    public GlobalInterceptor(Interceptor interceptor, String prefixPackage, String prefixMethod) {
        this.interceptor = interceptor;
        this.prefixPackage = prefixPackage;
        this.prefixMethod = prefixMethod;
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public String getPrefixPackage() {
        return prefixPackage;
    }

    public void setPrefixPackage(String prefixPackage) {
        this.prefixPackage = prefixPackage;
    }

    public String getPrefixMethod() {
        return prefixMethod;
    }

    public void setPrefixMethod(String prefixMethod) {
        this.prefixMethod = prefixMethod;
    }
}
