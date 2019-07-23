package cn.jants.core.context;

import cn.jants.common.bean.Log;
import cn.jants.common.exception.TipException;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.module.Constant;
import cn.jants.core.module.HandlerManager;
import cn.jants.core.module.RequestMappingManager;
import cn.jants.restful.request.MappingMatch;
import jdk.net.SocketFlow;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 框架进入点根据过滤器
 * 初始化插件
 * 初始化常量
 * 初始化路由
 * 初始化视图
 *
 * @author MrShun
 * @version 1.0
 */
public class AntsFilter implements Filter {

    /**
     * 常量配置
     */
    private Constant constant;

    /**
     * Handler管理链
     */
    private HandlerManager handlerManager;

    /**
     * 项目字符串长度
     */
    private int contextPathLength;

    private AntsContext context;

    /**
     * Filter【Ants框架】初始化
     *
     * @param filterConfig
     * @throws ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String configClass = filterConfig.getInitParameter("loadClass");
        if (configClass == null) {
            throw new RuntimeException("请在 web.xml 文件中配置启动类!");
        }
        try {
            Class temp = Class.forName(configClass);
            ServletContext servletContext = filterConfig.getServletContext();
            //初始化
            context = new AntsContext(temp, servletContext);
            handlerManager = context.getHandlerManager();
            constant = context.getConstant();
            String contextPath = servletContext.getContextPath();
            contextPathLength = (contextPath == null || "/".equals(contextPath)) ? 0 : contextPath.length();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("加载".concat(configClass) + " 失败!");
        }
    }

    /**
     * 执行过滤形成handler链动态调用方法
     *
     * @param req
     * @param res
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // 获取 request、response，设置编码
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        request.setCharacterEncoding(constant.getEncoding());
        response.setCharacterEncoding(constant.getEncoding());
        String target = request.getRequestURI();
        if (contextPathLength > 0) {
            target = target.substring(contextPathLength);
        }
        String[] resources = constant.getResources();
        //定义一个标志位
        boolean[] isHandled = {false};
        //判断是否有静态资源
        if (StrUtil.notBlank(resources)) {
            for (String resource : resources) {
                //存在静态资源就拦截 修改标志位
                if (target.startsWith(resource)) {
                    isHandled[0] = true;
                    break;
                }
            }
        }

        if (isHandled[0] || !MappingMatch.matchRequests(RequestMappingManager.getRequests(), target)) {
            chain.doFilter(request, response);
        } else {
            //设置允许域
            if (StrUtil.notBlank(AppConstant.DOMAIN)) {
                response.setHeader("Access-Control-Allow-Origin", AppConstant.DOMAIN);
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            }
            //交给handler处理
            boolean execute = handlerManager.execute(target, request, response);
            //当标志为true不执行下个过滤器
            if (execute) {
                chain.doFilter(request, response);
            }
        }
    }

    /**
     * Filter【Ants框架】销毁
     */
    @Override
    public void destroy() {
        Log.debug("销毁所有插件...");
        context.stopPlugins();
    }
}
