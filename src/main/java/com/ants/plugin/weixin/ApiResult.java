package com.ants.plugin.weixin;

import com.alibaba.fastjson.JSON;
import com.ants.common.bean.Log;
import com.ants.common.exception.TipException;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信接口返回API
 *
 * @author MrShun
 * @version 1.0
 * @Date 2017-06-22
 */
public class ApiResult extends HashMap {

    private boolean ok;

    private Object msg;
    /**
     * 数据对象
     */
    private Map data;

    public ApiResult(){
        put("message", "ok");
        put("code", 0);
    }

    public ApiResult(String result) {
        Map map = JSON.parseObject(result, Map.class);
        Object errcode = map.get("errcode");
        if (errcode == null || errcode.equals(0)) {
            this.data = map;
            this.ok = true;
            put("data", data);
            put("message", "ok");
            put("code", 0);
        } else {
            this.msg = String.valueOf(map.get("errmsg"));
            put("message", msg);
            put("code", errcode);
            throw new TipException(String.format("code -> %s, msg -> %s", errcode, msg));
        }

    }

    public boolean isOk() {
        return ok;
    }

    public Object getMsg() {
        return msg;
    }

    public String getStr(String key){
        return String.valueOf(data.get(key));
    }
}