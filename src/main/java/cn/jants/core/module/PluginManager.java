package cn.jants.core.module;

import cn.jants.core.ext.Plugin;
import cn.jants.plugin.db.CommonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件管理
 *
 * @author MrShun
 * @version 1.0
 */
final public class PluginManager {

    private final static List<Plugin> PLUGINS = new ArrayList<>();

    public PluginManager add(Plugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin can not be null");
        }
        PLUGINS.add(plugin);
        return this;
    }

    public static List<Plugin> getPluginList() {
        return PLUGINS;
    }

    /**
     * 在插件对象列表中查找类型一样的对象
     *
     * @param clsType 对象类型
     * @param <T>
     * @return
     */
    public static <T> T findPluginObject(Class<T> clsType) {
        for (Object obj : PLUGINS) {
            if (obj.getClass() == clsType) {
                return (T) obj;
            }
        }
        return null;
    }

    /**
     * 在插件对象列表中, 查找数据源类型的对象插件
     *
     * @param clsType 对象类型
     * @param <T>
     * @return
     */
    public static <T> T findSourcePluginObject(Class<T> clsType, String name) {
        for (Object obj : PLUGINS) {
            if (obj.getClass() == clsType) {
                CommonProperty ct = (CommonProperty) obj;
                if (name.equals(ct.getName())) {
                    return (T) obj;
                }
            }
        }
        return null;
    }

    /**
     * 查找默认第一个
     * @param clsType
     * @param <T>
     * @return
     */
    public static <T> T findFirstSourcePluginObject(Class<T> clsType) {
        return findSourcePluginObject(clsType, "");
    }

    public static <T> T findRandomSourcePluginObject(Class<T> clsType) {
        for (Object obj : PLUGINS) {
            if (obj.getClass() == clsType) {
                CommonProperty ct = (CommonProperty) obj;
                return (T) obj;
            }
        }
        return null;
    }
}
