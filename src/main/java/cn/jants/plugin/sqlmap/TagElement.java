package cn.jants.plugin.sqlmap;

import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2018-01-17
 */
public class TagElement {

    private String type;

    private List<SqlNode> sqlNodeList;

    public TagElement(String type, List<SqlNode> sqlNodeList) {
        this.type = type;
        this.sqlNodeList = sqlNodeList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SqlNode> getSqlNodeList() {
        return sqlNodeList;
    }

    public void setSqlNodeList(List<SqlNode> sqlNodeList) {
        this.sqlNodeList = sqlNodeList;
    }
}
