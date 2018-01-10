package cn.jants.plugin.template;

import cn.jants.common.annotation.boot.ViewConfiguration;
import cn.jants.common.enums.LoadType;
import cn.jants.common.utils.StrUtil;
import cn.jants.restful.render.ModelAndView;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * @author MrShun
 * @version 1.0
 */
public class VelocityTpl {

    private VelocityEngine velocityEngine;

    /**
     * 模板目录
     */
    private String tplDir = "";

    private String suffix = "";

    public VelocityTpl(ViewConfiguration viewConfiguration){
        velocityEngine = new VelocityEngine();
        //设置velocity资源加载方式为classpath、jar、class、file
        if(viewConfiguration.loadType() == LoadType.FilePath){
            velocityEngine.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, viewConfiguration.loadPath());
        }else {
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            velocityEngine.setProperty("classpath.resource.loader.cache", true);
            velocityEngine.setProperty("classpath.resource.loader.modification.check.interval", viewConfiguration.updateDelay());
            this.tplDir = viewConfiguration.loadPath();
        }
        velocityEngine.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        velocityEngine.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        velocityEngine.init();
        this.suffix = viewConfiguration.suffix();
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public void setTplDir(String tplDir) {
        this.tplDir = tplDir;
    }

    public void clearCache(){

    }

    private void render(String viewName, ModelAndView modelAndView, HttpServletRequest request, HttpServletResponse response) {
        Template template = null;
        VelocityContext ctx = new VelocityContext();
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            ctx.put(attributeName, request.getAttribute(attributeName));
        }
        if(viewName != null){
            template = velocityEngine.getTemplate(tplDir.concat(StrUtil.setFirstInitial(viewName, '/')).concat(suffix));
        }else if (modelAndView != null) {
            template = velocityEngine.getTemplate(tplDir.concat(StrUtil.setFirstInitial(modelAndView.getView(), '/')).concat(suffix));
            Map model = modelAndView.getModel();
            if(model != null){
                Set<Map.Entry<String, Object>> sets = model.entrySet();
                for(Map.Entry<String, Object> entry: sets){
                    ctx.put(entry.getKey(), entry.getValue());
                }
            }
        }
        try {
            template.merge(ctx, response.getWriter());
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
