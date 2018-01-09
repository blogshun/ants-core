package com.ants.plugin.pay.ali;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.ants.common.bean.Prop;
import com.ants.common.utils.StrUtil;
import com.ants.core.holder.ClientHolder;
import com.ants.plugin.pay.ali.common.NotifyResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class AliPayTool {
    /**
     * RSA2, 请求网关地址
     */
    private String signType = "RSA2", url = "https://openapi.alipay.com/gateway.do";

    /**
     * 商户appid, 私钥, 通知地址, 网页支付同步跳转地址, 公钥
     */
    private String appId, privateKey, notifyUrl, returnUrl, publicKey;

    private AlipayClient client;

    /**
     * 为了防止反复初始化
     */
    private final static ConcurrentMap<String, AliPayTool> ALI_CLIENT_MAP = new ConcurrentHashMap<>();

    private AliPayTool(String appId, String privateKey, String notifyUrl, String returnUrl, String publicKey) {
        client = new DefaultAlipayClient(url, appId, privateKey, "json", "UTF-8", publicKey, signType);
        this.notifyUrl = notifyUrl;
        this.returnUrl = returnUrl;
    }

    /**
     * 初始化工具类
     *
     * @param appId      商户appid
     * @param privateKey 私钥
     * @param notifyUrl  通知地址
     * @param returnUrl  网页支付同步跳转地址
     * @param publicKey  公钥
     * @return
     */
    public static AliPayTool init(String appId, String privateKey, String notifyUrl, String returnUrl, String publicKey) {
        appId = Prop.getKeyStrValue(appId);
        privateKey = Prop.getKeyStrValue(privateKey);
        notifyUrl = Prop.getKeyStrValue(notifyUrl);
        if (StrUtil.notBlank(returnUrl)) {
            returnUrl = Prop.getKeyStrValue(returnUrl);
        }
        publicKey = Prop.getKeyStrValue(publicKey);
        if (ALI_CLIENT_MAP.containsKey(appId)) {
            return ALI_CLIENT_MAP.get(appId);
        }
        AliPayTool aliPayTool = new AliPayTool(appId, privateKey, notifyUrl, returnUrl, publicKey);
        ALI_CLIENT_MAP.put(appId, aliPayTool);
        return aliPayTool;
    }

    public static AliPayTool init(String appId, String privateKey, String notifyUrl, String publicKey) {
        return init(appId, privateKey, notifyUrl, null, publicKey);
    }


    /**
     * 获取支付通知
     *
     * @return
     */
    public NotifyResult getNotify() {
        HttpServletRequest request = ClientHolder.getRequest();
        String code = request.getParameter("code");
        NotifyResult notifyResult = null;
        if ("10000".equals(code)) {
            notifyResult = new NotifyResult(request);
        } else {
            notifyResult = new NotifyResult(code, request.getParameter("msg"));
        }
        return notifyResult;
    }
}
