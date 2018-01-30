package cn.jants.plugin.template;

import cn.jants.common.bean.JsonMap;
import cn.jants.restful.render.ModelAndView;
import cn.jants.common.annotation.boot.ViewConfiguration;
import cn.jants.common.enums.LoadType;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.FileResourceLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 */
public class BeetleTpl {

    public Configuration configuration;

    private GroupTemplate groupTemplate;

    private String tplDir;

    public BeetleTpl(ViewConfiguration viewConfiguration){
        this.tplDir = viewConfiguration.loadPath();
        Configuration configuration;
        try {
            configuration = Configuration.defaultConfiguration();
            if(viewConfiguration.loadType() == LoadType.FilePath){
                groupTemplate = new GroupTemplate(new FileResourceLoader(viewConfiguration.loadPath()), configuration);
            }else {
                groupTemplate = new GroupTemplate(new ClasspathResourceLoader(tplDir), configuration);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTplDir(String tplDir) {
        groupTemplate.setResourceLoader(new ClasspathResourceLoader(tplDir));
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public GroupTemplate getGroupTemplate() {
        return groupTemplate;
    }

    private void render(String viewName, ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) {
        Template template = null;
        Enumeration<String> attributeNames = request.getAttributeNames();
        Map model = JsonMap.newJsonMap();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            model.put(attributeName, request.getAttribute(attributeName));
        }
        if(viewName != null){
            template = groupTemplate.getTemplate(viewName);
        }else if (modelAndView != null) {
            template = groupTemplate.getTemplate(modelAndView.getView());
            model.putAll(modelAndView.getModel());
        }
        try {
            template.binding(model);
            template.renderTo(response.getWriter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render(String viewName, HttpServletRequest request, HttpServletResponse response){
        render(viewName, null, request, response);
    }

    public void render(ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response){
        render(null, modelAndView, request, response);
    }
}
