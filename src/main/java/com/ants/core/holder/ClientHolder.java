package com.ants.core.holder;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017/12/19
 */
public class ClientHolder {

    /**
     * 获取ClientRequest对象
     *
     * @return
     */
    public static ClientRequest getClientRequest() {
        return ContextRequestManager.get();
    }

    /**
     * 获取request对象
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        return ContextRequestManager.get().getRequest();
    }

    public static ServletContext getContext() {
        return ContextRequestManager.get().getRequest().getServletContext();
    }

    public static HttpSession getSession() {
        return ContextRequestManager.get().getRequest().getSession();
    }

    /**
     * 获取response对象
     *
     * @return
     */
    public static HttpServletResponse getResponse() {
        return ContextRequestManager.get().getResponse();
    }

    /**
     * 获取Header信息
     *
     * @param key
     * @return
     */
    public static String getHeader(String key) {
        return ContextRequestManager.get().getRequest().getHeader(key);
    }

    /**
     * 获取头部UserToken
     * @return
     */
    public static String getUserToken(){
        return getHeader("User-Token");
    }
    /**
     * 获取UserAgent信息
     *
     * @return
     */
    public static String getUserAgent() {
        HttpServletRequest request = ContextRequestManager.get().getRequest();
        return request.getHeader("User-Agent");
    }

    /**
     * 获取Ip信息
     *
     * @return
     */
    public static String getIp() {
        HttpServletRequest request = ContextRequestManager.get().getRequest();
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取UserAgent信息
     *
     * @return
     */
    public static String getContextPath() {
        HttpServletRequest request = ContextRequestManager.get().getRequest();
        return request.getContextPath();
    }

    /**
     * 获取SessionId信息
     *
     * @return
     */
    public static String getSessionId() {
        HttpServletRequest request = ContextRequestManager.get().getRequest();
        return request.getSession().getId();
    }

    /**
     * 写入Cookie值 一般是4k大小
     *
     * @param key     键
     * @param value   值
     * @param seconds 时间
     */
    public static void setCookie(String key, String value, int seconds) {
        Cookie userCookie = new Cookie(key, value);
        userCookie.setMaxAge(seconds);
        userCookie.setPath("/");
        getResponse().addCookie(userCookie);
    }

    public static Cookie getCookie(String key) {
        Cookie[] cookies = getRequest().getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                return cookie;
            }
        }
        return null;
    }

    public static void delCookie(String key) {
        Cookie cookie = getCookie(key);
        if (cookie != null) {
            cookie.setPath("/");
            cookie.setMaxAge(0);
            getResponse().addCookie(cookie);
        }
    }
}
