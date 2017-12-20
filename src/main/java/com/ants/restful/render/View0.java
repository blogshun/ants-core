//package com.ants.restful.render;
//
//import com.ants.common.bean.JsonMap;
//import com.ants.common.enums.ViewType;
//import com.ants.core.module.Constant;
//import freemarker.template.Template;
//import org.beetl.core.Configuration;
//import org.beetl.ext.servlet.ServletGroupTemplate;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.RequestDispatcher;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Enumeration;
//
//
///**
// * @author MrShun
// * @version 1.0
// * @Date 2017-05-23
// */
//public class View0 {
//
//    private static final Logger logger = LoggerFactory.getLogger(View0.class);
//
//    protected static ServletGroupTemplate beetlTemplate = null;
//
//    protected static freemarker.template.Configuration config = null;
//
//    public static void render(Constant constant, String viewName, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        ViewType viewType = constant.getViewType();
//        if (viewType == ViewType.JSP) {
//            RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewName);
//            requestDispatcher.forward(request, response);
//        } else if (viewType == ViewType.FREEMARKER) {
//            if (config == null) {
//                synchronized (freemarker.template.Configuration.class) {
//                    if (config == null) {
//                        config = new freemarker.template.Configuration();
//                        config.setClassForTemplateLoading(Thread.currentThread().getClass(), "/tpl/");
//                     //   config.setServletContextForTemplateLoading(request.getServletContext(), viewType.getTemplePath());
//                        config.setDefaultEncoding(constant.getEncoding());
//                    }
//
//                }
//            }
//            Template template = config.getTemplate(viewName);
//            Enumeration<String> abts = request.getAttributeNames();
//            JsonMap model = JsonMap.newJsonMap();
//            while (abts.hasMoreElements()) {
//                String abt = abts.nextElement();
//                model.put(abt, request.getAttribute(abt));
//            }
//            template.process(model, response.getWriter());
//        } else if (viewType == ViewType.BEETL) {
//            if (beetlTemplate == null) {
//                synchronized (ServletGroupTemplate.class) {
//                    if (beetlTemplate == null) {
//                        beetlTemplate = ServletGroupTemplate.instance();
//                    }
//                }
//            }
//            Object config = constant.getViewType().getConfig();
//            if (config != null) {
//                beetlTemplate.getGroupTemplate().setConf((Configuration) config);
//            }
//            beetlTemplate.render(viewName, request, response);
//        } else {
//            throw new RuntimeException("没有指定任何视图模板!");
//        }
//
//        logger.debug("return view is {} !", viewType);
//    }
//}
