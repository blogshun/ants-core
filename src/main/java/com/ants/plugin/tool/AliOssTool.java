package com.ants.plugin.tool;

import com.aliyun.oss.OSSClient;
import com.ants.common.bean.Log;
import com.ants.common.bean.Prop;
import com.ants.common.utils.GenUtil;
import com.ants.common.utils.StrUtil;
import com.ants.plugin.oss.AliOssTpl;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017-12-27
 */
public class AliOssTool extends ConcurrentToolMap {

    /**
     * 获取阿里云Oss实例
     *
     * @param url
     * @param accessKeyId
     * @param accessKeySecret
     * @return
     */
    public static AliOssTpl getAliOss(String url, String accessKeyId, String accessKeySecret) {
        url = Prop.getKeyStrValue(url);
        accessKeyId = Prop.getKeyStrValue(accessKeyId);
        accessKeySecret = Prop.getKeyStrValue(accessKeySecret);
        String endpoint, bucketName;
        try {
            endpoint = url.substring(url.indexOf(".") + 1, url.length());
            bucketName = url.substring(url.indexOf("//") + 2, url.indexOf("."));
            Log.debug("endpoint:{} , bucketName:{}", endpoint, bucketName);
        } catch (Exception e) {
            throw new RuntimeException(String.format("AliOss配置错误 -> %s", e.getMessage()));
        }
        String key = "oss_".concat(GenUtil.makeMd5Str(url, accessKeyId, accessKeySecret));
        if (PLUGINS.containsKey(key)) {
            return (AliOssTpl) PLUGINS.get(key);
        }
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        return new AliOssTpl(client, url, bucketName);
    }

    public static AliOssTpl getAliOss() {
        String url = Prop.getStr("ants.alioss.url");
        String accessKeyId = Prop.getStr("ants.alioss.access-key-id");
        String accessKeySecret = Prop.getStr("ants.alioss.access-key-secret");
        if (StrUtil.notBlank(url, accessKeyId, accessKeySecret)) {
            return getAliOss(url, accessKeyId, accessKeySecret);
        }
        throw new RuntimeException("没有在配置文件中找到AliOss默认配置");
    }
}
