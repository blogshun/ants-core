package cn.jants.plugin.tool;

import cn.jants.common.bean.Log;
import cn.jants.common.bean.Prop;
import cn.jants.core.holder.ClientHolder;
import cn.jants.plugin.pay.wx.Sign;
import cn.jants.plugin.weixin.ApiResult;
import cn.jants.plugin.weixin.TokenCache;
import cn.jants.plugin.weixin.WxApiConstant;
import cn.jants.plugin.weixin.WxToken;
import cn.jants.common.utils.HttpUtil;
import cn.jants.common.utils.StrUtil;

import java.util.TreeMap;

/**
 * 微信JSSDK签名工具类
 *
 * @author MrShun
 * @version 1.0
 */
public class WxJsApiTool {

    /**
     * 全局缓存对象jsapi_ticket
     */
    private static TokenCache jsApiTicketCache = new TokenCache();

    /**
     * 获取jsapi_ticket
     *
     * @param appId
     * @param appSecret
     * @return
     */
    public static String getJsApiTicketStr(String appId, String appSecret) {
        appId = Prop.getKeyStrValue(appId);
        appSecret = Prop.getKeyStrValue(appSecret);
        Long expires = jsApiTicketCache.getExpires();
        long currentTime = System.currentTimeMillis();
        //当第一次获取jsapi_ticket 或者 时间设置已经过期
        if (expires == null || currentTime - expires > 7000000) {
            String accessTokenStr = WxToken.getAccessTokenStr(appId, appSecret);
            String response = HttpUtil.sendGet(String.format(WxApiConstant.TICKET_API, accessTokenStr));
            ApiResult apiResult = new ApiResult(response);
            String ticket = apiResult.getStr("ticket");
            jsApiTicketCache.setTokenCache(ticket, currentTime);
            return ticket;
        } else {
            String ticket = jsApiTicketCache.getToken();
            Log.debug("取缓存JsApiTicket > {}", ticket);
            return ticket;
        }
    }

    /**
     * 获取JsApiSignature签名
     *
     * @param appId
     * @param appSecret
     * @return
     */
    public static ApiResult getJsApiSignature(String appId, String appSecret) {
        appId = Prop.getKeyStrValue(appId);
        appSecret = Prop.getKeyStrValue(appSecret);
        String jsApiTicketStr = getJsApiTicketStr(appId, appSecret);
        Long timeStamp = System.currentTimeMillis() / 1000;
        String nonceStr = StrUtil.randomUUID();
        String webUrl = ClientHolder.getWebUrl();
        TreeMap params = new TreeMap();
        params.put("jsapi_ticket", jsApiTicketStr);
        params.put("noncestr", nonceStr);
        params.put("timestamp", timeStamp);
        params.put("url", webUrl);

        Log.debug("signature params > {}", params);
        ApiResult apiResult = new ApiResult();
        apiResult.put("appId", appId);
        apiResult.put("timestamp", timeStamp);
        apiResult.put("nonceStr", nonceStr);
        apiResult.put("signature", Sign.sha1(Sign.pj(params)));
        return apiResult;
    }
}
