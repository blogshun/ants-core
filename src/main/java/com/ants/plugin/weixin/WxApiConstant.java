package com.ants.plugin.weixin;

/**
 * 微信Api接口常量
 *
 * @author MrShun
 * @version 1.0
 * @date 2017/12/23
 */
public interface WxApiConstant {

    /**
     * 网页重定向授权
     * GET
     */
    String OAUTH2_REDIRECT_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=STATE#wechat_redirect";

    /**
     * 网页授权得到code 获取access_token和openid
     * GET
     */
    String OAUTH2_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 根据获取access_token和openid获取更多的用户信息
     * GET
     */
    String USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
}
