package com.ants.plugin.sqlmap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-05-31
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
        }
        Boolean check = (Boolean) OgnlCache.getValue(test, obj);
        if (check) {
            return context;
        }
        return "";
    }

}
