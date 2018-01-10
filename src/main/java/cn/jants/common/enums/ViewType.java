package cn.jants.common.enums;

/**
 * @author MrShun
 * @version 1.0
 */
public enum ViewType {

    /**
     * BEETL http://ibeetl.com/
     */
    BEETL,
    /**
     * JSP
     */
    JSP,
    /**
     * FREEMARKER
     */
    FREEMARKER,
    /**
     * VELOCITY
     */
    VELOCITY;

    private Object config;

    private String templePath;

    public Object getConfig() {
        return config;
    }

    public String getTemplePath() {
        return templePath;
    }

    public ViewType setTemplePath(String templePath) {
        this.templePath = templePath;
        return this;
    }

    public ViewType setConfig(Object config) {
        this.config = config;
        return this;
    }
}