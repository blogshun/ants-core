package com.ants.restful.request;

import com.ants.common.enums.RequestMethod;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author MrShun
 * @version 1.0
 */
public class RequestMappingBean {

    /**
     * 请求多配置URLS
     */
    private String[] urls;

    /**
     * 匹配到的目标配置URL
     */
    private String currentUrl;

    /**
     * 请求类型
     */
    private RequestMethod requestType;

    /**
     * 已经实例化了的Class对象
     */
    private final Object object;

    /**
     * 代理对象
     */
    private final Object proxy;

    /**
     * 目标Class对象方法
     */
    private final Method method;

    /**
     * 请求参数
     */
    private Object params;


    public RequestMappingBean(String[] urls, String currentUrl, RequestMethod requestType, Object object, Object proxy, Method method) {
        this.urls = urls;
        this.currentUrl = currentUrl;
        this.requestType = requestType;
        this.object = object;
        this.proxy = proxy;
        this.method = method;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public String[] getUrls() {
        return urls;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public RequestMethod getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestMethod requestType) {
        this.requestType = requestType;
    }

    public Object getObject() {
        return object;
    }

    public Object getProxy() {
        return proxy;
    }

    public Method getMethod() {
        return method;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "RequestMappingBean{" +
                "urls=" + Arrays.toString(urls) +
                ", requestType=" + requestType +
                ", method=" + method +
                '}';
    }
}
