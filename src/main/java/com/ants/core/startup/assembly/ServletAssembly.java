package com.ants.core.startup.assembly;

import javax.servlet.http.HttpServlet;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-11-16
 */
public class ServletAssembly {

    /**
     * Servlet名称
     */
    private String servletName;

    /**
     * Servlet对象
     */
    private HttpServlet servlet;

    /**
     * 匹配路径
     */
    private String urlPattern;

    public ServletAssembly() {
    }

    public ServletAssembly(String servletName, HttpServlet servlet, String urlPattern) {
        this.servletName = servletName;
        this.servlet = servlet;
        this.urlPattern = urlPattern;
    }

    public String getServletName() {
        return servletName;
    }

    public HttpServlet getServlet() {
        return servlet;
    }

    public String getUrlPattern() {
        return urlPattern;
    }
}
