package cn.jants.plugin.sqlmap;

import cn.jants.core.utils.ParamTypeUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class SqlXmlParser {

    protected static Map<String, TagElement> sqlMap = new ConcurrentHashMap<>();

    private static final String STATIC_START_SYMBOL = "#{";

    private static final String STATIC_END_SYMBOL = "}";

    private static final String[] OPTIONS = new String[]{"sql", "select", "insert", "update", "delete"};


    /**
     * 将dom文件sql解析出来以键值对形式放入链表
     *
     * @param document
     */
    public static void parse(Document document) {
        Element documentElement = document.getDocumentElement();
        if ("mapper".equals(documentElement.getTagName())) {
            String rootName = documentElement.getAttribute("namespace");
            if (rootName.isEmpty()) {
                throw new RuntimeException("the namespace domain must be defined!");
            }
            for (String option : OPTIONS) {
                NodeList nodeList = documentElement.getElementsByTagName(option);
                addTagElement(option, rootName, nodeList);
            }
        }
        System.out.println(sqlMap.size());
    }

    private static void addTagElement(String type, String rootName, NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            String key = rootName + "." + ((Element) item).getAttribute("id");
            String returnType = ((Element) item).getAttribute("returnType");
            NodeList childNodes = item.getChildNodes();
            List<SqlNode> nodes = new ArrayList<>();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node it = childNodes.item(j);
                if (it.getNodeType() == Node.TEXT_NODE) {
                    nodes.add(new TextSqlNode(it.getTextContent()));
                } else {
                    String nodeName = it.getNodeName();
                    if (nodeName == Tag.INCLUDE) {
                        String refid = ((Element) it).getAttribute("refid");
                        String includeKey = rootName + "." + refid;
                        TagElement tagElement = sqlMap.get(includeKey);
                        if (tagElement == null) {
                            throw new RuntimeException(String.format("not found %s !", includeKey));
                        }
                        List<SqlNode> sqlNodes = tagElement.getSqlNodeList();
                        if (sqlNodes == null || sqlNodes.size() == 0) {
                            throw new RuntimeException(includeKey + " the includ reference node must exist or can only be configured in the front!");
                        }
                        nodes.add(new IncludeSqlNode(sqlNodes.get(0)));
                    } else if (nodeName == Tag.IF) {
                        nodes.add(new IfSqlNode(it));
                    } else if (nodeName == Tag.WHERE) {
                        nodes.add(new WhereSqlNode(it));
                    } else if (nodeName == Tag.SET) {
                        nodes.add(new SetSqlNode(it));
                    } else if (nodeName == Tag.TRIM) {
                        nodes.add(new TrimSqlNode(it));
                    } else if (nodeName == Tag.CHOOSE) {
                        nodes.add(new ChooseSqlNode(it));
                    } else if (nodeName == Tag.FOREACH) {
                        nodes.add(new ForEachSqlNode(it));
                    }
                }
            }
            sqlMap.put(key, new TagElement(type, returnType, nodes));

        }
    }

    /**
     * 根据命名空间和参数对象获取sql语句对象
     *
     * @param key    命名空间
     * @param params 参数对象
     * @return
     */
    public static SqlParams getPreparedStatement(String key, Object params) {
        TagElement tagElement = sqlMap.get(key);
        List<SqlNode> sqlNodes = tagElement.getSqlNodeList();
        if (sqlNodes == null || sqlNodes.size() == 0) {
            throw new IllegalArgumentException("not find " + key + "!");
        }
        StringBuffer sb = new StringBuffer();
        for (SqlNode sqlNode : sqlNodes) {
            sb.append(sqlNode.getResult(params));
        }
        String sql = sb.toString().replaceAll("\r|\n|\t|", "").replaceAll(" +", " ").trim();
        if (params == null) {
            return new SqlParams(sql, null);
        }
        List<Object> values = new ArrayList<>();
        //基本数据类型
        if (ParamTypeUtil.isBaseDataType(params.getClass())) {
            System.out.println(sql);
            int startNum = sql.indexOf(STATIC_START_SYMBOL);
            sql = sql.substring(0, startNum).concat("?");
            values.add(params);
        }
        //Map数据类型
        else if (params instanceof Map) {
            Set<Map.Entry<String, Object>> entries = ((Map<String, Object>) params).entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                String ikey = STATIC_START_SYMBOL + entry.getKey() + STATIC_END_SYMBOL;
                Object v = entry.getValue();
                if (sql.indexOf(ikey) != -1) {
                    values.add(v);
                    sql = sql.replace(ikey, "? ");
                }
            }
        } else {
            throw new IllegalArgumentException("[" + params + "] 传入的数据对象必须Map类型 或基本数据类型!");
        }
        return new SqlParams(sql, values.toArray());
    }

    public static SqlParams getPreparedStatement(String key) {
        return getPreparedStatement(key, null);
    }

    public static void clear() {
        sqlMap.clear();
    }

    public static TagElement getOptionType(String key) {
        TagElement tagElement = sqlMap.get(key);
        List<SqlNode> sqlNodes = tagElement.getSqlNodeList();
        if (sqlNodes == null || sqlNodes.size() == 0) {
            throw new IllegalArgumentException("not find " + key + "!");
        }
        return tagElement;
    }

}
