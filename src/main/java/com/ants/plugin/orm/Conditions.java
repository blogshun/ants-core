package com.ants.plugin.orm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ants.common.utils.StrCaseUtil;
import com.ants.common.utils.StrUtil;
import com.ants.plugin.orm.enums.Condition;
import com.ants.plugin.orm.enums.OrderBy;
import com.ants.plugin.orm.enums.Relation;
import com.ants.plugin.orm.enums.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author MrShun
 * @version 1.0
 *          Date 2017-12-08
 */
public class Conditions {

    /**
     * 存放select label 标签
     */
    private String label = "*";

    /**
     * 存放排序字符串
     */
    private String orderBy;

    /**
     * 存放分组字符串
     */
    private String groupBy;

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
        relations.add(String.format(relation.getValue(), table, alias, cond1, cond2));
        return this;
    }

    public Conditions addRelation(Relation relation, String table, String cond1, String cond2) {
        relations.add(String.format(relation.getValue(), table, "", cond1, cond2));
        return this;
    }

    public List<String> getRelations() {
        return relations;
    }

    public Conditions label(String... labels) {
        if (labels.length == 1) {
            this.label = " ".concat(labels[0]);
        } else {
            StringBuffer sb = new StringBuffer(" ");
            for (String label : labels) {
                String asStr;
                if (label.indexOf(".") != -1) {
                    asStr = label.substring(label.indexOf(".") + 1, label.length());
                } else {
                    asStr = label;
                }
                sb.append(label).append(" as ").append(StrCaseUtil.toCapital(asStr, false)).append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
            this.label = sb.toString();
        }
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

    public Conditions orderBy(OrderBy orderBy, String... fields) {
        if (fields.length == 1) {
            this.orderBy = String.format(orderBy.getValue(), fields[0]);
        } else {
            StringBuffer sb = new StringBuffer();
            for (String field : fields) {
                sb.append(field).append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
            this.orderBy = String.format(orderBy.getValue(), sb.toString());
        }
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public Conditions groupBy(String field) {
        this.groupBy = field;
        return this;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public Conditions and(String field, Condition condition, Object... value) {
        conditions.add(new Cond(Symbol.AND, field, condition, value));
        return this;
    }

    public Conditions or(String field, Condition condition, Object... value) {
        conditions.add(new Cond(Symbol.OR, field, condition, value));
        return this;
    }

    public Conditions clear() {
        conditions.clear();
        relations.clear();
        this.label = "*";
        this.orderBy = null;
        this.limit = null;
        return this;
    }

    public Conditions filters(String filters) {
        JSONObject jsonObject = JSON.parseObject(filters);
        Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            if (StrUtil.notBlank(String.valueOf(value))) {
                and(entry.getKey(), Condition.EQ, value);
            }
        }
        return this;
    }
}
