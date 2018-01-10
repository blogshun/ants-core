package cn.jants.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * @author MrShun
 * @version 1.0
 */
public class PathUtil {

    /**
     * 获取当资源路径
     *
     * @return
     */
    public static String getClassPath() {
        try {
            String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            return URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断运行模式 true/jar模式 false/解压模式
     */
    public static boolean isJarMode() {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("");
        return resource == null ? true : false;
    }

    /**
     * 获取jar所在位置
     *
     * @return
     */
    public static String getJarPath(Class cls) {
        URL url = cls.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            // 转化为utf-8编码
            filePath = URLDecoder.decode(url.getPath(), "utf-8");
            System.out.println("@@@@@@@Jar PATH:"+filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }

    /**
     * 获取项目路径
     *
     * @return
     */
    public static String projectPath() {
        try {
            return new File("").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 根据Class获取路径
     *
     * @param cls
     * @return
     */
    public static String getClassPath(Class cls) {
        try {
            String path = cls.getResource("/").getPath();
            return URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}


