package cn.jants.plugin.tool;

import cn.jants.common.bean.Log;
import cn.jants.common.exception.TipException;
import cn.jants.common.utils.HttpUtil;
import cn.jants.common.utils.IOUtil;
import cn.jants.common.utils.MapXmlUtil;
import cn.jants.common.utils.StrUtil;
import cn.jants.core.holder.ClientHolder;
import cn.jants.plugin.pay.wx.*;
import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class WxPayTool {

    /**
     * 应用 appId, 应用 appSecret, 商户ID, 支付密钥, 通知地址
     */
    private String appId, mchId, payKey, certPath;

    private WxPayTool(String appId, String mchId, String payKey, String certPath) {
        this.appId = appId;
        this.mchId = mchId;
        this.payKey = payKey;
        this.certPath = certPath;
    }

    /**
     * 初始化工具类
     *
     * @param appId    应用ID
     * @param mchId    商户ID
     * @param payKey   支付秘钥
     * @param certPath 证书
     * @return
     */
    public static WxPayTool init(String appId, String mchId, String payKey, String certPath) {
        return new WxPayTool(appId, mchId, payKey, certPath);
    }

    public static WxPayTool init(String appId, String mchId, String payKey) {
        return new WxPayTool(appId, mchId, payKey, null);
    }

    /**
     * 获取App支付签名
     * 类型：PayType.APP
     *
     * @param params 支付参数
     */
    public WxPayApiResult getAppPaySign(WxPayParams params) {
        paramsValidate(params, "body", "out_trade_no", "total_fee", "notify_url", "trade_type");
        //IP地址
        params.setSpbillCreateIp(ClientHolder.getIp());
        WxPayApiResult unifiedOrderParams = unifiedOrderParams(params);
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

        return new WxPayApiResult(result);
    }

    /**
     * 获取JsApi支付签名
     * 类型：PayType.JSAPI
     *
     * @param params 支付参数
     * @return
     */
    public WxPayApiResult getJsApiPaySign(WxPayParams params) {
        paramsValidate(params, "body", "openid", "out_trade_no", "total_fee", "notify_url", "trade_type");
        //IP地址
        params.setSpbillCreateIp(ClientHolder.getIp());
        WxPayApiResult unifiedOrderParams = unifiedOrderParams(params);
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

        return new WxPayApiResult(result);
    }

    /**
     * 扫描支付, 主要是先生存订单, 在扫码完成支付
     * 类型：PayType.NATIVE
     *
     * @param params 支付参数
     * @return
     */
    public WxPayApiResult getScanCodePaySign(WxPayParams params) {
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
     * 微信退款
     *
     * @param params
     */
    public String refund(WxPayParams params) {
        paramsValidate(params, "out_trade_no", "refund_fee", "total_fee");
        params.setAppId(appId)
                .setMchId(mchId)
                .setNonceStr(StrUtil.randomUUID());
        params.setSign(Sign.md5Sign(params, payKey));
        String xml = MapXmlUtil.map2Xml(params, "xml");
        String responseXml = SslHttpRequest.p12Send(WxPayApiConstant.REFUND_API, xml, certPath, mchId);
        Map map = MapXmlUtil.xml2Map(responseXml, "xml");
        Log.debug("退款 Result - > {}", JSON.toJSONString(map, true));
        //成功
        if ("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code"))) {
            return String.valueOf(map.get("refund_id"));
        } else {
            throw new TipException(JSON.toJSONString(map, true));
        }
    }

    /**
     * 微信提现到零钱
     *
     * @param params
     */
    public String withdrawals(WxTxParams params) {
        paramsValidate(params, "partner_trade_no", "openid", "check_name", "amount", "desc");
        params.setAppId(appId)
                .setMchId(mchId)
                .setNonceStr(StrUtil.randomUUID())
                .setCreateIp(ClientHolder.getIp());
        params.setSign(Sign.md5Sign(params, payKey));
        String xml = MapXmlUtil.map2Xml(params, "xml");
        System.out.println(xml);
        String responseXml = SslHttpRequest.p12Send(WxPayApiConstant.WITHDRAWALS_API, xml, certPath, mchId);
        Map map = MapXmlUtil.xml2Map(responseXml, "xml");
        Log.debug("提现 Result - > {}", JSON.toJSONString(map, true));
        //成功
        if ("SUCCESS".equals(map.get("return_code")) && "SUCCESS".equals(map.get("result_code"))) {
            return String.valueOf(map.get("payment_no"));
        } else {
            throw new TipException(20001, String.valueOf(map.get("err_code_des")));
        }
    }

    /**
     * 查询订单, 关闭订单, 查询退款订单
     *
     * @param id   商户订单号
     * @param type 0/查询订单 1/关闭订单 2/查询退款订单
     * @return
     */
    public Map orderQuery(String id, int type) {
        WxPayParams params = WxPayParams.newPayParams()
                .setAppId(appId)
                .setMchId(mchId)
                .setNonceStr(StrUtil.randomUUID())
                .setOutTradeNo(id);
        params.setSign(Sign.md5Sign(params, payKey));
        String xml = MapXmlUtil.map2Xml(params, "xml");
        String url = WxPayApiConstant.UNIFIED_ORDER_QUERY_API;
        if (type == 1) {
            url = WxPayApiConstant.CLOSE_ORDER_API;
        } else if (type == 2) {
            url = WxPayApiConstant.REFUND_QUERY_API;
        }
        String responseXml = HttpUtil.sendPost(url, xml);
        return MapXmlUtil.xml2Map(responseXml, "xml");
    }

    /**
     * 根据流读取返回通知内容
     *
     * @return
     */
    public static WxNotifyResult getNotify() {
        try {
            HttpServletRequest request = ClientHolder.getRequest();
            String xmlStr = IOUtil.parseStr(request.getInputStream());
            return new WxNotifyResult(MapXmlUtil.xml2Map(xmlStr, "xml"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 统一订单生成
     */
    private WxPayApiResult unifiedOrderParams(WxPayParams params) {
        params.setAppId(appId)
                .setMchId(mchId)
                .setNonceStr(StrUtil.randomUUID());

        String orderXml = unifiedOrderXml(params);
        Log.debug("统一订单XML > {}", orderXml);
        //以上是生成订单数据map
        String responseXml = HttpUtil.sendPost(WxPayApiConstant.UNIFIED_ORDER_API, orderXml);
        Map unifiedOrderParams = MapXmlUtil.xml2Map(String.valueOf(responseXml), "xml");
        return new WxPayApiResult(unifiedOrderParams);
    }


    /**
     * 根据openid统一订单生成xml数据
     *
     * @param params 统一订单参数
     * @return
     */
    private String unifiedOrderXml(WxPayParams params) {
        String md5Sign = Sign.md5Sign(params, payKey);
        params.put("sign", md5Sign);
        return MapXmlUtil.map2Xml(params, "xml");
    }

    /**
     * 参数校验
     */
    private void paramsValidate(Map map, String... keys) {
        for (String key : keys) {
            if (!map.containsKey(key)) {
                throw new TipException(20003, String.format("缺少 %s 参数对象!", key));
            }
            if (map.get(key) == null) {
                throw new TipException(20003, String.format("%s 参数对象值不能为空!", key));
            }
        }
    }
}
