package cn.jants.common.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author MrShun
 * @version 1.0
 */
public class RequestUtil {

    private final static String[] AGENTS = { "Android", "iPhone", "iPod","iPad", "Windows Phone", "MQQBrowser" };

    /**
     * 判断ajax请求
     * @param request
     * @return
     */
    public static boolean isAjax(HttpServletRequest request){
        if(request.getHeader("X-Requested-With") != null  && "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return true;
        }
        return false;
    }

    public static boolean isApp(HttpServletRequest request){
        String userAgent = request.getHeader("user-agent");
        for(String agent: AGENTS){
            if(agent.equalsIgnoreCase(userAgent)) {
                return true;
            }
        }
        return false;
    }
}
