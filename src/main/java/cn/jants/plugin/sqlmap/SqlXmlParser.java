package cn.jants.plugin.sqlmap;

import cn.jants.core.utils.ParamTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(SqlXmlParser.class);


    protected static Map<String, List<SqlNode>> sqlMap = new ConcurrentHashMap<>();

    private static final String STATIC_START_SYMBOL = "#{";

    private static final String STATIC_END_SYMBOL = "}";


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
            NodeList nodeList = documentElement.getElementsByTagName("sql");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                String key = rootName + "." + ((Element) item).getAttribute("id");
                NodeList childs = item.getChildNodes();
                List<SqlNode> nodes = new ArrayList<>();
                for (int j = 0; j < childs.getLength(); j++) {
                    Node it = childs.item(j);
                    if (it.getNodeType() == Node.TEXT_NODE) {
                        nodes.add(new TextSqlNode(it.getTextContent()));
                    } else {
                        String nodeName = it.getNodeName();
                        if (nodeName == Tag.INCLUDE) {
                            String refid = ((Element) it).getAttribute("refid");
                            String includKey = rootName + "." + refid;
                            List<SqlNode> sqlNodes = sqlMap.get(includKey);
                            if (sqlNodes == null || sqlNodes.size() == 0) {
                                throw new RuntimeException(includKey + " the includ reference node must exist or can only be configured in the front!");
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
                sqlMap.put(key, nodes);

            }

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
        List<SqlNode> sqlNodes = sqlMap.get(key);
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
        if(ParamTypeUtil.isBaseDataType(params.getClass())) {
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

}
