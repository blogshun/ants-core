package com.ants.plugin.weixin;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017/12/23
 */
public enum SnsApi {

    /**
     * 只获取微信OpenId
     */
    Base("snsapi_base", "只获取微信OpenId"),

    /**
     * 获取微信用户信息
     */
    UserInfo("snsapi_userinfo", "获取微信用户信息");

    private String type;

    private String desc;

    SnsApi(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
