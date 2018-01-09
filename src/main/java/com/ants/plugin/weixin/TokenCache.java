package com.ants.plugin.weixin;

/**
 * access_token 和 jsapi_ticket 共用
 *
 * @author MrShun
 * @version 1.0
 */
public class TokenCache {

    /**
     * 微信基本access_token
     */
    private String token;

    /**
     * 过期时间
     */
    private Long expires;

    public void setTokenCache(String token, Long expires) {
        this.token = token;
        this.expires = expires;
    }

    public String getToken() {
        return token;
    }

    public Long getExpires() {
        return expires;
    }
}
