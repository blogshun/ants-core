package com.ants.plugin.pay.ali.common;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class NotifyResult extends HashMap {


    private HttpServletRequest request;

    private boolean ok;

    private String msg;

    public NotifyResult(HttpServletRequest request){
        this.request = request;
        this.ok = true;
        this.msg = "ok";
        put("message", "ok");
        put("data", request.getParameterMap());
        put("code", 0);
    }

    public NotifyResult(String code, String msg){
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
     * 应用ID
     */
    public String getAppId() {
        return request.getParameter("app_id");
    }

    /**
     * 商户网站唯一订单号
     */
    public String getOutTradeNo() {
        return request.getParameter("out_trade_no");
    }

    /**
     * 支付宝交易流水号
     */
    public String getTradeNo() {
        return request.getParameter("trade_no");
    }

    /**
     * 收款支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字
     */
    public String getSellerId() {
        return request.getParameter("seller_id");
    }
}
