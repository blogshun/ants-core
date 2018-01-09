package com.ants.plugin.pay.wx;

import java.math.BigInteger;
import java.util.TreeMap;

/**
 * 具体详情看同一订单此处和同一订单对应
 * TreeMap带排序功能
 *
 * @author MrShun
 * @version 1.0
 */
public class WxPayParams extends TreeMap {

    private WxPayParams() {
    }

    public static <K, V> WxPayParams newPayParams() {
        return new WxPayParams();
    }

    /**
     * @param appId 公众账号ID
     * @return
     */
    public WxPayParams setAppId(String appId) {
        put("appid", appId);
        return this;
    }

    /**
     * @param mchId 商户号
     * @return
     */
    public WxPayParams setMchId(String mchId) {
        put("mch_id", mchId);
        return this;
    }

    /**
     * @param deviceInfo 设备号
     * @return
     */
    public WxPayParams setDeviceInfo(String deviceInfo) {
        put("device_info", deviceInfo);
        return this;
    }

    /**
     * @param nonceStr 随机字符串
     * @return
     */
    public WxPayParams setNonceStr(String nonceStr) {
        put("nonce_str", nonceStr);
        return this;
    }

    /**
     * @param sign 签名
     * @return
     */
    public WxPayParams setSign(String sign) {
        put("sign", sign);
        return this;
    }

    /**
     * @param signType 签名类型
     * @return
     */
    public WxPayParams setSignType(String signType) {
        put("sign_type", signType);
        return this;
    }

    /**
     * @param body 商品描述
     * @return
     */
    public WxPayParams setBody(String body) {
        put("body", body);
        return this;
    }

    /**
     * @param detail 商品详情
     * @return
     */
    public WxPayParams setDetail(String detail) {
        put("detail", detail);
        return this;
    }

    /**
     * @param attach 附加数据
     * @return
     */
    public WxPayParams setAttach(String attach) {
        put("attach", attach);
        return this;
    }

    /**
     * @param outTradeNo 商户订单号
     * @return
     */
    public WxPayParams setOutTradeNo(String outTradeNo) {
        put("out_trade_no", outTradeNo);
        return this;
    }

    /**
     * @param feeType 标价币种
     * @return
     */
    public WxPayParams setFeeType(String feeType) {
        put("fee_type", feeType);
        return this;
    }

    /**
     * @param totalFee 标价金额
     * @return
     */
    public WxPayParams setTotalFee(BigInteger totalFee) {
        put("total_fee", totalFee);
        return this;
    }

    /**
     * @param spbillCreateIp 终端IP
     * @return
     */
    public WxPayParams setSpbillCreateIp(String spbillCreateIp) {
        put("spbill_create_ip", spbillCreateIp);
        return this;
    }

    /**
     * @param timeStart 交易起始时间
     * @return
     */
    public WxPayParams setTimeStart(String timeStart) {
        put("time_start", timeStart);
        return this;
    }
    /**
     * @param timeExpire 交易结束时间
     * @return
     */
    public WxPayParams setTimeExpire(String timeExpire) {
        put("time_expire", timeExpire);
        return this;
    }
    /**
     * @param goodsTag 订单优惠标记
     * @return
     */
    public WxPayParams setGoodsTag(String goodsTag) {
        put("goods_tag", goodsTag);
        return this;
    }

    /**
     * @param notifyUrl 通知地址
     * @return
     */
    public WxPayParams setNotifyUrl(String notifyUrl) {
        put("notify_url", notifyUrl);
        return this;
    }

    /**
     * @param tradeType 交易类型
     * @return
     */
    public WxPayParams setTradeType(PayType tradeType) {
        put("trade_type", tradeType);
        return this;
    }

    /**
     * @param productId 商品ID
     * @return
     */
    public WxPayParams setProductId(String productId) {
        put("product_id", productId);
        return this;
    }

    /**
     * @param limitPay 指定支付方式
     * @return
     */
    public WxPayParams setTradeLimitPay(String limitPay) {
        put("limit_pay", limitPay);
        return this;
    }

    /**
     * @param openid 用户标识
     * @return
     */
    public WxPayParams setOpenId(String openid) {
        put("openid", openid);
        return this;
    }

    /**
     * @param sceneInfo 场景信息
     * @return
     */
    public WxPayParams setSceneInfo(String sceneInfo) {
        put("scene_info", sceneInfo);
        return this;
    }

}
