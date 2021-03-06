package cn.jants.core.module;

import cn.jants.common.bean.Log;
import cn.jants.core.utils.ScanUtil;
import cn.jants.restful.request.ActionInitialization;
import cn.jants.common.annotation.action.Controller;
import cn.jants.core.context.AppConstant;
import cn.jants.restful.request.RequestMappingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 */
final public class RequestMappingManager {


    private static final List<RequestMappingBean> REQUEST_MAPPINGS = new ArrayList<>();

    private static final List<String[]> REQUESTS = new ArrayList<>();

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
            REQUESTS.add(bean.getUrls());
            REQUEST_MAPPINGS.add(bean);
        }
        if (AppConstant.DEBUG) {
            Log.debug(">>> 共计 {} 个Api", beans.size());
        }
    }

    public static List<RequestMappingBean> getRequestMappingManager() {
        return REQUEST_MAPPINGS;
    }

    public static List<String[]> getRequests() {
        return REQUESTS;
    }
}
