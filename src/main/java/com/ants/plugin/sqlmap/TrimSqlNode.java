package com.ants.plugin.sqlmap;


import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class TrimSqlNode implements SqlNode {

    //前缀
    private String prefix;

    //后缀
    private String suffix;

    //加入字符
    private String suffixOverrides;

    private List<IfSqlNode> ifSqlNodeList = new ArrayList<>();

    public TrimSqlNode(Node node) {
        Element el = (Element) node;
        this.prefix = el.getAttribute("prefix");
        this.suffix = el.getAttribute("suffix");
        this.suffixOverrides = el.getAttribute("suffixOverrides");
        NodeList childNodes = el.getElementsByTagName("if");
        if (childNodes.getLength() == 0) {
            throw new RuntimeException("configuring the trim tag must include configuring the if tag!");
        }
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            ifSqlNodeList.add(new IfSqlNode(item));
        }
    }

    @Override
    public String getResult(Object obj) {
        StringBuffer sb = new StringBuffer(prefix);
        String sbx = "";
        for (IfSqlNode ifSqlNode : ifSqlNodeList) {
            String result = ifSqlNode.getResult(obj);
            if (!result.isEmpty()) {
                if (result.indexOf(suffixOverrides) == -1) {
                    result = result + suffixOverrides;
                }
                sbx = sbx + result;
            }

        }
        if (!sbx.isEmpty()) {
            sbx = sbx.substring(0, sbx.length() - 1);
        }
        sb.append(sbx + suffix);
        return sb.toString();
    }
}
