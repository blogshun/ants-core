package com.ants.core.startup;

import com.ants.common.bean.Log;
import com.ants.common.utils.StrUtil;
import com.ants.core.context.AntsFilter;
import com.ants.core.startup.assembly.FilterAssembly;
import com.ants.core.startup.assembly.ServletAssembly;
import com.ants.core.startup.servlet.IndexServlet;
import com.ants.core.startup.servlet.LogoServlet;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * 内嵌Jetty容器
 *
 * @author MrShun
 * @version 1.0
 * @date 2017-11-16
 */
public class Jetty extends CommonProperty {


    public Jetty(String webApp, int port, String contextPath, Class loadClass, boolean isOpen) {
        super(webApp, port, contextPath, loadClass, isOpen);
    }

    /**
     * Jetty启动
     */
    @Override
    public Jetty start() {

        Server server = new Server();
        Connector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.addConnector(connector);
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath(contextPath);
        webAppContext.setBaseResource(Resource.newClassPathResource(""));
        webAppContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        // 在启动过程中允许抛出异常终止启动并退出 JVM
        webAppContext.setThrowUnavailableOnStartupException(true);
        webAppContext.setConfigurationDiscovered(true);
        webAppContext.setParentLoaderPriority(true);
        server.setHandler(webAppContext);

        //添加默认首页
        webAppContext.addServlet(new ServletHolder(new IndexServlet()), "");
        //添加首页logo
        webAppContext.addServlet(new ServletHolder(new LogoServlet()), "/ants-logo");
        if (StrUtil.notNull(servlets)) {
            for (ServletAssembly servlet : servlets) {
                webAppContext.addServlet(new ServletHolder(servlet.getServlet()), servlet.getUrlPattern());
            }
        }

        //设置加入Ants过滤器
        FilterHolder filterHolder = new FilterHolder(new AntsFilter());
        filterHolder.setInitParameter("loadClass", loadClass.getName());
        webAppContext.addFilter(filterHolder, "/*", EnumSet.allOf(DispatcherType.class));

        //设置字体ContentType
        MimeTypes mimeTypes = new MimeTypes();
        mimeTypes.addMimeMapping("woff", "application/x-font-woff");
        mimeTypes.addMimeMapping("woff2", "application/x-font-woff");
        mimeTypes.addMimeMapping("ttf", "application/octet-stream");
        mimeTypes.addMimeMapping("otf", "application/octet-stream");
        webAppContext.setMimeTypes(mimeTypes);

        if (StrUtil.notNull(filters)) {
            for (FilterAssembly filter : filters) {
                webAppContext.addFilter(new FilterHolder(filter.getFilter()), filter.getFilterName(), EnumSet.allOf(DispatcherType.class));
            }
        }
        try {
            Log.debug("jetty is success!");
            server.start();
            if (isOpen) {
                openBrowser();
            }
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public static Jetty run(Class loadClass) {
        return new Jetty(null, 8080, "", loadClass, false).start();
    }

    public static Jetty run(Class loadClass, int port){
        return new Jetty(null, port, "", loadClass, false).start();
    }

    public static Jetty run(Class loadClass, int port, boolean isOpen) {
        return new Jetty(null, port, "", loadClass, isOpen).start();
    }

    public static Jetty run(Class loadClass, String[] args) {
        int port = 8080;
        String contextPath = "/";
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
            contextPath = args[1] != null ? args[1] : "";
        }
        return new Jetty(null, port, contextPath, loadClass, false).start();
    }

    public static Jetty run(Class loadClass, String contextPath) {
        return new Jetty(null, 8080, contextPath, loadClass, false).start();
    }

    public static Jetty run(Class loadClass, int port, String contextPath) {
        return new Jetty(null, port, contextPath, loadClass, false).start();
    }

    public static Jetty run(Class loadClass, String webApp, int port, String contextPath) {
        return new Jetty(webApp, port, contextPath, loadClass, false).start();
    }
}
