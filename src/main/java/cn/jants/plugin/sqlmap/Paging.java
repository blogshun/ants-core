package cn.jants.plugin.sqlmap;

import cn.jants.common.bean.PageConditions;
import cn.jants.plugin.orm.enums.OrderBy;

/**
 * @author MrShun
 * @version 1.0
 */
public class Paging {

    //分页线程对象
    private final static ThreadLocal<PageConditions> TL = new ThreadLocal<>();

    /**
     * 开始分页
     *
     * @param pageNum  当前页数
     * @param pageSize 每页大小
     */
    public static void startPage(Integer pageNum, Integer pageSize) {
        PageConditions pageConditions = TL.get();
        if (pageConditions == null) {
            pageConditions = new PageConditions(pageNum, pageSize);
        }
        TL.set(pageConditions);
    }

    /**
     * 设置分页参数
     *
     * @param orderFiled
     * @param orderBy
     */
    public static void orderBy(String orderFiled, OrderBy orderBy) {
        PageConditions pageConditions = TL.get();
        if (pageConditions != null) {
            pageConditions.setOrderField(orderFiled);
            pageConditions.setSortType(orderBy);
            TL.set(pageConditions);
        }

    }


    /**
     * 获取分页对象
     *
     * @return
     */
    public static PageConditions getPageConditions() {
        return TL.get();
    }
}
