package com.ants.plugin.orm;

import com.ants.plugin.orm.enums.Condition;
import com.ants.plugin.orm.enums.OrderBy;
import com.ants.plugin.orm.enums.Relation;
import com.ants.plugin.orm.enums.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-08
 */
public class Conditions {

    /**
     * 存放select label 标签
     */
    private String label = " _.*";

    /**
     * 存放排序字符串
     */
    private String orderBy;

    /**
     * 存放分页字符串
     */
    private String limit;

    /**
     * 常用链接关联查询
     */
    private List<String> relations = new ArrayList<>();

    /**
     * 条件对象
     */
    private List<Cond> conditions = new ArrayList<>();

    public Conditions limit(Integer start, Integer size) {
        this.limit = String.format(" limit %s,%s", start, size);
        return this;
    }

    public String getLimit() {
        return limit;
    }

    public Conditions addRelation(Relation relation, String table, String alias, String cond1, String cond2) {
        relations.add(String.format(relation.getValue(), table, alias, cond1.indexOf("_.") != -1 ? cond1 : "_.".concat(cond1), cond2));
        return this;
    }

    public List<String> getRelations() {
        return relations;
    }

    public Conditions label(String label) {
        this.label = label;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public List<Cond> getConditions() {
        return conditions;
    }

    public void setConditions(List<Cond> conditions) {
        this.conditions = conditions;
    }

    public Conditions orderBy(String field, OrderBy orderBy) {
        this.orderBy = String.format(orderBy.getValue(), field);
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public Conditions and(String field, Condition condition, Object... value) {
        conditions.add(new Cond(Symbol.AND, field, condition, value));
        return this;
    }

    public Conditions or(String field, Condition condition, Object... value) {
        conditions.add(new Cond(Symbol.OR, field, condition, value));
        return this;
    }
}
