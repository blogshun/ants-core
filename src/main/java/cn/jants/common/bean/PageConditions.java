package cn.jants.common.bean;


import cn.jants.plugin.orm.enums.OrderBy;

/**
 * 分页信息实体
 *
 * @author MrShun
 * @version 1.0
 */
public class PageConditions<T> {

    /**
     * 当前第几页
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 排序字段
     */
    private String orderField;

    /**
     * 排序方式
     */
    private OrderBy sortType;

    /**
     * 条件
     */
    private JsonMap conditions;

    /**
     * 参数
     */
    private Object[] params;


    public PageConditions(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public PageConditions(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public OrderBy getSortType() {
        return sortType;
    }

    public void setSortType(OrderBy sortType) {
        this.sortType = sortType;
    }

    public JsonMap getConditions() {
        return conditions == null ? JsonMap.newJsonMap(): conditions;
    }

    public void setConditions(JsonMap conditions) {
        this.conditions = conditions;
    }

    public void put(String key, Object value){
        if(this.conditions == null){
            this.conditions = JsonMap.newJsonMap();
        }
        this.conditions.set(key, value);
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object... params) {
        this.params = params;
    }
}
