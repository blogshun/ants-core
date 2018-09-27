package cn.jants.plugin.weixin;

import cn.jants.common.exception.TipException;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信接口返回API
 *
 * @author MrShun
 * @version 1.0
 */
public class ApiResult extends HashMap {

    private boolean ok;

    private String msg;
    /**
     * 数据对象
     */
    private Map data;

    public ApiResult() {
        put("message", "ok");
        put("code", 0);
    }

    public ApiResult(String result) {
        Map map = JSON.parseObject(result, Map.class);
        Object errCode = map.get("errcode");
        if (errCode == null || errCode.equals(0)) {
            this.data = map;
            this.ok = true;
            put("data", data);
            put("message", "ok");
            put("code", 0);
        } else {
            Object msg = map.get("errmsg");
            throw new TipException(8003, String.format("%s", errCode, msg));
        }

    }

    public boolean isOk() {
        return ok;
    }

    public String getMsg() {
        return msg;
    }

    public String getStr(String key) {
        return String.valueOf(data.get(key));
    }
}