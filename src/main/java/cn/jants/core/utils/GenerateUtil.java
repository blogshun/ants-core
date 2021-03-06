package cn.jants.core.utils;

import cn.jants.common.utils.StrUtil;

/**
 * @author MrShun
 * @version 1.0
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
