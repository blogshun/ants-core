package cn.jants.core.handler;

import cn.jants.common.annotation.action.NoUserToken;
import cn.jants.common.exception.TipException;
import cn.jants.common.utils.StrUtil;
import cn.jants.common.utils.TokenUtil;
import cn.jants.core.ext.Handler;
import cn.jants.core.module.RequestMappingManager;
import cn.jants.core.utils.AnnotationUtils;
import cn.jants.restful.request.MappingMatch;
import cn.jants.restful.request.RequestMappingBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

/**
 * token拦截链
 *
 * @author MrShun
 * @version 1.0
 */
public class UserTokenHandler implements Handler {

    private String userTokenName = "Api-User-Token";

    private final List<RequestMappingBean> requestMappingManager = RequestMappingManager.getRequestMappingManager();

    public UserTokenHandler() {
    }


    @Override
    public boolean preHandler(String target, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestMappingBean bean = MappingMatch.match(requestMappingManager, target);
        if (bean == null) {
            return true;
        } else {
            Method method = bean.getMethod();
            NoUserToken noUserToken = AnnotationUtils.findAnnotation(method, NoUserToken.class);
            if (noUserToken == null) {
                checkUserToken(request);
            }
            return true;
        }
    }

    /**
     * 校验用户Token值
     *
     * @param request
     */
    private void checkUserToken(HttpServletRequest request) {
        String userTokenStr = request.getHeader(userTokenName);
        if (StrUtil.isBlank(userTokenStr)) {
            throw new TipException(String.format("缺少 %s 参数!", userTokenName));
        }
        TokenUtil.checkValidity(userTokenStr);
    }
}
