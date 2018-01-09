package com.ants.plugin.pay.wx;

import com.ants.common.bean.Log;
import com.ants.common.bean.Prop;
import com.ants.common.utils.HttpUtil;
import com.ants.common.utils.IOUtil;
import com.ants.common.utils.MapXmlUtil;
import com.ants.common.utils.StrUtil;
import com.ants.core.holder.ClientHolder;
import com.ants.plugin.pay.wx.common.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class WxPayTool {

    /**
     * 应用 appId, 应用 appSecret, 商户ID, 支付密钥, 通知地址
     */
    private String appId, appSecret, mchId, payKey, notifyUrl;

    /**
     * 为了防止反复初始化
     */
    private final static ConcurrentMap<String, WxPayTool> PAY_MAP = new ConcurrentHashMap<>();

    private WxPayTool(String appId, String appSecret, String mchId, String payKey, String notifyUrl) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.mchId = mchId;
        this.payKey = payKey;
        this.notifyUrl = notifyUrl;
    }

    /**
     * 初始化工具类
     *
     * @param appId
     * @param appSecret
     * @return
     */
    public static WxPayTool init(String appId, String appSecret, String mchId, String payKey, String notifyUrl) {
        appId = Prop.getKeyStrValue(appId);
        appSecret = Prop.getKeyStrValue(appSecret);
        String key = appId.concat("_").concat(appSecret);
        if (PAY_MAP.containsKey(key)) {
            return PAY_MAP.get(key);
        }
        WxPayTool wxPayTool = new WxPayTool(appId, appSecret, mchId, payKey, notifyUrl);
        PAY_MAP.put(key, wxPayTool);
        return wxPayTool;
    }

    /**
     * 获取App支付签名
     * 类型：PayType.APP
     *
     * @param params 支付参数
     */
    public PayApiResult getAppPaySign(PayParams params) {
        PayApiResult unifiedOrderParams = unifiedOrderParams(params);
        String prepayId = unifiedOrderParams.getStr("prepay_id");
        String timeStamp = Long.toString(System.currentTimeMillis() / 1000);
        //获取订单里面的prepay_id, 再次签名
        TreeMap<String, Object> result = new TreeMap<String, Object>();
        result.put("appid", appId);
        result.put("partnerid", mchId);
        result.put("package", "Sign=WXPay");
        result.put("noncestr", StrUtil.randomUUID());
        result.put("prepayid", prepayId);
        result.put("timestamp", timeStamp);
        result.put("sign", Sign.md5Sign(result, payKey));

        PayApiResult payApiResult = new PayApiResult(result);
        payApiResult.set("prepay_id", prepayId);
        return payApiResult;
    }

    /**
     * 获取JsApi支付签名
     * 类型：PayType.JSAPI
     *
     * @param params 支付参数
     * @return
     */
    public PayApiResult getJsApiPaySign(PayParams params) {
        PayApiResult unifiedOrderParams = unifiedOrderParams(params);
        String prepayId = unifiedOrderParams.getStr("prepay_id");
        String timeStamp = Long.toString(System.currentTimeMillis() / 1000);
        //获取订单里面的prepay_id, 再次签名
        TreeMap<String, Object> result = new TreeMap<String, Object>();
        result.put("appId", appId);
        result.put("nonceStr", StrUtil.randomUUID());
        result.put("package", "prepay_id=" + prepayId);
        result.put("signType", "MD5");
        result.put("timeStamp", timeStamp);
        result.put("paySign", Sign.md5Sign(result, payKey));
        result.put("prepayId", prepayId);

        PayApiResult payApiResult = new PayApiResult(result);
        payApiResult.set("prepay_id", prepayId);
        return payApiResult;
    }

    /**
     * 扫描支付, 主要是先生存订单, 在扫码完成支付
     * 类型：PayType.NATIVE
     *
     * @param params 支付参数
     * @return
     */
    public PayApiResult getScanCodePaySign(PayParams params) {
        return unifiedOrderParams(params);
    }

    /**
     * 扫描支付, 输入金额支付到商户
     *
     * @param productId 商户定义的商品id 或者订单号
     * @return
     */
    public String createPayCodeUrl(String productId) {
        TreeMap params = new TreeMap();
        params.put("product_id", productId);
        params.put("appid", appId);
        params.put("mch_id", mchId);
        params.put("time_stamp", Long.toString(System.currentTimeMillis() / 1000));
        params.put("nonce_str", StrUtil.randomUUID());
        params.put("sign", Sign.md5Sign(params, payKey));
        return "weixin://wxpay/bizpayurl?" + Sign.pj(params);
    }

    /**
     * 根据流读取返回通知内容
     *
     * @return
     */
    public NotifyResult getNotify() {
        try {
            HttpServletRequest request = ClientHolder.getRequest();
            String xmlStr = IOUtil.parseStr(request.getInputStream());
            return new NotifyResult(MapXmlUtil.xml2Map(xmlStr, "xml"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 统一订单生成
     */
    private PayApiResult unifiedOrderParams(PayParams params) {
        params.setAppId(appId)
                .setMchId(mchId)
                .setNonceStr(StrUtil.randomUUID())
                .setNotifyUrl(notifyUrl);

        String orderXml = unifiedOrderXml(params);
        Log.debug("统一订单XML > {}", orderXml);
        //以上是生成订单数据map
        String responseXml = HttpUtil.sendPost(WxPayApiConstant.UNIFIED_ORDER_API, orderXml);
        Map unifiedOrderParams = MapXmlUtil.xml2Map(String.valueOf(responseXml), "xml");
        return new PayApiResult(unifiedOrderParams);
    }

    /**
     * 根据openid统一订单生成xml数据
     *
     * @param params 统一订单参数
     * @return
     */
    private String unifiedOrderXml(PayParams params) {
        String md5Sign = Sign.md5Sign(params, payKey);
        params.put("sign", md5Sign);
        return MapXmlUtil.map2Xml(params, "xml");
    }
}
