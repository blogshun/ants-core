package com.ants.common.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 */
public class ImageUtil {

    private static final List<String> allowType = Arrays.asList("image/bmp", "image/png", "image/gif", "image/jpg", "image/jpeg");

    /**
     * 校验文件是否为图片
     *
     * @param contentType 请求类型
     * @return
     */
    public static boolean check(String contentType) {
        return allowType.contains(contentType);
    }
}
