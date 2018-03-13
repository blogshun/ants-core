package cn.jants.core.context;

import cn.jants.common.enums.StartMode;
import cn.jants.common.annotation.boot.ViewConfiguration;

/**
 * 全局加载后的变量定义
 *
 * @author MrShun
 * @version 1.0
 */
public class AppConstant {

    /**
     * 默认是jar模式启动
     */
    public static StartMode START_MODE = StartMode.JAR;

    /**
     * 加载类
     */
    public static Class LOAD_CLASS = null;


    /**
     * 全局调式配置,默认false
     */
    public static Boolean DEBUG = true;

    /**
     * 默认编码集
     */
    public static String DEFAULT_ENCODING = "UTF-8";

    /**
     * 默认URL后缀名
     */
    public static String URL_REGEX_SUFFIX = "";

    /**
     * 默认配置文件
     */
    public static String DEFAULT_CONFIG = "application.properties";


    /**
     * 全局默认模板配置
     */
    public static ViewConfiguration TPL_CONFIG;

    /**
     * 全局随机密钥
     */
    public static String SECRET_KEY;

    /**
     * 是否驼峰
     */
    public static boolean HUMP = false;

    /**
     * 跨域配置
     */
    public static String DOMAIN;


    /**
     * 过滤包路径 例如 .api .web
     */
    public static String[] FILTER_PACKAGES = null;
}
