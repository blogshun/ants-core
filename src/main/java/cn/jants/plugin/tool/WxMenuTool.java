package cn.jants.plugin.tool;

import cn.jants.common.bean.JsonMap;
import cn.jants.common.bean.Prop;
import cn.jants.common.utils.HttpUtil;
import cn.jants.plugin.weixin.ApiResult;
import cn.jants.plugin.weixin.WxApiConstant;
import cn.jants.plugin.weixin.WxToken;

/**
 * @author MrShun
 * @version 1.0
 */
public class WxMenuTool {


    /**
     * 查询自定义菜单
     *
     * @param appId
     * @param appSecret
     * @return
     */
    public static ApiResult getMenu(String appId, String appSecret) {
        appId = Prop.getKeyStrValue(appId);
        appSecret = Prop.getKeyStrValue(appSecret);
        String accessToken = WxToken.getAccessTokenStr(appId, appSecret);
        String result = HttpUtil.sendGet(String.format(WxApiConstant.MENU_GET_API, accessToken));
        return new ApiResult(result);
    }


    /**
     * 自定义菜单删除接口可以删除所有自定义菜单（包括默认菜单和全部个性化菜单）
     *
     * @return
     */
    public static ApiResult delMenu(String appId, String appSecret) {
        appId = Prop.getKeyStrValue(appId);
        appSecret = Prop.getKeyStrValue(appSecret);
        String accessToken = WxToken.getAccessTokenStr(appId, appSecret);
        String result = HttpUtil.sendGet(String.format(WxApiConstant.MENU_DELETE_API, accessToken));
        return new ApiResult(result);
    }

    /**
     * 创建自定义菜单
     *
     * @param appId
     * @param appSecret
     * @param jsonStr
     * @return
     */
    public static ApiResult createMenu(String appId, String appSecret, String jsonStr) {
        appId = Prop.getKeyStrValue(appId);
        appSecret = Prop.getKeyStrValue(appSecret);
        String accessToken = WxToken.getAccessTokenStr(appId, appSecret);
        String result = HttpUtil.sendPost(String.format(WxApiConstant.MENU_CREATE_API, accessToken), jsonStr);
        return new ApiResult(result);
    }

    /**
     * 添加个性化菜单
     *
     * @param appId
     * @param appSecret
     * @param jsonStr
     * @return
     */
    public static ApiResult addConditional(String appId, String appSecret, String jsonStr) {
        appId = Prop.getKeyStrValue(appId);
        appSecret = Prop.getKeyStrValue(appSecret);
        String accessToken = WxToken.getAccessTokenStr(appId, appSecret);
        String result = HttpUtil.sendPost(String.format(WxApiConstant.CONDITIONAL_ADD_API, accessToken), jsonStr);
        return new ApiResult(result);
    }


    /**
     * 删除个性化菜单
     *
     * @param menuId 菜单ID
     * @return
     */
    public static ApiResult delConditional(String menuId) {
        return null;
    }


    /**
     * 测试个性化菜单匹配结果
     *
     * @param appId
     * @param appSecret
     * @param userId    user_id可以是粉丝的OpenID，也可以是粉丝的微信号。
     * @return
     */
    public static ApiResult tryMatch(String appId, String appSecret, String userId) {
        appId = Prop.getKeyStrValue(appId);
        appSecret = Prop.getKeyStrValue(appSecret);
        String accessToken = WxToken.getAccessTokenStr(appId, appSecret);
        String result = HttpUtil.sendPost(String.format(WxApiConstant.TRY_MATCH_API, accessToken), JsonMap.newJsonMap().set("user_id", userId));
        return new ApiResult(result);
    }

    /**
     * 获取自定义菜单或者微信运营者设置菜单
     *
     * @return
     */
    public static ApiResult getCurrentSelfMenu() {
        return null;
    }
}
