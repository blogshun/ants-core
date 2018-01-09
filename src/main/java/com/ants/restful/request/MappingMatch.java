package com.ants.restful.request;


import com.ants.restful.matcher.AntPathMatcher;
import com.ants.restful.matcher.PathMatcher;

import java.util.List;
import java.util.Map;

/**
 * 请求URL匹配获取RequestMappingBean对象
 *
 * @author MrShun
 * @version 1.0
 * @date 2017-04-27
 */
public class MappingMatch {

    private final static PathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 根据target url匹配出最适合RequestMapping对象
     * 如果相等直接放回
     * 如果不相等则放回字符串长的匹配
     *
     * @param mappingBeangList RequestMappingBean集合
     * @param target           匹配的URL
     * @return
     */
    public static RequestMappingBean match(List<RequestMappingBean> mappingBeangList, String target) {
        if (target.lastIndexOf("/") == target.length() - 1) {
            target = target + "index";
        }
        int k = -1, temp = 0;
        for (int i = 0; i < mappingBeangList.size(); i++) {
            RequestMappingBean mapping = mappingBeangList.get(i);
            String[] urls = mapping.getUrls();
            for (String url : urls) {
                boolean result = PATH_MATCHER.match(url, target);
                if (result) {
                    if (url.equals(target)) {
                        mapping.setCurrentUrl(url);
                        return mappingBeangList.get(i);
                    } else {
                        int num = url.length();
                        if (num > temp) {
                            temp = num;
                            k = i;
                            mapping.setCurrentUrl(url);
                        }
                    }

                }
            }
        }
        if (k == -1) {
            return null;
        }
        return mappingBeangList.get(k);
    }


    /**
     * 获取url里面值并且保持为map
     *
     * @param mapping url配置
     * @param uri     请求url
     * @return
     */
    public static Map<String, String> getURIValues(String mapping, String uri) {
        int num = mapping.indexOf("{");
        if (num == -1) {
            return null;
        } else {
            mapping = mapping.substring(num, mapping.length());
            String target = uri.substring(num, uri.length());
            return PATH_MATCHER.extractUriTemplateVariables(mapping, target);
        }
    }

}
