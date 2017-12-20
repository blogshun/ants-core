package com.ants.core.module;

import com.ants.common.annotation.action.Controller;
import com.ants.common.bean.Log;
import com.ants.core.context.AppConstant;
import com.ants.core.utils.ScanUtil;
import com.ants.restful.request.ActionInitialization;
import com.ants.restful.request.RequestMappingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-04-27
 */
final public class RequestMappingManager {


    private static final List<RequestMappingBean> REQUEST_MAPPINGS = new ArrayList<>();


    /**
     * 注册RequestMapping
     *
     * @param packages 包路径
     */
    public static void register(String... packages) {
        List<Class<?>> ctls = ScanUtil.findScanClass(packages, Controller.class);
        List<RequestMappingBean> beans = ActionInitialization.createRequestMapping(ctls);
        for (RequestMappingBean bean : beans) {
            if (AppConstant.DEBUG) {
                Log.debug(">>> {} :: Generator Success !", bean.toString());
            }
            REQUEST_MAPPINGS.add(bean);
        }
        if (AppConstant.DEBUG) {
            Log.debug(">>> 共计 {} 个Api", beans.size());
        }
    }

    public static List<RequestMappingBean> getRequestMappingManager() {
        return REQUEST_MAPPINGS;
    }
}
