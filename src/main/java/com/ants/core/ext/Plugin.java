package com.ants.core.ext;

/**
 * 插件扩展
 *
 * @author MrShun
 * @version 1.0
 */
public interface Plugin {

    /**
     * 开启插件
     * @return
     * @throws Exception
     */
    boolean start() throws Exception;

    /**
     * 销毁插件
     *
     * @return
     */
    boolean destroy();
}
