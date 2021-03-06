package cn.jants.plugin.template;

import cn.jants.common.annotation.boot.ViewConfiguration;
import cn.jants.common.bean.JsonMap;
import cn.jants.common.enums.LoadType;
import cn.jants.restful.render.ModelAndView;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 */
public class FreeMarkerTpl {

    private Configuration configuration;

    private String tplDir;

    private LoadType loadType;

    public FreeMarkerTpl(ViewConfiguration viewConfiguration) {
        this.tplDir = viewConfiguration.loadPath();
        this.loadType = viewConfiguration.loadType();
        configuration = new Configuration();
        configuration.setTemplateUpdateDelay(viewConfiguration.updateDelay());
        configuration.setDefaultEncoding(viewConfiguration.encoding());
        //解决屏蔽掉FreeMaker为空报错 <#setting classic_compatible=false> 可以激活
        configuration.setClassicCompatible(true);
        configuration.setOutputEncoding(viewConfiguration.encoding());
        if (loadType == LoadType.FilePath) {
            try {
                configuration.setDirectoryForTemplateLoading(new File(tplDir));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(tplDir + " 没有找到模板目录!");
            }
        } else {
            configuration.setClassForTemplateLoading(Thread.currentThread().getClass(), tplDir);
        }
    }

    public void setTplDir(String tplDir) {
        if (loadType == LoadType.FilePath) {
            try {
                configuration.setDirectoryForTemplateLoading(new File(tplDir));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(tplDir + " 没有找到模板目录!");
            }
        } else {
            configuration.setClassForTemplateLoading(Thread.currentThread().getClass(), tplDir);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    private void render(String viewName, ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) {
        Enumeration<String> attributeNames = request.getAttributeNames();
        Map model = JsonMap.newJsonMap();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            model.put(attributeName, request.getAttribute(attributeName));
        }
        try {
            Template template = viewName != null ? configuration.getTemplate(viewName) : configuration.getTemplate(modelAndView.getView());
            if (modelAndView != null) {
                model.putAll(modelAndView.getModel());
            }
            template.process(model, response.getWriter());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() + " 没有指定任何视图模板!");
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public void render(String viewName, HttpServletRequest request, HttpServletResponse response) {
        render(viewName, null, request, response);
    }

    public void render(ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) {
        render(null, modelAndView, request, response);
    }
}
