package cn.jants.core.ext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author MrShun
 * @version 1.0
 */
public interface Handler {

    /**
     * 对handler链进行拦截处理
     *
     * @param target
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    boolean preHandler(String target, HttpServletRequest request, HttpServletResponse response)
            throws Exception;
}
