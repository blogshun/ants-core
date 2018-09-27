package cn.jants.plugin.tool;

import cn.jants.common.bean.JsonMap;
import cn.jants.common.enums.ResponseCode;
import cn.jants.common.exception.TipException;
import cn.jants.common.utils.HttpUtil;
import cn.jants.common.utils.IOUtil;
import cn.jants.plugin.weixin.TokenCache;
import com.alibaba.fastjson.JSON;
import org.apache.commons.net.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class BaiDuContentTool {

    /**
     * 应用 ak, 应用 sk
     */
    private String ak, sk;

    /**
     * 文本审核接口
     */
    private final String TEXT_VERIFY_URL = "https://aip.baidubce.com/rest/2.0/antispam/v2/spam";

    /**
     * 图片审核接口
     */
    private final String IMAGE_VERIFY_URL = "https://aip.baidubce.com/api/v1/solution/direct/img_censor";

    /**
     * 百度accessToken 接口
     */
    private final String ACCESS_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token" +
            "?grant_type=client_credentials&client_id=%s&client_secret=%s";

    /**
     * 全局缓存对象
     */
    private TokenCache tokenCache = new TokenCache();

    /**
     * 为了防止反复初始化
     */
    private final static ConcurrentMap<String, BaiDuContentTool> BDT_MAP = new ConcurrentHashMap<>();


    public BaiDuContentTool(String ak, String sk) {
        this.ak = ak;
        this.sk = sk;
    }

    public static BaiDuContentTool init(String ak, String sk) {
        String key = ak.concat("_").concat(sk);
        if (BDT_MAP.containsKey(key)) {
            return BDT_MAP.get(key);
        }
        BaiDuContentTool baiDuContentTool = new BaiDuContentTool(ak, sk);
        BDT_MAP.put(key, baiDuContentTool);
        return baiDuContentTool;
    }

    /**
     * 查询百度accessToken
     *
     * @return
     */
    private String getAccessToken(String ak, String sk) {
        Long expires = tokenCache.getExpires();
        long currentTime = System.currentTimeMillis();
        if (expires == null || currentTime - expires > 2160000000L) {
            String resp = HttpUtil.sendGet(String.format(ACCESS_TOKEN_URL, ak, sk));
            JsonMap jsonMap = JSON.parseObject(resp, JsonMap.class);
            String accessTokenStr = jsonMap.getStr("access_token");
            tokenCache.setTokenCache(accessTokenStr, currentTime);
            return accessTokenStr;
        } else {
            return tokenCache.getToken();
        }
    }

    /**
     * 文本审核
     * +spam	int	请求中是否包含违禁，0表示非违禁，1表示违禁，2表示建议人工复审
     * +reject	array	审核未通过的类别列表与详情
     * +review	array	待人工复审的类别列表与详情
     * +pass	array	审核通过的类别列表与详情
     * ++label	int	请求中的违禁类型
     * ++score	float	违禁检测分，范围0~1，数值从低到高代表风险程度的高低
     * ++hit	array	违禁类型对应命中的违禁词集合，可能为空
     *
     * @param text 需要审核得内容
     * @param spam true/严格 false/复审核
     */
    public void textVerify(String text, boolean spam) {
        Map params = new HashMap<>(2);
        params.put("content", text);
        params.put("access_token", getAccessToken(ak, sk));
        String resp = HttpUtil.sendPost(TEXT_VERIFY_URL, params);
        JsonMap jsonMap = JSON.parseObject(resp, JsonMap.class);

        if (jsonMap.get("error_code") != null) {
            throw new TipException(ResponseCode.UNKNOWN_ERROR.getCode(), "baidu api 调用失败 -> " + jsonMap.getStr("error_msg"));
        } else {
            Map res = (Map) (jsonMap.get("result"));
            Integer yz = Integer.valueOf(res.get("spam") + "");
            if (spam) {
                if (yz > 0) {
                    throw new TipException(ResponseCode.CONTENT_GARBAGE_INFO);
                }
            } else {
                if (yz == 1) {
                    throw new TipException(ResponseCode.CONTENT_GARBAGE_INFO);
                }
            }
        }
    }

    public void textVerify(String text) {
        textVerify(text, true);
    }


    /**
     * 文本审核
     * conclusionType	uint64	否	审核结果标识，成功才返回，失败不返回。可取值1:合规, 2:不合规, 3:疑似, 4:审核失败
     *
     * @param in 输入流
     */
    public void imageVerify(InputStream in) throws Exception {
        Map params = new HashMap<>(2);
        ByteArrayOutputStream data = IOUtil.parse(in);

        params.put("image", new String(Base64.encodeBase64(data.toByteArray())));
        params.put("access_token", getAccessToken(ak, sk));
        params.put("scenes", new String[]{"webimage"});
        String resp = HttpUtil.sendPost(IMAGE_VERIFY_URL, params);
        JsonMap jsonMap = JSON.parseObject(resp, JsonMap.class);

        if (jsonMap.get("error_code") != null) {
            throw new TipException(ResponseCode.UNKNOWN_ERROR.getCode(), "baidu api 调用失败 -> " + jsonMap.getStr("error_msg"));
        } else {
            Integer conclusionType = jsonMap.getInt("conclusionType");
            if (conclusionType == 2) {
                throw new TipException(ResponseCode.IMAGE_GARBAGE_INFO);
            }
            System.out.println(JSON.toJSONString(jsonMap, true));
        }
    }

}
