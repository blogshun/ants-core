package com.ants.plugin.pay.ali;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单的封装支付返回
 *
 * @author MrShun
 * @version 1.0
 */
public class AliPayApiResult extends HashMap {

    private boolean ok;

    private String msg;

    private String data;

    private Map<String, String> params;

    public AliPayApiResult(Map<String, String> params) {
        this.params = params;
        this.ok = true;
        this.msg = "ok";
        put("data", params);
        put("message", msg);
        put("code", 0);
    }

    public AliPayApiResult(String data) {
        this.data = data;
        this.ok = true;
        this.msg = "ok";
        put("data", data);
        put("message", msg);
        put("code", 0);
    }

    public AliPayApiResult(String code, String msg) {
        this.ok = false;
        this.msg = msg;
        put("message", msg);
        put("code", code);
    }

    public boolean isOk() {
        return ok;
    }

    public Object getMsg() {
        return msg;
    }

    /**
     * 获取支付参数
     *
     * @return
     */
    public String getPayParams() {
        return data;
    }


    public Map<String, String> getParams() {
        return params;
    }

    public String getParam(String key) {
        return params.get(key);
    }
}
