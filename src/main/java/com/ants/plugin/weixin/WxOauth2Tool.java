package com.ants.plugin.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ants.common.bean.Log;
import com.ants.common.bean.Prop;
import com.ants.common.utils.HttpUtil;
import com.ants.core.holder.ClientHolder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class WxOauth2Tool {

    /**
     * 应用 appId
     */
    private String appId;

    /**
     * 应用 appSecret
     */
    private String appSecret;

    /**
     * 为了防止反复初始化
     */
    private final static ConcurrentMap<String, WxOauth2Tool> OAUTH_MAP = new ConcurrentHashMap<>();

    private WxOauth2Tool(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
    }

    /**
     * 初始化工具类
     *
     * @param appId
     * @param appSecret
     * @return
     */
    public static WxOauth2Tool init(String appId, String appSecret) {
        appId = Prop.getKeyStrValue(appId);
        appSecret = Prop.getKeyStrValue(appSecret);
        String key = appId.concat("_").concat(appSecret);
        if (OAUTH_MAP.containsKey(key)) {
            return OAUTH_MAP.get(key);
        }
        WxOauth2Tool wxOauth2 = new WxOauth2Tool(appId, appSecret);
        OAUTH_MAP.put(key, wxOauth2);
        return wxOauth2;
    }

    /**
     * 拼接获取跳转的oauth2链接
     *
     * @param scope 获取基本openid[snsapi_base] 获取用户信息[snsapi_userinfo]
     * @return
     */
    public String getOauth2Url(SnsApi scope) {
        HttpServletRequest request = ClientHolder.getRequest();
        StringBuffer sbUrl = new StringBuffer();
        sbUrl.append("http://" + request.getServerName() + request.getContextPath() + request.getServletPath());
        String oauth2Url;
        try {
            String enUrl = URLEncoder.encode(sbUrl.toString(), "utf-8");
            oauth2Url = String.format(WxApiConstant.OAUTH2_REDIRECT_URL, appId, enUrl, scope.getType());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("url编码转换异常!");
        }
        return oauth2Url;
    }

    /**
     * 获取AccessTokenStr
     *
     * @param code 授权页面code
     * @return
     */
    public JSONObject getAccessTokenStr(String code) {
        String oauth2UrlStr = String.format(WxApiConstant.OAUTH2_ACCESS_TOKEN_URL, appId, appSecret, code);
        String responseStr = HttpUtil.sendGet(oauth2UrlStr);
        Log.debug("基本授权返回数据 >> {} ", responseStr);
        return JSON.parseObject(responseStr);
    }

    /**
     * 获取用户更多资料信息
     *
     * @param code 授权页面code
     * @return
     */
    public WxUserMap getUserInfo(String code) {
        JSONObject result = getAccessTokenStr(code);
        String accessToken = result.getString("access_token");
        String openId = result.getString("openid");
        String userInfoUrlStr = String.format(WxApiConstant.USER_INFO_URL, accessToken, openId);
        String responseStr = HttpUtil.sendGet(userInfoUrlStr);
        Log.debug("获取基本用户数据 >> {} ", responseStr);
        return new WxUserMap(JSON.parseObject(responseStr));
    }

}
