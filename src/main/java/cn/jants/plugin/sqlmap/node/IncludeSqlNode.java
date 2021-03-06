package cn.jants.plugin.sqlmap.node;


/**
 * @author MrShun
 * @version 1.0
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
