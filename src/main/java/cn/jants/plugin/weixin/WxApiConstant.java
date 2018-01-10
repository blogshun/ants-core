package cn.jants.plugin.weixin;

/**
 * 微信Api接口常量
 *
 * @author MrShun
 * @version 1.0
 */
public interface WxApiConstant {

    /**
     * 网页重定向授权
     * GET
     */
    String OAUTH2_REDIRECT_API = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=STATE#wechat_redirect";

    /**
     * 网页授权得到code 获取access_token和openid
     * GET
     */
    String OAUTH2_ACCESS_TOKEN_API = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 根据获取access_token和openid获取更多的用户信息
     * GET
     */
    String USER_INFO_API = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

    /**
     * 获取基础支持access_token
     * GET
     */
    String ACCESS_TOKEN_API = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    /**
     * 请求获得jsapi_ticket
     * GET
     */
    String TICKET_API = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";

    /**
     * 请求获取消息目标列表
     * GET
     */
    String TEMPLATE_LIST_API = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=%s";

    /**
     * 发送模板消息
     * POST JSON数据
     */
    String SEND_TEMPLATE_API = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";


    /**
     * 创建自定义菜单
     * POST
     */
    String MENU_CREATE_API = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";

    /**
     * 删除自定义菜单
     * GET
     */
    String MENU_DELETE_API = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=%s";

    /**
     * 查询自定义菜单
     * GET
     */
    String MENU_GET_API = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=%s";

    /**
     * 添加个性菜单
     * POST
     */
    String CONDITIONAL_ADD_API = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=%s";

    /**
     * 删除个性菜单
     * POST
     */
    String CONDITIONAL_DEL_API = "https://api.weixin.qq.com/cgi-bin/menu/delconditional?access_token=%s";

    /**
     * 测试个性化菜单匹配结果
     * POST
     */
    String TRY_MATCH_API = "https://api.weixin.qq.com/cgi-bin/menu/trymatch?access_token=%s";

    /**
     * 自定义菜单或者微信运营者设置菜单
     */
    String CURRENT_SELF_MENU_API = "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=%s";
}
