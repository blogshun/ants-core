package com.ants.plugin.scheduler;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017/12/15
 */
public class SchedulerBean {

    private FixedDelay fixedDelay;

    private Class<?> cls;

    public SchedulerBean(FixedDelay fixedDelay, Class<?> cls) {
        this.fixedDelay = fixedDelay;
        this.cls = cls;
    }

    public FixedDelay getFixedDelay() {
        return fixedDelay;
    }

    public void setFixedDelay(FixedDelay fixedDelay) {
        this.fixedDelay = fixedDelay;
    }

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }
}
