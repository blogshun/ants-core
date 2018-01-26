package cn.jants.plugin.sqlmap;

import cn.jants.plugin.sqlmap.node.SqlNode;

import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 */
public class TagElement {

    /**
     * 操作类型
     */
    private String optionType;

    /**
     * 节点信息
     */
    private List<SqlNode> sqlNodeList;

    /**
     * 返回类型
     */
    private String resultType;


    public TagElement(String optionType, String resultType, List<SqlNode> sqlNodeList) {
        this.optionType = optionType;
        this.resultType = resultType;
        this.sqlNodeList = sqlNodeList;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public List<SqlNode> getSqlNodeList() {
        return sqlNodeList;
    }

    public void setSqlNodeList(List<SqlNode> sqlNodeList) {
        this.sqlNodeList = sqlNodeList;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}
