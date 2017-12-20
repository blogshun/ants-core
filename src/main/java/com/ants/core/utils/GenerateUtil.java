package com.ants.core.utils;

import com.ants.common.utils.StrUtil;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-02
 */
public class GenerateUtil {

    /**
     * 生成service key
     *
     * @param serName service名称
     * @return
     */
    public static String createServiceKey(String serName) {
        if (StrUtil.count(".", serName) > 1) {
            return StrUtil.lastSubstring(serName, '.', 3).replace(".", "_");
        }
        return serName.replace(".", "_");
    }
}
