package cn.jants.plugin.tool;

import cn.jants.common.bean.Prop;
import cn.jants.common.utils.HttpUtil;
import cn.jants.plugin.weixin.ApiResult;
import cn.jants.plugin.weixin.TplMsgParams;
import cn.jants.plugin.weixin.WxApiConstant;
import cn.jants.plugin.weixin.WxToken;
import com.alibaba.fastjson.JSON;

/**
 * @author MrShun
 * @version 1.0
 */
public class WxTplMsgTool {

    /**
     * 获取模板消息列表
     *
     * @return
     */
    public static ApiResult getTplMsgList(String appId, String appSecret) {
        appId = Prop.getKeyStrValue(appId);
        appSecret = Prop.getKeyStrValue(appSecret);
        String accessToken = WxToken.getAccessTokenStr(appId, appSecret);
        String result = HttpUtil.sendGet(String.format(WxApiConstant.TEMPLATE_LIST_API, accessToken));
        return new ApiResult(result);
    }

    /**
     * 发送模板消息
     *
     * @return
     */
    public static ApiResult sendTplMsg(String appId, String appSecret, TplMsgParams tplMsgParams) {
        appId = Prop.getKeyStrValue(appId);
        appSecret = Prop.getKeyStrValue(appSecret);
        String accessToken = WxToken.getAccessTokenStr(appId, appSecret);
        String result = HttpUtil.sendPost(String.format(WxApiConstant.SEND_TEMPLATE_API, accessToken), JSON.toJSONString(tplMsgParams));
        return new ApiResult(result);
    }


    public static void main(String[] args) {
        TplMsgParams tplMsgParams = new TplMsgParams();
        tplMsgParams.setToUser("o1zX7syRdNhWfqDmWauhcUeBL8Tk");
        tplMsgParams.setTemplateId("D-Pl3NVq4X32Mr6t76tylt1O7CsAv5uEo5fATg0830o");
        ApiResult result = sendTplMsg("wx89091f47367d3552", "3cfa4efa1f26a28486960d8ca63783e0", tplMsgParams);
        System.out.println(result);
    }
}
