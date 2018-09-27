package cn.jants.core.startup;

import cn.jants.common.utils.StrUtil;
import cn.jants.core.context.AntsFilter;
import cn.jants.core.startup.assembly.FilterAssembly;
import cn.jants.core.startup.assembly.ServletAssembly;
import cn.jants.core.startup.servlet.IndexServlet;
import cn.jants.core.startup.servlet.LogoServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.ServletException;

/**
 * 内嵌Tomcat容器
 *
 * @author MrShun
 * @version 1.0
 */
public class JTomcat extends CommonProperty {


    public JTomcat(Class loadClass, int port, String contextPath) {
        super(loadClass, port, contextPath);
    }

    @Override
    public JTomcat start() {
        //创建内嵌tomcat容器
        Tomcat tomcat = new Tomcat();
        //设置端口
        tomcat.setPort(port);
        //用于存储自身的信息，可以随意指定，最好包含在项目目录下
        tomcat.setBaseDir(".");
        Connector connector = tomcat.getConnector();
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        //设置最大连接数
        protocol.setMaxConnections(maxConnections);
        //设置最大线程数
        protocol.setMaxThreads(maxThreads);
        //设置超时时间
        protocol.setConnectionTimeout(connectionTimeout);
        //不限制上传大小
        connector.setMaxPostSize(-1);
        //设置编码
        connector.setURIEncoding(charset);
        connector.setUseBodyEncodingForURI(true);
        //设置表单传参支持PUT
        connector.setParseBodyMethods("POST,PUT");
        //建立server参照tomcat文件结构
        StandardServer server = (StandardServer) tomcat.getServer();
        AprLifecycleListener listener = new AprLifecycleListener();
        server.addLifecycleListener(listener);

        // 将appBase设为本项目所在目录
        tomcat.getHost().setAppBase(".");
        try {
            Context ctx = tomcat.addWebapp(contextPath, webApp == null ? "." : webApp);
            //添加默认首页
            tomcat.addServlet(contextPath, "indexServlet", new IndexServlet());
            ctx.addServletMapping("", "indexServlet");
            //添加首页logo
            tomcat.addServlet(contextPath, "logoServlet", new LogoServlet());
            ctx.addServletMapping("/ants-logo", "logoServlet");

            //设置字体ContentType
            ctx.addMimeMapping("woff", "application/x-font-woff");
            ctx.addMimeMapping("woff2", "application/x-font-woff");
            ctx.addMimeMapping("ttf", "application/octet-stream");
            ctx.addMimeMapping("otf", "application/octet-stream");
            //允许上传随意大小的文件
            ctx.setAllowCasualMultipartParsing(true);
            if (StrUtil.notNull(servlets)) {
                for (ServletAssembly servlet : servlets) {
                    tomcat.addServlet(contextPath, servlet.getServletName(), servlet.getServlet());
                    ctx.addServletMapping(servlet.getUrlPattern(), servlet.getServletName());
                }
            }

            /**************************************************
             * 框架初始化核心Filter
             */
            FilterDef fd = new FilterDef();
            fd.setFilterName("AntsFilter");
            fd.setFilter(new AntsFilter());

            //添加启动模块
            fd.addInitParameter("loadClass", loadClass.getName());
            ctx.addFilterDef(fd);

            FilterMap fm = new FilterMap();
            fm.setFilterName("AntsFilter");
            fm.addURLPattern("/*");
            ctx.addFilterMap(fm);

            if (StrUtil.notNull(filters)) {
                for (FilterAssembly filter : filters) {
                    fd.setFilterName(filter.getFilterName());
                    fd.setFilter(filter.getFilter());
                    ctx.addFilterDef(fd);
                    fm.setFilterName(filter.getFilterName());
                    fm.addURLPattern(filter.getUrlPattern());
                    ctx.addFilterMap(fm);
                }
            }
            // 启动tomcat
            tomcat.start();
            // 维持
            tomcat.getServer().await();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static void run(Class loadClass) {
        new JTomcat(loadClass, 8080, "").start();
    }

    public static void run(Class loadClass, int port) {
        new JTomcat(loadClass, port, "").start();
    }

    public static void run(Class loadClass, String contextPath) {
        new JTomcat(loadClass, 8080, contextPath).start();
    }

    public static void run(Class loadClass, int port, String contextPath) {
        new JTomcat(loadClass, port, contextPath).start();
    }
}
