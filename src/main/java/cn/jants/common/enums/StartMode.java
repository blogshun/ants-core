package cn.jants.common.enums;

/**
 * 启动模式
 *
 * @author MrShun
 * @version 1.0
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
