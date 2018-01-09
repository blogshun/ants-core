package com.ants.plugin.weixin;

import com.ants.common.bean.Log;
import com.ants.common.utils.HttpUtil;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2018-01-09
 */
public class WxToken {

    /**
     * 全局缓存对象access_token
     */
    private static TokenCache accessTokenCache = new TokenCache();


    /**
     * 获取access_token
     *
     * @param appId
     * @param appSecret
     * @return
     */
    public static String getAccessTokenStr(String appId, String appSecret) {
        Long expires = accessTokenCache.getExpires();
        long currentTime = System.currentTimeMillis();
        //当第一次获取access_token 或者 时间设置已经过期
        if (expires == null || currentTime - expires > 7000000) {
            String response = HttpUtil.sendGet(String.format(WxApiConstant.ACCESS_TOKEN_API, appId, appSecret));
            ApiResult apiResult = new ApiResult(response);
            String accessToken = apiResult.getStr("access_token");
            accessTokenCache.setTokenCache(accessToken, currentTime);
            return accessToken;
        } else {
            String accessToken = accessTokenCache.getToken();
            Log.debug("取缓存AccessToken > {}", accessToken);
            return accessToken;
        }
    }
}
