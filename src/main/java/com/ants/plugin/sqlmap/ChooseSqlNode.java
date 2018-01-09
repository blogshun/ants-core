package com.ants.plugin.sqlmap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MrShun
 * @version 1.0
 */
public class ChooseSqlNode implements SqlNode {

    private List<IfSqlNode> ifSqlNodeList = new ArrayList<>();

    private String otherwiseText;

    public ChooseSqlNode(Node node) {
        Element el = (Element) node;
        NodeList childNodes = el.getElementsByTagName("when");
        if (childNodes.getLength() == 0) {
            throw new RuntimeException("configuring the choose tag must include configuring the when tag!");
        }
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            ifSqlNodeList.add(new IfSqlNode(item));
        }
        NodeList otherwiseTag = el.getElementsByTagName("otherwise");
        if (otherwiseTag.getLength() != 0) {
            this.otherwiseText = otherwiseTag.item(0).getTextContent();
        }
    }

    @Override
    public String getResult(Object obj) {
        String sbx = "";
        for (IfSqlNode ifSqlNode : ifSqlNodeList) {
            String result = ifSqlNode.getResult(obj);
            if (!result.isEmpty()) {
                sbx = result;
                break;
            }
        }
        if (sbx == "" && otherwiseText != null) {
            return otherwiseText;
        }
        return sbx;
    }
}
