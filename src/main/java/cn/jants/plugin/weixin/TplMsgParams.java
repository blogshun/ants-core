package cn.jants.plugin.weixin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 */
public class TplMsgParams extends HashMap {


    /**
     * @param openId 目标用户
     */
    public void setToUser(String openId) {
        put("touser", openId);
    }

    /**
     * @param templateId 目标用户
     */
    public void setTemplateId(String templateId) {
        put("template_id", templateId);
    }

    /**
     * @param url 模板跳转的URL
     */
    public void setUrl(String url) {
        put("url", url);
    }

    /**
     * 跳转小程序
     *
     * @param appId    小程序appId
     * @param pagePath 带参数,（示例index?foo=bar）
     */
    public void setMiniProgram(String appId, String pagePath) {
        Map map = new HashMap();
        map.put("appid", appId);
        map.put("pagepath", pagePath);
        put("miniprogram", map);
    }

    /**
     * @param dataMap 模板数据
     */
    public void setData(Map dataMap) {
        put("data", dataMap);
    }

    /**
     * 模板内容字体颜色，不填默认为黑色
     *
     * @param color
     */
    public void setColor(String color) {
        put("color", color);
    }
}
