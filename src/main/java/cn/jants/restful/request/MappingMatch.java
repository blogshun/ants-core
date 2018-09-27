package cn.jants.restful.request;


import cn.jants.restful.matcher.AntPathMatcher;
import cn.jants.restful.matcher.PathMatcher;

import java.util.List;
import java.util.Map;

/**
 * 请求URL匹配获取RequestMappingBean对象
 *
 * @author MrShun
 * @version 1.0
 */
public class MappingMatch {

    private final static PathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 根据target url匹配出最适合RequestMapping对象
     * 如果相等直接放回
     * 如果不相等则放回字符串长的匹配
     *
     * @param mappingBeanList RequestMappingBean集合
     * @param target           匹配的URL
     * @return
     */
    public static RequestMappingBean match(List<RequestMappingBean> mappingBeanList, String target) {
        if (target.lastIndexOf("/") == target.length() - 1) {
            target = target + "index";
        }
        int k = -1, temp = 0;
        for (int i = 0; i < mappingBeanList.size(); i++) {
            RequestMappingBean mapping = mappingBeanList.get(i);
            String[] urls = mapping.getUrls();
            for (String url : urls) {
                boolean result = PATH_MATCHER.match(url, target);
                if (result) {
                    if (url.equals(target)) {
                        mapping.setCurrentUrl(url);
                        return mappingBeanList.get(i);
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
        return mappingBeanList.get(k);
    }

    /**
     * 链接匹配
     *
     * @param requests
     * @param target
     * @return
     */
    public static boolean matchRequests(List<String[]> requests, String target) {
        for (int i = 0; i < requests.size(); i++) {
            String[] urls = requests.get(i);
            for (String url : urls) {
                boolean result = PATH_MATCHER.match(url, target);
                if (result) {
                    return true;
                }
            }
        }
        return false;
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
