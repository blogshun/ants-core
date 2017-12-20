package com.ants.core.context;

import com.ants.common.annotation.boot.ViewConfiguration;
import com.ants.common.enums.StartMode;

/**
 * 全局加载后的变量定义
 *
 * @author MrShun
 * @version 1.0
 * @Date 2017-11-19
 */
public class AppConstant {

    /**
     * 默认是jar模式启动
     */
    public static StartMode START_MODE = StartMode.JAR;


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

}
