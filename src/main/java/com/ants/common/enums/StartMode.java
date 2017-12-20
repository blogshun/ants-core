package com.ants.common.enums;

/**
 * 启动模式
 *
 * @author MrShun
 * @version 1.0
 * @Date 2017-11-19
 */
public enum StartMode {
    /**
     * Jar模式运行, 内嵌容器
     */
    JAR,
    /**
     * WAR解压运行, 外置容器
     */
    WAR;
}
