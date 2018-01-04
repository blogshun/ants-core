package com.ants.plugin.weixin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017/11/23
 */
public class WxUserMap extends HashMap {

    private boolean ok;

    private Object msg;

    public WxUserMap(Map map) {
        super(map);
        if (get("errcode") != null) {
            ok = false;
            msg = get("errmsg");
        } else {
            ok = true;
        }
    }

    public boolean isOk() {
        return ok;
    }

    public Object getMsg() {
        return msg;
    }

    public String getOpenId() {
        return String.valueOf(get("openid"));
    }

    public String getNickName() {
        return String.valueOf(get("nickname"));
    }

    public Integer getSex() {
        return Integer.valueOf(String.valueOf(get("sex")));
    }

    public String getProvince() {
        return String.valueOf(get("province"));
    }

    public String getCity() {
        return String.valueOf(get("city"));
    }

    public String getCountry() {
        return String.valueOf(get("country"));
    }

    public String getHeadImgUrl() {
        return String.valueOf(get("headimgurl"));
    }


    public String getUnionId() {
        return String.valueOf(get("unionid"));
    }
}
