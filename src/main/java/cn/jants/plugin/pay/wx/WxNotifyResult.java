package cn.jants.plugin.pay.wx;

import cn.jants.common.exception.TipException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 */
public class WxNotifyResult extends HashMap {

    private boolean ok;

    private String msg;

    private Map data;

    public WxNotifyResult(Map map) {
        Object errCode = map.get("return_code");
        if ("FAIL".equals(errCode)) {
            Object errMsg = map.get("err_code_des");
            throw new TipException(8002, String.format("%s", errMsg));
        } else {
            data = map;
            put("data", map);
            put("message", "ok");
            put("code", 0);
            ok = true;
        }
    }

    public boolean isOk() {
        return ok;
    }

    public String getMsg() {
        return msg;
    }

    public String getStr(String key) {
        return String.valueOf(data.get(key));
    }

    public void set(String key, Object value) {
        data.put(key, value);
    }

    /**
     * 微信支付分配的终端设备号
     *
     * @return
     */
    public String getDeviceInfo() {
        return getStr("device_info");
    }

    /**
     * 用户标识
     *
     * @return
     */
    public String getOpenId() {
        return getStr("openid");
    }

    /**
     * 是否关注过公众号
     *
     * @return
     */
    public boolean isSubscribe() {
        if ("Y".equals(getStr("is_subscribe"))) {
            return true;
        }
        return false;
    }

    /**
     * 交易类型 JSAPI、NATIVE、APP
     *
     * @return
     */
    public String getTradeType() {
        return getStr("trade_type");
    }

    /**
     * 微信支付订单号
     *
     * @return
     */
    public String getTransactionId() {
        return getStr("transaction_id");
    }

    /**
     * 商户订单号
     *
     * @return
     */
    public String getOutTradeNo() {
        return getStr("out_trade_no");
    }

    /**
     * 商家数据包 String(128)
     *
     * @return
     */
    public String getAttach() {
        return getStr("attach");
    }
}
