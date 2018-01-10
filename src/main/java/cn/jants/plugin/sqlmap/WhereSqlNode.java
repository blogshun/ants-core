/**
 * 里面要么包含if
 */
package cn.jants.plugin.sqlmap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class WhereSqlNode implements SqlNode {

    private List<IfSqlNode> ifSqlNodeList = new ArrayList<>();

    private String textString = null;

    public WhereSqlNode(Node node) {
        Node lastChild = node.getLastChild();
        if(lastChild.getNodeType() == Node.TEXT_NODE && !"".equals(lastChild.getTextContent().replaceAll("\r|\n|\t|", "").replaceAll(" +", " ").trim())) {
            textString = lastChild.getTextContent();
        }
        NodeList childNodes = ((Element) node).getElementsByTagName("if");
        if (childNodes.getLength() == 0) {
            throw new RuntimeException("configuring the where tag must include configuring the if tag!");
        }
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            ifSqlNodeList.add(new IfSqlNode(item));
        }
    }

    @Override
    public String getResult(Object obj) {
        StringBuffer sb = new StringBuffer(" where ");
        int i = 0;
        for (IfSqlNode ifSqlNode : ifSqlNodeList) {
            String result = ifSqlNode.getResult(obj);
            if (!result.isEmpty()) {
                if(i==0) {
                    sb.append(result.replace("and ", ""));
                } else {
                    sb.append(result);
                }
                i++;
            }
        }
        if (i == 0 && textString==null) {
            return "";
        } else if(i == 0 && textString != null) {
            sb.append(textString);
        } else if(i > 0 && textString != null) {
            sb.append("and " + textString);
        }
        return sb.toString();
    }
}
