package cn.jants.restful.render;

import cn.jants.plugin.template.VelocityTpl;
import cn.jants.common.annotation.boot.ViewConfiguration;
import cn.jants.common.enums.ViewType;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.context.AppConstant;
import cn.jants.core.module.ServiceManager;
import cn.jants.plugin.template.BeetleTpl;
import cn.jants.plugin.template.FreeMarkerTpl;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author MrShun
 * @version 1.0
 */
public class View {

    /**
     * 渲染输出模板
     *
     * @param viewName 视图名称
     * @param request
     * @param response
     */
    private static void render(Object viewName, ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ViewConfiguration tplConfig = AppConstant.TPL_CONFIG;
        if (tplConfig == null) {
            throw new RuntimeException("错误, 没有配置任何模板引擎!");
        }
        String view = StrUtil.setFirstInitial(String.valueOf(viewName), '/');
        response.setContentType("text/html");
        if (tplConfig.viewType() == ViewType.JSP) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(view);
            requestDispatcher.forward(request, response);
        }else if(tplConfig.viewType() == ViewType.FREEMARKER){
            FreeMarkerTpl  freeMarkerTpl = (FreeMarkerTpl) ServiceManager.getService("plugin_template_FreeMarkerTpl");
            if(viewName != null) {
                freeMarkerTpl.render(String.valueOf(viewName), request, response);
            }else if(modelAndView != null){
                freeMarkerTpl.render(modelAndView, request, response);
            }
        }else if(tplConfig.viewType() == ViewType.BEETL){
            BeetleTpl beetlTpl = (BeetleTpl) ServiceManager.getService("plugin_template_BeetleTpl");
            if(viewName != null) {
                beetlTpl.render(String.valueOf(viewName), request, response);
            }else if(modelAndView != null){
                beetlTpl.render(modelAndView, request, response);
            }
        }else if(tplConfig.viewType() == ViewType.VELOCITY){
            VelocityTpl velocityTpl = (VelocityTpl)ServiceManager.getService("plugin_template_VelocityTpl");
            if(viewName != null) {
                velocityTpl.render(String.valueOf(viewName), request, response);
            }else if(modelAndView != null){
                velocityTpl.render(modelAndView, request, response);
            }
        }
    }

    public static void render(Object viewName, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        render(viewName, null, request, response);
    }

    public static void render(ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        render(null, modelAndView, request, response);
    }
}
