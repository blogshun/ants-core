package cn.jants.core.utils;


import cn.jants.common.bean.Log;
import cn.jants.common.enums.StartMode;
import cn.jants.common.utils.PathUtil;
import cn.jants.core.context.AppConstant;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 扫描注解工具类
 * 获取注解信息
 *
 * @author MrShun
 * @version 1.0
 */
public class ScanUtil {

    /**
     * 所有扫描到的class
     */
    private static List<Class> CLASSES = null;

    public static List<Class<?>> findScanClass(String[] packages, Class<? extends Annotation> annotationCls) {
        List<Class<?>> result = new ArrayList<>();
        if (CLASSES == null) {
            CLASSES = new ArrayList<>(100);
        }
        //jar模式
        if (PathUtil.isJarMode()) {
            AppConstant.START_MODE = StartMode.JAR;
            try {
                JarFile jarFile = new JarFile(PathUtil.getJarPath(AppConstant.LOAD_CLASS));
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String strCls = entry.getName();
                    if (strCls.endsWith(".class")) {
                        String clsName = strCls.substring(0, strCls.lastIndexOf(".")).replace("/", ".");
                        if (check(clsName, packages)) {
                            Class<?> cls = Class.forName(clsName);
                            CLASSES.add(cls);
                            Annotation anno = cls.getAnnotation(annotationCls);
                            if (anno != null) {
                                result.add(cls);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        //war解压模式
        else {
            AppConstant.START_MODE = StartMode.WAR;
            String classPath = PathUtil.getClassPath();
            if (CLASSES.size() == 0) {
                loadPathClass(classPath, packages);
            }
            for (Class cls : CLASSES) {
                if (cls.getAnnotation(annotationCls) != null) {
                    result.add(cls);
                }
            }
        }

        Log.info("mode: {}", AppConstant.START_MODE);
        return result;
    }

    /**
     * 迭代目录下面class入到集合中
     *
     * @param filePath 迭代的文件目录
     * @param packages 需要扫描的包名称
     */
    private static void loadPathClass(String filePath, String[] packages) {
        File dir = new File(filePath);
        // 该文件目录下文件全部放入数组
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String path = file.getPath();
                if (!file.isDirectory() && path.endsWith(".class")) {
                    String className = path.split("classes\\" + File.separator)[1].replace(File.separator, ".");
                    if (check(className, packages)) {
                        Class<?> aClass;
                        try {
                            aClass = Class.forName(className.substring(0, className.lastIndexOf(".")));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            throw new RuntimeException("包扫描, class.forname 加载出现异常!");
                        }
                        CLASSES.add(aClass);
                    }
                } else {
                    loadPathClass(path, packages);
                }
            }

        }
    }

    /**
     * 检查类是否在扫描的包下
     *
     * @param clsName  类包名称
     * @param packages 扫描包名称
     * @return
     */
    private static boolean check(String clsName, String[] packages) {
        for (String pg : packages) {
            if (clsName.startsWith(pg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 清空List集合
     */
    public static void clear() {
        CLASSES.clear();
    }
}
