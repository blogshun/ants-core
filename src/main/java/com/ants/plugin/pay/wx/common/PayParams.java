package com.ants.plugin.pay.wx.common;

import java.math.BigInteger;
import java.util.TreeMap;

/**
 * 具体详情看同一订单此处和同一订单对应
 * TreeMap带排序功能
 *
 * @author MrShun
 * @version 1.0
 */
public class PayParams extends TreeMap {

    private PayParams() {
    }

    public static <K, V> PayParams newPayParams() {
        return new PayParams();
    }

    /**
     * @param appId 公众账号ID
     * @return
     */
    public PayParams setAppId(String appId) {
        put("appid", appId);
        return this;
    }

    /**
     * @param mchId 商户号
     * @return
     */
    public PayParams setMchId(String mchId) {
        put("mch_id", mchId);
        return this;
    }

    /**
     * @param deviceInfo 设备号
     * @return
     */
    public PayParams setDeviceInfo(String deviceInfo) {
        put("device_info", deviceInfo);
        return this;
    }

    /**
     * @param nonceStr 随机字符串
     * @return
     */
    public PayParams setNonceStr(String nonceStr) {
        put("nonce_str", nonceStr);
        return this;
    }

    /**
     * @param sign 签名
     * @return
     */
    public PayParams setSign(String sign) {
        put("sign", sign);
        return this;
    }

    /**
     * @param signType 签名类型
     * @return
     */
    public PayParams setSignType(String signType) {
        put("sign_type", signType);
        return this;
    }

    /**
     * @param body 商品描述
     * @return
     */
    public PayParams setBody(String body) {
        put("body", body);
        return this;
    }

    /**
     * @param detail 商品详情
     * @return
     */
    public PayParams setDetail(String detail) {
        put("detail", detail);
        return this;
    }

    /**
     * @param attach 附加数据
     * @return
     */
    public PayParams setAttach(String attach) {
        put("attach", attach);
        return this;
    }

    /**
     * @param outTradeNo 商户订单号
     * @return
     */
    public PayParams setOutTradeNo(String outTradeNo) {
        put("out_trade_no", outTradeNo);
        return this;
    }

    /**
     * @param feeType 标价币种
     * @return
     */
    public PayParams setFeeType(String feeType) {
        put("fee_type", feeType);
        return this;
    }

    /**
     * @param totalFee 标价金额
     * @return
     */
    public PayParams setTotalFee(BigInteger totalFee) {
        put("total_fee", totalFee);
        return this;
    }

    /**
     * @param spbillCreateIp 终端IP
     * @return
     */
    public PayParams setSpbillCreateIp(String spbillCreateIp) {
        put("spbill_create_ip", spbillCreateIp);
        return this;
    }

    /**
     * @param timeStart 交易起始时间
     * @return
     */
    public PayParams setTimeStart(String timeStart) {
        put("time_start", timeStart);
        return this;
    }
    /**
     * @param timeExpire 交易结束时间
     * @return
     */
    public PayParams setTimeExpire(String timeExpire) {
        put("time_expire", timeExpire);
        return this;
    }
    /**
     * @param goodsTag 订单优惠标记
     * @return
     */
    public PayParams setGoodsTag(String goodsTag) {
        put("goods_tag", goodsTag);
        return this;
    }

    /**
     * @param notifyUrl 通知地址
     * @return
     */
    public PayParams setNotifyUrl(String notifyUrl) {
        put("notify_url", notifyUrl);
        return this;
    }

    /**
     * @param tradeType 交易类型
     * @return
     */
    public PayParams setTradeType(PayType tradeType) {
        put("trade_type", tradeType);
        return this;
    }

    /**
     * @param productId 商品ID
     * @return
     */
    public PayParams setProductId(String productId) {
        put("product_id", productId);
        return this;
    }

    /**
     * @param limitPay 指定支付方式
     * @return
     */
    public PayParams setTradeLimitPay(String limitPay) {
        put("limit_pay", limitPay);
        return this;
    }

    /**
     * @param openid 用户标识
     * @return
     */
    public PayParams setOpenId(String openid) {
        put("openid", openid);
        return this;
    }

    /**
     * @param sceneInfo 场景信息
     * @return
     */
    public PayParams setSceneInfo(String sceneInfo) {
        put("scene_info", sceneInfo);
        return this;
    }

}
