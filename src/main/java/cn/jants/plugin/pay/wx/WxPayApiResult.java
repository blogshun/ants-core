package cn.jants.plugin.pay.wx;

import cn.jants.common.exception.TipException;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单的封装支付返回
 *
 * @author MrShun
 * @version 1.0
 */
public class WxPayApiResult extends HashMap {

    private boolean ok;

    private String msg;

    private Map data;

    public WxPayApiResult(Map map) {
        Object errCode = map.get("return_code");
        if ("FAIL".equals(errCode)) {
            Object msg = map.get("return_msg");
            throw new TipException(8001, String.format("%s", msg));
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
     * 获取返回的签名
     *
     * @return
     */
    public String getSign() {
        return getStr("sign");
    }

    /**
     * 交易类型，取值为：JSAPI，NATIVE，APP等
     *
     * @return
     */
    public String getTradeType() {
        return getStr("trade_type");
    }

    /**
     * 微信生成的预支付会话标识，用于后续接口调用中使用，该值有效期为2小时
     *
     * @return
     */
    public String getPrepayId() {
        return getStr("prepay_id");
    }

    /**
     * trade_type为NATIVE时有返回，用于生成二维码，展示给用户进行扫码支付
     *
     * @return
     */
    public String getCodeUrl() {
        return getStr("code_url");
    }
}
