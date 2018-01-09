package com.ants.plugin.pay.ali.common;

import java.util.HashMap;

/**
 * 简单的封装支付返回
 *
 * @author MrShun
 * @version 1.0
 */
public class PayApiResult extends HashMap {

    private boolean ok;

    private String msg;

    private String data;

    public PayApiResult(String data) {
        this.data = data;
        this.ok = true;
        this.msg = "ok";
        put("data", data);
        put("message", msg);
        put("code", 0);
    }

    public PayApiResult(String code, String msg) {
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
}
