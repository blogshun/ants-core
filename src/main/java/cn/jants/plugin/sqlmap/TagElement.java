package cn.jants.plugin.sqlmap;

import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2018-01-17
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
    private String returnType;


    public TagElement(String optionType, List<SqlNode> sqlNodeList) {
        this.optionType = optionType;
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

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
}
