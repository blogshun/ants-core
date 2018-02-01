package cn.jants.plugin.i18n;

import cn.jants.core.utils.PropertyUtil;

/**
 * @author MrShun
 * @version 1.0
 */
public class I18n {

    /**
     * 获取i18n数据值
     *
     * @param key    键
     * @param values 条件值
     * @return
     */
    public static String get(String key, Object... values) {
        String label = PropertyUtil.getStr(key);
        return String.format(label, values);
    }
}
