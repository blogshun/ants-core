package cn.jants.core.handler;

import cn.jants.core.ext.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 */
public class PathHandler implements Handler {

    private Map<String, String> pathMap = null;

    public PathHandler(Map<String, String> pathMap) {
        this.pathMap = pathMap;
    }

    @Override
    public boolean preHandler(String target, HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (Map.Entry<String, String> entry : pathMap.entrySet()) {
            request.setAttribute(entry.getKey(), request.getContextPath() + entry.getValue());
        }
        return true;
    }
}
