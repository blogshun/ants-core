package com.ants.restful.request;

import com.ants.common.annotation.action.*;
import com.ants.common.enums.RequestMethod;
import com.ants.core.context.AppConstant;
import com.ants.core.ext.InitializingBean;
import com.ants.core.proxy.CglibProxy;
import com.ants.core.proxy.FiledBinding;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017-12-02
 */
public class ActionInitialization {

    /**
     * 主要是生成控制层映射
     * 获取classes里面的信息, 组装成RequestMappingBean
     *
     * @param ctls 类对象列表
     * @return
     */
    public static List<RequestMappingBean> createRequestMapping(List<Class<?>> ctls) {
        List<RequestMappingBean> result = new ArrayList<>();
        //用来校验是否存在相同初始化
        List<String> keys = new ArrayList<>();
        for (Class<?> ctl : ctls) {
            try {
                Object ctlObj = ctl.newInstance();
                // 处理实例化类里面的属性注解信息
                FiledBinding.initFiledValues(ctlObj);
                //采用CGLIB代理实例化service
                Object proxy = CglibProxy.createProxy(ctlObj);
                //初始化完成后调用init()实例化, 在注解方法之后
                if(proxy instanceof InitializingBean){
                    InitializingBean ib = (InitializingBean) proxy;
                    ib.afterPropertiesSet();
                }
                Controller ctlAnno = ctl.getDeclaredAnnotation(Controller.class);
                //得到该类下面的所有方法
                Method[] methods = ctl.getDeclaredMethods();
                for (Method method : methods) {
                    //排除静态方法和私有方法
                    if (!Modifier.isStatic(method.getModifiers()) && !method.getClass().isPrimitive()) {
                        RequestMethod requestType = null;
                        //得到该类下面的RequestMapping注解
                        POST post;
                        GET get;
                        PUT put;
                        DELETE delete;
                        //默认方法名称作为URL
                        String[] strUrls = new String[]{method.getName()};
                        //为POST注解
                        if ((post = method.getDeclaredAnnotation(POST.class)) != null) {
                            requestType = RequestMethod.POST;
                            strUrls = post.value();
                        }
                        //为GET注解
                        else if ((get = method.getDeclaredAnnotation(GET.class)) != null) {
                            requestType = RequestMethod.GET;
                            strUrls = get.value();
                        }
                        //为PUT注解
                        else if ((put = method.getDeclaredAnnotation(PUT.class)) != null) {
                            requestType = RequestMethod.PUT;
                            strUrls = put.value();
                        }
                        //为DELETE注解
                        else if ((delete = method.getDeclaredAnnotation(DELETE.class)) != null) {
                            requestType = RequestMethod.DELETE;
                            strUrls = delete.value();
                        }
                        String[] urls = join(ctlAnno.value(), formatURLs(strUrls, AppConstant.URL_REGEX_SUFFIX));
                        RequestMappingBean bean = new RequestMappingBean(urls, null, requestType, ctlObj, proxy, method);
                        for (String key : urls) {
                            if (keys.contains(key)) {
                                throw new RuntimeException(key + " 存在相同的URL链接, 初始化失败!");
                            }
                            keys.add(key);
                        }
                        result.add(bean);
                    }
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
                break;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                break;
            }
        }
        return result;
    }


    /**
     * 格式化URL字符串 如果没有没有/前缀则添加
     *
     * @param urls        url数组
     * @param regexSuffix url后缀名
     * @return
     */
    private static String[] formatURLs(String[] urls, String regexSuffix) {
        if (urls != null && urls.length != 0) {
            for (int i = 0; i < urls.length; i++) {
                String url = (urls[i].indexOf('/') != 0) ? "/".concat(urls[i]) : urls[i];
                if (url.indexOf(".", url.lastIndexOf("/")) == -1) {
                    urls[i] = url.trim() + regexSuffix;
                } else {
                    urls[i] = url.trim();
                }
            }
        }
        return urls;
    }

    /**
     * 往urls数组里面前面加上模块
     *
     * @param moduleStr 模块字符串
     * @param urls      url数组
     * @return
     */
    private static String[] join(String moduleStr, String[] urls) {
        if (moduleStr.indexOf('/') != 0 && !"".equals(moduleStr)) {
            moduleStr = "/" + moduleStr;
        }
        for (int i = 0; i < urls.length; i++) {
            urls[i] = moduleStr.trim() + urls[i];
        }
        return urls;
    }
}
