package cn.jants.common.utils;

import cn.jants.common.bean.JsonMap;
import cn.jants.common.enums.ResponseCode;
import cn.jants.common.exception.TipException;
import cn.jants.plugin.cache.RedisTpl;
import cn.jants.plugin.tool.RedisTool;
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
     * 当前存储时间
     */
    private final static String CURRENT_TIME = "currentTime";

    /**
     * 用户Token前缀
     */
    private final static String USER_TOKEN_REDIS_PREFIX = "userToken:";

    /**
     * Token唯一对应ID,一般是用户唯一标识
     */
    private final static String TOKEN_ID_REDIS_PREFIX = "tokenId:";

    /**
     * 创建userTokenStr
     *
     * @param onlyId      唯一标识
     * @param userInfoExt 用户信息
     * @return
     */
    public static String generate(String onlyId, JsonMap userInfoExt) {
        long currentTime = System.currentTimeMillis();
        userInfoExt.put(CURRENT_TIME, currentTime);

        //MD5加密后进行编码
        String str1 = StrEncryptUtil.md5(onlyId.concat("&")
                .concat(StrUtil.randomUUID()).concat("&") + currentTime);
        byte[] bytes = str1.getBytes();
        String userTokenStr = Base64.getEncoder().encodeToString(bytes);

        //获取Redis对象
        RedisTpl redis = RedisTool.getRedis();
        //根据Token存储用户信息
        redis.set(USER_TOKEN_REDIS_PREFIX.concat(userTokenStr), JSON.toJSONString(userInfoExt));
        //根据用户唯一ID存储用户Token
        redis.set(TOKEN_ID_REDIS_PREFIX.concat(onlyId), userTokenStr);
        return userTokenStr;
    }


    /**
     * 检查userTokenStr有效性
     *
     * @param userTokenStr
     * @return
     */
    public static JsonMap checkValidity(String userTokenStr) {
        //获取Redis对象
        RedisTpl redis = RedisTool.getRedis();
        String userTokenKey = USER_TOKEN_REDIS_PREFIX.concat(userTokenStr);
        if (!redis.exists(userTokenKey)) {
            throw new TipException(ResponseCode.TOKEN_INVALID_INFO);
        }
        JsonMap userInfoExt = redis.get(userTokenKey, JsonMap.class);
        userInfoExt.put(CURRENT_TIME, System.currentTimeMillis());
        return userInfoExt;
    }

    /**
     * 删除用户userToken
     *
     * @param onlyId 唯一标识
     */
    public static void delUserToken(String onlyId) {
        //获取Redis对象
        RedisTpl redis = RedisTool.getRedis();
        String userTokenStr = redis.getStr(TOKEN_ID_REDIS_PREFIX.concat(onlyId));
        redis.remove(USER_TOKEN_REDIS_PREFIX.concat(userTokenStr));
    }

    /**
     * 查询用户信息
     *
     * @param userTokenStr
     */
    public static JsonMap findUserInfo(String userTokenStr) {
        //获取Redis对象
        RedisTpl redis = RedisTool.getRedis();
        return redis.get(USER_TOKEN_REDIS_PREFIX.concat(userTokenStr), JsonMap.class);
    }

    /**
     * 查询用户Token信息
     *
     * @param onlyId 唯一标识
     */
    public static String findUserToken(String onlyId) {
        //获取Redis对象
        RedisTpl redis = RedisTool.getRedis();
        return redis.getStr(TOKEN_ID_REDIS_PREFIX.concat(onlyId));
    }
}
