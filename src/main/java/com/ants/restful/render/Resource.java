package com.ants.restful.render;

import com.ants.core.context.AppConstant;
import com.ants.core.utils.FileUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-08-29
 */
public class Resource {

    /**
     * 资源路径
     */
    private String resName;

    /**
     * 填充的数据
     */
    private Object data;

    public Resource(String resName) {
        this.resName = resName;
    }

    public Resource(String resName, Object data) {
        this.resName = resName;
        this.data = data;
    }


    public void render(HttpServletRequest request, HttpServletResponse response) {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resName);
        write(request, response, in);
    }

    public void write(HttpServletRequest request, HttpServletResponse response, InputStream in) {
        try {
            String contentType = request.getServletContext().getMimeType(resName);
            //设置文件MIME类型
            response.setContentType(contentType);
            if (contentType.startsWith("image/")) {
                int i = in.available();
                //byte数组用于存放图片字节数据
                byte[] buff = new byte[i];
                in.read(buff);
                //记得关闭输入流
                in.close();
                OutputStream out = response.getOutputStream();
                out.write(buff);
                //关闭响应输出流
                out.close();
                return;
            } else {
                String content = FileUtil.read(in, AppConstant.DEFAULT_ENCODING);
                if(contentType.startsWith("text/html")) {
                    Map<String, Object> dataMap = new HashMap<>(10);
                    dataMap.put("ctx", request.getContextPath());
                    dataMap.put("JS_PATH", "/static/js");
                    dataMap.put("IMG_PATH", "/static/images");
                    dataMap.put("CSS_PATH", "/static/css");
                    dataMap.put("SWF_PATH", "/static/swf");
                    dataMap.put("UE_PATH", "/static/ueditor");
                    if (dataMap != null) {
                        content = replace(content, dataMap);
                    }
                }
                response.getWriter().write(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据键值对填充字符串
     *
     * @param content
     * @param map
     * @return
     */
    private String replace(String content, Map<String, Object> map) {
        if (map == null || content == null) {
            return content;
        }
        Set<Map.Entry<String, Object>> sets = map.entrySet();
        for (Map.Entry<String, Object> entry : sets) {
            String key = entry.getKey();
            String regex = "${" + key + "}";
            content = content.replace(regex, String.valueOf(entry.getValue()));
        }
        return content;
    }
}
