package com.ants.plugin.tool;

import com.alibaba.fastjson.JSON;
import com.alipay.api.*;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ants.common.bean.Log;
import com.ants.common.bean.Prop;
import com.ants.common.utils.StrUtil;
import com.ants.core.holder.ClientHolder;
import com.ants.plugin.pay.ali.AliNotifyResult;
import com.ants.plugin.pay.ali.AliPayApiResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class AliPayTool {
    /**
     * RSA2, 请求网关地址
     */
    private String signType = "RSA2", url = "https://openapi.alipay.com/gateway.do";

    /**
     * 商户appid, 私钥, 通知地址, 网页支付同步跳转地址, 公钥
     */
    private String notifyUrl, returnUrl, publicKey;

    private AlipayClient client;

    /**
     * 为了防止反复初始化
     */
    private final static ConcurrentMap<String, AliPayTool> ALI_CLIENT_MAP = new ConcurrentHashMap<>();

    private AliPayTool(String appId, String privateKey, String notifyUrl, String returnUrl, String publicKey) {
        client = new DefaultAlipayClient(url, appId, privateKey, "json", "UTF-8", publicKey, signType);
        this.notifyUrl = notifyUrl;
        this.returnUrl = returnUrl;
    }

    /**
     * 初始化工具类
     *
     * @param appId      商户appid
     * @param privateKey 私钥
     * @param notifyUrl  通知地址
     * @param returnUrl  网页支付同步跳转地址
     * @param publicKey  公钥
     * @return
     */
    public static AliPayTool init(String appId, String privateKey, String notifyUrl, String returnUrl, String publicKey) {
        appId = Prop.getKeyStrValue(appId);
        privateKey = Prop.getKeyStrValue(privateKey);
        notifyUrl = Prop.getKeyStrValue(notifyUrl);
        if (StrUtil.notBlank(returnUrl)) {
            returnUrl = Prop.getKeyStrValue(returnUrl);
        }
        publicKey = Prop.getKeyStrValue(publicKey);
        if (ALI_CLIENT_MAP.containsKey(appId)) {
            return ALI_CLIENT_MAP.get(appId);
        }
        AliPayTool aliPayTool = new AliPayTool(appId, privateKey, notifyUrl, returnUrl, publicKey);
        ALI_CLIENT_MAP.put(appId, aliPayTool);
        return aliPayTool;
    }

    public static AliPayTool init(String appId, String privateKey, String notifyUrl, String publicKey) {
        return init(appId, privateKey, notifyUrl, null, publicKey);
    }

    /**
     * Wap端发起支付
     * https://docs.open.alipay.com/203/105285/
     *
     * @param model 支付请求参数
     */
    public void printWapPay(AlipayTradePagePayModel model) {
        AlipayRequest aliPayRequest = new AlipayTradeWapPayRequest();
        model.setProductCode("QUICK_MSECURITY_PAY");
        aliPayRequest.setBizModel(model);
        // 设置异步通知地址
        aliPayRequest.setNotifyUrl(notifyUrl);
        // 设置同步地址
        aliPayRequest.setReturnUrl(returnUrl);
        try {
            String body = client.pageExecute(aliPayRequest).getBody();
            printPayPage(body);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            Log.error("支付宝生成WAP支付表单字符串失败 > {} !", e.getErrMsg());
        }
    }

    /**
     * Pc端发起支付宝支付
     * https://docs.open.alipay.com/270/105899/
     *
     * @param model 支付请求参数
     */
    public void printPcPay(AlipayTradePagePayModel model) {
        AlipayRequest aliPayRequest = new AlipayTradePagePayRequest();
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        aliPayRequest.setBizModel(model);
        // 设置异步通知地址
        aliPayRequest.setNotifyUrl(notifyUrl);
        // 设置同步地址
        aliPayRequest.setReturnUrl(returnUrl);
        try {
            String body = client.pageExecute(aliPayRequest).getBody();
            printPayPage(body);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            Log.error("支付宝生成PC支付表单字符串失败 > {} !", e.getErrMsg());
        }
    }

    /**
     * App发起支付支付
     * https://docs.open.alipay.com/204/105297/
     *
     * @param model 支付请求参数
     * @return
     */
    public AliPayApiResult getAppPaySign(AlipayTradeAppPayModel model) {
        AlipayTradeAppPayRequest appRequest = new AlipayTradeAppPayRequest();
        model.setProductCode("QUICK_MSECURITY_PAY");
        appRequest.setBizModel(model);
        // 设置异步通知地址
        appRequest.setNotifyUrl(notifyUrl);
        //这里和普通的接口调用不同，使用的是sdkExecute
        AlipayResponse response = null;
        try {
            response = client.sdkExecute(appRequest);
            String data = response.getBody();
            return new AliPayApiResult(data);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return new AliPayApiResult(e.getErrCode(), e.getErrMsg());
        }
    }

    /**
     * 发送退款请求
     * https://docs.open.alipay.com/api_1/alipay.trade.refund/
     *
     * @param model
     * @return
     */
    public AliPayApiResult sendRefund(AlipayTradeRefundModel model) {
        AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
        refundRequest.setBizModel(model);
        try {
            AlipayTradeRefundResponse response = client.execute(refundRequest);
            if (response.isSuccess()) {
                return new AliPayApiResult(response.getParams());
            }
            return new AliPayApiResult(response.getSubCode(), response.getSubMsg());
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return new AliPayApiResult(e.getErrCode(), e.getErrMsg());
        }
    }

    /**
     * 生成支付二维码
     * https://docs.open.alipay.com/api_1/alipay.trade.precreate/
     *
     * @param model 请求支付参数
     * @return
     */
    public AliPayApiResult createCodeUrl(AlipayTradePrecreateModel model) {
        AlipayTradePrecreateRequest createRequest = new AlipayTradePrecreateRequest();
        createRequest.setBizModel(model);
        try {
            AlipayTradePrecreateResponse response = client.execute(createRequest);
            if (response.isSuccess()) {
                return new AliPayApiResult(response.getParams());
            }
            return new AliPayApiResult(response.getSubCode(), response.getSubMsg());
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return new AliPayApiResult(e.getErrCode(), e.getErrMsg());
        }
    }

    /**
     * 发起单笔转账
     * https://docs.open.alipay.com/api_28/alipay.fund.trans.toaccount.transfer
     *
     * @param model 转账参数
     * @return
     */
    public AliPayApiResult sendTransfer(AlipayFundTransToaccountTransferModel model) {
        AlipayFundTransToaccountTransferRequest transferRequest = new AlipayFundTransToaccountTransferRequest();
        transferRequest.setBizContent(JSON.toJSONString(model));
        try {
            AlipayFundTransToaccountTransferResponse response = client.execute(transferRequest);
            if (response.isSuccess()) {
                Map jsonMap = new HashMap();
                jsonMap.put("orderId", response.getOrderId());
                jsonMap.put("outBizNo", response.getOutBizNo());
                return new AliPayApiResult(JSON.toJSONString(jsonMap));
            }
            return new AliPayApiResult(response.getSubCode(), response.getSubMsg());
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return new AliPayApiResult(e.getErrCode(), e.getErrMsg());
        }
    }

    /**
     * 获取支付通知
     *
     * @return
     */
    public AliNotifyResult getNotify() {
        HttpServletRequest request = ClientHolder.getRequest();
        String code = request.getParameter("code");
        AliNotifyResult notifyResult = null;
        if ("10000".equals(code)) {
            notifyResult = new AliNotifyResult(request);
        } else {
            notifyResult = new AliNotifyResult(code, request.getParameter("msg"));
        }
        return notifyResult;
    }

    /**
     * 输出到页面
     *
     * @param body 表单内容
     */
    private void printPayPage(String body) {
        //直接将完整的表单html输出到页面
        try {
            HttpServletResponse response = ClientHolder.getResponse();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(body);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
