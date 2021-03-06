package cn.jants.common.enums;

/**
 * 模板加载类型
 *
 * @author MrShun
 * @version 1.0
 */
public enum LoadType {
    /**
     * 从resource资源加载
     */
    Resource,

    /**
     * 从外部路径加载，nginx静态化会用到
     */
    FilePath
}
