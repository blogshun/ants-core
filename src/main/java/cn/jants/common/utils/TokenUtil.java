package cn.jants.common.utils;

import cn.jants.common.bean.JsonMap;
import cn.jants.common.enums.ResponseCode;
import cn.jants.common.exception.TipException;
import cn.jants.core.module.ServiceManager;
import cn.jants.plugin.cache.RedisTpl;
import com.alibaba.fastjson.JSON;

import java.util.Base64;

/**
 * 用户Token操作工具类
 *
 * @author MrShun
 * @version 1.0
 */
public class TokenUtil {

    /**
     * 用户唯一标识，获取Token
     *
     * @param onlyId
     * @return
     */
    private static String getToken(String onlyId) {
        //MD5加密后进行编码
        String str1 = StrEncryptUtil.md5(onlyId.concat("&").concat(System.currentTimeMillis() + "&ants"));
        byte[] bytes = str1.getBytes();
        String userTokenStr = Base64.getEncoder().encodeToString(bytes);
        return userTokenStr;
    }

    /**
     * 创建userTokenStr
     *
     * @param tokenPrefixName 前缀名称
     * @param onlyId          唯一标识
     * @param userInfoExt     用户信息
     * @param seconds         有效时间
     * @return
     */
    public static String generate(String tokenPrefixName, String onlyId, JsonMap userInfoExt, int seconds) {
        String userTokenStr = getToken(onlyId);
        //获取Redis对象
        RedisTpl redis = ServiceManager.getService(RedisTpl.class);
        redis.set(tokenPrefixName.concat(userTokenStr), JSON.toJSONString(userInfoExt), seconds);
        return userTokenStr;
    }


    /**
     * 检查userTokenStr有效性
     *
     * @param tokenStr
     * @param tokenPrefixName 前缀名称
     * @param seconds         有效时间
     * @return
     */
    public static JsonMap checkValidity(String tokenStr, String tokenPrefixName, int seconds) {
        String userTokenKey = tokenPrefixName.concat(tokenStr);
        //获取Redis对象
        RedisTpl redis = ServiceManager.getService(RedisTpl.class);
        if (!redis.exists(userTokenKey)) {
            throw new TipException(ResponseCode.TOKEN_INVALID_INFO);
        }

        JsonMap ret = redis.get(userTokenKey, JsonMap.class);
        if(seconds != -1){
            redis.expire(userTokenKey, seconds);
        }
        return ret;
    }

    public static JsonMap checkValidity(String tokenStr, String tokenPrefixName) {
        return checkValidity(tokenStr, tokenPrefixName, 60 * 30);
    }


    /**
     * 删除用户userToken
     *
     * @param userTokenStr 唯一标识
     */
    public static void delUserToken(String userTokenStr) {
        //获取Redis对象
        RedisTpl redis = ServiceManager.getService(RedisTpl.class);
        redis.remove(userTokenStr);
    }

    /**
     * 查询用户信息
     *
     * @param userTokenStr
     */
    public static JsonMap findUserInfo(String userTokenStr) {
        //获取Redis对象
        RedisTpl redis = ServiceManager.getService(RedisTpl.class);
        return redis.get(userTokenStr, JsonMap.class);
    }
}
