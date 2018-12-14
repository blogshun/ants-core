package cn.jants.plugin.pay.wx;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.TreeMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class WxTxParams extends TreeMap {


    private WxTxParams() {
    }

    public static WxTxParams newPayParams() {
        return new WxTxParams();
    }

    /**
     * @param tradeNo 订单号
     * @return
     */
    public WxTxParams setTradeNo(String tradeNo) {
        put("partner_trade_no", tradeNo);
        return this;
    }

    /**
     * @param appId 公众账号ID
     * @return
     */
    public WxTxParams setAppId(String appId) {
        put("mch_appid", appId);
        return this;
    }

    /**
     * @param mchId 商户号
     * @return
     */
    public WxTxParams setMchId(String mchId) {
        put("mchid", mchId);
        return this;
    }

    /**
     * @param openId 用户openid
     * @return
     */
    public WxTxParams setOpenId(String openId) {
        put("openid", openId);
        return this;
    }

    /**
     * @param realName 校验真实姓名
     * @return
     */
    public WxTxParams checkName(String realName) {
        put("check_name", "FORCE_CHECK");
        put("re_user_name", realName);
        return this;
    }

    //不校验真实姓名
    public WxTxParams noCheckName() {
        put("check_name", "NO_CHECK");
        return this;
    }

    /**
     * @param amount 提现金额
     * @return
     */
    public WxTxParams setAmount(BigInteger amount) {
        put("amount", amount);
        return this;
    }

    /**
     * @param desc 提现描述
     * @return
     */
    public WxTxParams setDesc(String desc) {
        put("desc", desc);
        return this;
    }

    /**
     * @param ip 提现客户端ip
     * @return
     */
    public WxTxParams setCreateIp(String ip) {
        put("spbill_create_ip", ip);
        return this;
    }

    /**
     * @param nonceStr 随机字符串
     * @return
     */
    public WxTxParams setNonceStr(String nonceStr) {
        put("nonce_str", nonceStr);
        return this;
    }

    /**
     * @param sign 签名
     * @return
     */
    public WxTxParams setSign(String sign) {
        put("sign", sign);
        return this;
    }

}
