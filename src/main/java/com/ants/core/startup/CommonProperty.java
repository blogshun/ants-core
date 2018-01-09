package com.ants.core.startup;

import com.ants.core.startup.assembly.FilterAssembly;
import com.ants.core.startup.assembly.ServletAssembly;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-12-03
 */
public class CommonProperty {

    /**
     * 端口
     */
    public int port = 8080;

    /**
     * ContextPath
     */
    public String contextPath;

    /**
     * 运行目录
     */
    public String webApp;

    /**
     * 最大链接数
     */
    public Integer maxConnections = 2000;

    /**
     * 最大线程数
     */
    public Integer maxThreads = 2000;

    /**
     * 链接超时时间
     */
    public Integer connectionTimeout = 30000;

    /**
     * 设置编码
     */
    public String charset = "UTF-8";

    /**
     * 添加的Servlet组件
     */
    public List<ServletAssembly> servlets;

    /**
     * 添加的Filter
     */
    public List<FilterAssembly> filters;

    /**
     * 加载类
     */
    public Class loadClass;

    /**
     * 是否打开默认浏览器
     */
    public boolean isOpen = false;

    public CommonProperty(String webApp, int port, String contextPath, Class loadClass, boolean isOpen) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port of web server: " + port);
        }
        this.webApp = webApp;
        this.port = port;
        this.contextPath = contextPath;
        this.loadClass = loadClass;
        this.isOpen = isOpen;
    }

    public CommonProperty setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public CommonProperty setPort(int port) {
        this.port = port;
        return this;
    }

    public CommonProperty setContextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    public CommonProperty setWebApp(String webApp) {
        this.webApp = webApp;
        return this;
    }

    public CommonProperty setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    public CommonProperty setMaxThreads(Integer maxThreads) {
        this.maxThreads = maxThreads;
        return this;
    }

    public CommonProperty setCharset(String charset) {
        this.charset = charset;
        return this;
    }


    public CommonProperty addServlets(List<ServletAssembly> servlets) {
        this.servlets = servlets;
        return this;
    }

    public CommonProperty addFilters(List<FilterAssembly> filters) {
        this.filters = filters;
        return this;
    }

    public Class getLoadClass() {
        return loadClass;
    }

    public void openBrowser() {
        //启用系统默认浏览器来打开网址。
        try {
            String urlStr = "http://localhost";
            if (port != 80) {
                urlStr = urlStr.concat(":" + port);
            }
            URI uri = new URI(urlStr);
            Desktop.getDesktop().browse(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CommonProperty start() {
        return this;
    }
}
