package com.ants.plugin.scheduler;

import com.ants.common.annotation.service.Service;
import com.ants.core.module.ServiceManager;
import com.ants.core.utils.GenerateUtil;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017/12/15
 */
public class SchedulerManager {

    private List<SchedulerBean> list;

    private ScheduledThreadPoolExecutor exec;


    public SchedulerManager(List<SchedulerBean> list){
        this.exec = new ScheduledThreadPoolExecutor(1);
        this.list = list;
    }

    public void start() {
        for(SchedulerBean scheduler: list){
            FixedDelay fixedDelay = scheduler.getFixedDelay();
            Class<?> cls = scheduler.getCls();
            Object object = null;
            try {
                object = cls.newInstance();
                if(object instanceof Runnable){
                    //检测是否有Service
                    if(cls.getDeclaredAnnotation(Service.class) != null){
                        String serName = cls.getName();
                        String key = GenerateUtil.createServiceKey(serName);
                        object = ServiceManager.getService(key);
                    }
                    Runnable target = (Runnable) object;
                    exec.scheduleWithFixedDelay(target, fixedDelay.initialDelay(), fixedDelay.delay(), fixedDelay.timeUnit());
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
                break;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
