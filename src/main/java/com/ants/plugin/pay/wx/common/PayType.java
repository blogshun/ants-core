package com.ants.plugin.pay.wx.common;

/**
 * 位微信支付类型枚举
 *
 * @author MrShun
 * @version 1.0
 */
public enum PayType {

    /**
     * 公众号支付
     */
    JSAPI("公众号支付"),
    /**
     * 扫码支付
     */
    NATIVE("扫码支付"),
    /**
     * APP支付
     */
    APP("APP支付"),
    /**
     * H5支付
     */
    MWEB("H5支付");

    private String type;

    PayType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
