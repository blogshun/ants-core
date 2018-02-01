package cn.jants.plugin.scheduler;

import cn.jants.common.bean.Log;
import cn.jants.core.ext.Plugin;
import cn.jants.core.module.ServiceManager;
import cn.jants.common.annotation.service.Service;
import cn.jants.core.utils.GenerateUtil;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author MrShun
 * @version 1.0
 */
public class SchedulerPlugin implements Plugin{

    private List<SchedulerBean> list;

    private ScheduledThreadPoolExecutor exec;


    public SchedulerPlugin(List<SchedulerBean> list){
        this.exec = new ScheduledThreadPoolExecutor(1);
        this.list = list;
    }

    @Override
    public boolean start() {
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
        return true;
    }

    @Override
    public boolean destroy() {
        exec.shutdown();
        Log.debug("ScheduledThreadPoolExecutor 任务调度已销毁 ...");
        return true;
    }
}
