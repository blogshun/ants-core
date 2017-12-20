/**
 * set标签里面包含if
 */
package com.ants.plugin.sqlmap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class SetSqlNode implements SqlNode {

    private List<IfSqlNode> ifSqlNodeList = new ArrayList<>();

    public SetSqlNode(Node node) {
        NodeList childNodes = ((Element) node).getElementsByTagName("if");
        if (childNodes.getLength() == 0) {
            throw new RuntimeException("configuring the set tag must include configuring the if tag!");
        }
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            ifSqlNodeList.add(new IfSqlNode(item));
        }
    }

    @Override
    public String getResult(Object obj) {
        String sbx = "";
        for (IfSqlNode ifSqlNode : ifSqlNodeList) {
            String result = ifSqlNode.getResult(obj);
            if (!result.isEmpty()) {
                if (result.toCharArray()[result.length() - 1] == ',') {
                    sbx = sbx + result;
                } else {
                    sbx = sbx + result + ",";
                }
            }
        }
        if (!sbx.isEmpty()) {
            return " set " + sbx.substring(0, sbx.length() - 1);
        }
        return sbx;
    }
}
