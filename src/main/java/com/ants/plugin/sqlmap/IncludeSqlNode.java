package com.ants.plugin.sqlmap;


/**
 * @author MrShun
 * @version 1.0
 * Date 2017-05-31
 */
public class IncludeSqlNode implements SqlNode {

    private String context;

    /**
     * @param sqlNode 引用节点
     */
    public IncludeSqlNode(SqlNode sqlNode) {
        this.context = sqlNode.getResult(null);
    }

    @Override
    public String getResult(Object obj) {
        return context;
    }
}
