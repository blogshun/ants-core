package cn.jants.plugin.sqlmap.node;

import cn.jants.plugin.sqlmap.OgnlCache;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author MrShun
 * @version 1.0
 */
public class IfSqlNode implements SqlNode {

    private String test;

    private String context;

    public IfSqlNode(Node node) {
        this.test = ((Element) node).getAttribute("test");
        this.context = node.getTextContent().trim();
    }

    @Override
    public String getResult(Object obj) {
        if(obj == null) {
            return "";
        } else if(!"1".equals(this.test) && !"true".equals(this.test)) {
            Boolean check = (Boolean)OgnlCache.getValue(this.test, obj);
            return check.booleanValue()?this.context:"";
        } else {
            return this.context;
        }
    }

}
