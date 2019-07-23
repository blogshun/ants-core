package cn.jants.core.handler;

import cn.jants.common.annotation.action.NoUserToken;
import cn.jants.common.exception.TipException;
import cn.jants.common.utils.StrUtil;
import cn.jants.common.utils.TokenUtil;
import cn.jants.core.ext.Handler;
import cn.jants.core.module.RequestMappingManager;
import cn.jants.restful.bind.utils.AnnotationUtils;
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

    private final List<RequestMappingBean> requestMappingManager = RequestMappingManager.getRequestMappingManager();

    /**
     * 全局请求Token名称
     */
    private String tokenRequestName;

    /**
     * redis token文件夹名称
     */
    private String tokenPrefixName;

    /**
     * 设置token有效期秒, 默认30分钟
     */
    private int seconds = 60 * 30;

    private UserTokenHandler() {
    }

    public UserTokenHandler(String tokenRequestName, String tokenPrefixName) {
        this.tokenRequestName = tokenRequestName;
        this.tokenPrefixName = tokenPrefixName;
    }


    public UserTokenHandler(String tokenRequestName, String tokenPrefixName, int seconds) {
        this.tokenRequestName = tokenRequestName;
        this.tokenPrefixName = tokenPrefixName;
        this.seconds = seconds;
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
        String userTokenStr = request.getHeader(tokenRequestName);
        if (StrUtil.isBlank(userTokenStr)) {
            throw new TipException(1002, String.format("缺少 %s 参数!", tokenRequestName));
        }
        TokenUtil.checkValidity(userTokenStr, tokenPrefixName, seconds);
    }
}
