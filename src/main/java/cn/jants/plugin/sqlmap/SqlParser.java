package cn.jants.plugin.sqlmap;

import cn.jants.core.utils.ParamTypeUtil;
import cn.jants.plugin.sqlmap.node.*;
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
public class SqlParser {

    protected static Map<String, TagElement> sqlMap = new ConcurrentHashMap<>();

    protected static Map<String, String> resultTypeMap = new ConcurrentHashMap<>();

    private static final String STATIC_START_SYMBOL = "#{";

    private static final String STATIC_END_SYMBOL = "}";

    private static final String[] OPTIONS = new String[]{"resultType", "sql", "select", "insert", "update", "delete"};


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
                if ("resultType".equals(option)) {
                    addResultType(rootName, nodeList);
                } else {
                    addTagElement(option, rootName, nodeList);
                }
            }
        }
    }

    private static void addResultType(String rootName, NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            String key = rootName + "." + ((Element) item).getAttribute("id");
            String type = ((Element) item).getAttribute("type");
            resultTypeMap.put(key, type);
        }
    }

    private static void addTagElement(String type, String rootName, NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            String key = rootName + "." + ((Element) item).getAttribute("id");
            String resultType = ((Element) item).getAttribute("resultType");
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
            sqlMap.put(key, new TagElement(type, resultType, nodes));

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

        //基本数据类型
        if (ParamTypeUtil.isBaseDataType(params.getClass())) {
            int startNum = sql.indexOf(STATIC_START_SYMBOL);
            if (startNum == -1) {
                return new SqlParams(sql, null);
            }
            sql = sql.substring(0, startNum).concat("?");
            return new SqlParams(sql, new Object[]{params});
        }
        //Map数据类型
        else if (params instanceof Map) {
            List<String> fieldList = getFieldList(sql);
            Map<String, Object> mapParams = (Map<String, Object>) params;
            List<Object> values = new ArrayList<>(mapParams.size());
            for(String filed: fieldList){
                if(mapParams.containsKey(filed)){
                    Object val = mapParams.get(filed);
                    sql = sql.replace(STATIC_START_SYMBOL.concat(filed).concat(STATIC_END_SYMBOL), "? ");
                    values.add(val);
                }

            }
            return new SqlParams(sql, values.toArray());
        } else {
            throw new IllegalArgumentException("[" + params + "] 传入的数据对象必须Map类型 或基本数据类型!");
        }

    }

    public static SqlParams getPreparedStatement(String key) {
        return getPreparedStatement(key, null);
    }

    public static String getResultType(String key) {
        return resultTypeMap.get(key);
    }

    public static void clear() {
        sqlMap.clear();
    }

    public static TagElement getOptionType(String key) {
        TagElement tagElement = sqlMap.get(key);
        if (tagElement == null) {
            throw new IllegalArgumentException("not found " + key + "!");
        }
        return tagElement;
    }

    /**
     * 获取字段
     *
     * @param sql sql语句
     * @return
     */
    private static List<String> getFieldList(String sql) {
        List<String> list = new ArrayList<>();
        while (sql.indexOf(STATIC_START_SYMBOL) != -1) {
            int sNum = sql.indexOf(STATIC_START_SYMBOL);
            int eNum = sql.indexOf(STATIC_END_SYMBOL, sNum);
            String str = sql.substring(sNum + 2, eNum);
            sql = sql.replace(STATIC_START_SYMBOL + str + STATIC_END_SYMBOL, "");
            list.add(str);
        }
        return list;
    }

}
