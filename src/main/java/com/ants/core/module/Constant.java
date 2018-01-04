package com.ants.core.module;

import com.ants.common.enums.ViewType;
import com.ants.core.context.AppConstant;

/**
 * 系统全局常量配置类
 *
 * @author MrShun
 * @version 1.0
 * Date 2017-11-16
 */
final public class Constant {

    /**
     * 编码集
     */
    private String encoding = AppConstant.DEFAULT_ENCODING;

    /**
     * 设置404页面
     */
    private String error404Page;

    /**
     * 设置500页面
     */
    private String error500Page;

    /**
     * 设置url拦截后缀 例如.html,.json,.jsp
     */
    private String[] regexSuffix;

    /**
     * 设置静态目录不经过handler链
     */
    private String[] resources;

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getError404Page() {
        return error404Page;
    }

    public void setError404Page(String error404Page) {
        this.error404Page = error404Page;
    }

    public String getError500Page() {
        return error500Page;
    }

    public void setError500Page(String error500Page) {
        this.error500Page = error500Page;
    }

    public String getRegexSuffix() {
        if (regexSuffix != null && regexSuffix.length != 0) {
            StringBuffer sb = new StringBuffer("{:(");
            for (int i = 0; i < regexSuffix.length; i++) {
                sb.append(regexSuffix[i]);
                if (i != regexSuffix.length - 1) {
                    sb.append("|");
                }
            }
            sb.append(")}");
            return sb.toString();
        }
        return "";
    }

    public void setRegexSuffix(String... regexSuffix) {
        this.regexSuffix = regexSuffix;
    }

    public String[] getResources() {
        return resources;
    }

    public void setResources(String... resources) {
        this.resources = resources;
    }
}
