package cn.jants.core.context;

import cn.jants.core.module.Constant;
import cn.jants.core.module.HandlerManager;
import cn.jants.core.module.InterceptorManager;
import cn.jants.core.module.PluginManager;

/**
 * @author MrShun
 * @version 1.0
 */
public class AppConfiguration {

    /**
     * 配置基本常量
     *
     * @param constant
     */
    public void configConstant(Constant constant) {
    }

    /**
     * 插件配置
     *
     * @param plugins
     */
    public void configPlugin(PluginManager plugins) {
    }

    /**
     * Handler配置
     *
     * @param handlers
     */
    public void configHandler(HandlerManager handlers) {
    }

    /**
     * 拦截器配置
     *
     * @param interceptors
     */
    public void configInterceptor(InterceptorManager interceptors) {
    }
}
