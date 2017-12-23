package com.ants.core.module;

import com.ants.common.annotation.service.Service;
import com.ants.common.bean.Log;
import com.ants.core.context.AppConstant;
import com.ants.core.ext.InitializingBean;
import com.ants.core.proxy.CglibProxy;
import com.ants.core.proxy.FiledBinding;
import com.ants.core.utils.GenerateUtil;
import com.ants.core.utils.ScanUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务实例管理
 *
 * @author MrShun
 * @version 1.0
 * @Date 2017-11-16
 */
final public class ServiceManager {

    private static final Map<String, Object> SERVICES = new ConcurrentHashMap<>();

    /**
     * 注册Service
     *
     * @param packages 包路径
     */
    public static void register(String... packages) {
        List<Class<?>> sers = ScanUtil.findScanClass(packages, Service.class);
        for (Class ser : sers) {
            String serName = ser.getName();
            //生成 Service Key
            String key = GenerateUtil.createServiceKey(serName);
            if (AppConstant.DEBUG) {
                Log.debug(">>> {} :: Generator Success !", key);
            }
            if (!SERVICES.containsKey(key)) {
                try {
                    Object serObj = ser.newInstance();
                    // 处理实例化类里面的属性注解信息
                    FiledBinding.initFiledValues(serObj);
                    //采用CGLIB代理实例化service
                    Object proxy = CglibProxy.createProxy(serObj);
                    //初始化完成后调用init()实例化, 在注解方法之后
                    if (proxy instanceof InitializingBean) {
                        InitializingBean ib = (InitializingBean) proxy;
                        ib.afterPropertiesSet();
                    }
                    SERVICES.put(key, proxy);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
        if (AppConstant.DEBUG) {
            Log.debug(">>> 共计 {} 个服务", sers.size());
        }
    }

    public static Object getService(String key) {
        return SERVICES.get(key);
    }

    public static Object setService(String key, Object object) {
        return SERVICES.put(key, object);
    }

    public static <T> T getService(Class<T> cls) {
        T object = null;
        for (Map.Entry<String, Object> entry : SERVICES.entrySet()) {
            object = (T) entry.getValue();
            if (cls.isInstance(object)) {
                break;
            }
        }
        return object;
    }

}
