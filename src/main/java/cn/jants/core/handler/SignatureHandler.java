package cn.jants.core.handler;

import cn.jants.common.annotation.action.NoCheckSignature;
import cn.jants.common.bean.Log;
import cn.jants.common.enums.ResponseCode;
import cn.jants.common.exception.TipException;
import cn.jants.common.utils.StrEncryptUtil;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.ext.Handler;
import cn.jants.core.module.RequestMappingManager;
import cn.jants.core.utils.AnnotationUtils;
import cn.jants.restful.request.MappingMatch;
import cn.jants.restful.request.RequestMappingBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 签名拦截链
 *
 * @author MrShun
 * @version 1.0
 */
public class SignatureHandler implements Handler {


    /**
     * 默认密钥// 签名名称 //时间戳
     */
    private String secretKey = "&2018", signName = "sign", effTimeName = "timeStamp";

    /**
     * 时间戳,限制请求有效时间
     */
    private Long effTime = null;

    private final List<RequestMappingBean> requestMappingManager = RequestMappingManager.getRequestMappingManager();

    public SignatureHandler() {
    }

    public SignatureHandler(String secretKey) {
        this.secretKey = secretKey;
    }

    public SignatureHandler(Long effTime) {
        this.effTime = effTime;
    }

    public SignatureHandler(String secretKey, Long effTime) {
        this.secretKey = secretKey;
        this.effTime = effTime;
    }


    @Override
    public boolean preHandler(String target, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestMappingBean bean = MappingMatch.match(requestMappingManager, target);
        if (bean == null) {
            return true;
        } else {
            Method method = bean.getMethod();
            NoCheckSignature noCheckSignature = AnnotationUtils.findAnnotation(method, NoCheckSignature.class);
            if (noCheckSignature == null) {
                checkSignature(request);
            }
            return true;
        }
    }

    /**
     * 为了系统安全, 做签名校验
     *
     * @param request
     */
    private void checkSignature(HttpServletRequest request) {
        String reqSignStr = request.getParameter(signName);

        Log.debug("请求签名 request sign - > {}", reqSignStr);

        //签名错误
        if (StrUtil.isBlank(reqSignStr)) {
            throw new TipException(1003, "缺少签名参数!");
        }

        Enumeration<String> parameterNames = request.getParameterNames();
        Map<String, String> treeMap = new TreeMap();
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            if (!key.equals(signName)) {
                treeMap.put(key, request.getParameter(key));
            }
        }

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key.concat("=").concat(value).concat("&"));
        }
        sb.append(secretKey);
        String serSignStr = StrEncryptUtil.md5(sb.toString());
        Log.debug("服务签名 server sign - > {}", serSignStr);
        if (!serSignStr.equalsIgnoreCase(reqSignStr)) {
            throw new TipException(ResponseCode.SING_ERROR);
        }
        String timeStamp = request.getParameter(effTimeName);
        if (timeStamp != null) {
            long l = System.currentTimeMillis() - Long.valueOf(timeStamp);
            if(l > effTime){
                throw new TipException(ResponseCode.REQUEST_INVALID_ERROR);
            }
        }
    }
}
