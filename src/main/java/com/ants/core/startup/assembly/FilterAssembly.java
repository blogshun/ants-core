package com.ants.core.startup.assembly;

import javax.servlet.Filter;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017-11-16
 */
public class FilterAssembly {

    /**
     * 过滤器名称
     */
    private String filterName;

    /**
     * 过滤器对象
     */
    private Filter filter;

    /**
     * 匹配路径
     */
    private String urlPattern;

    public FilterAssembly() {
    }

    public FilterAssembly(String filterName, Filter filter, String urlPattern) {
        this.filterName = filterName;
        this.filter = filter;
        this.urlPattern = urlPattern;
    }


    public String getFilterName() {
        return filterName;
    }


    public Filter getFilter() {
        return filter;
    }


    public String getUrlPattern() {
        return urlPattern;
    }

}
