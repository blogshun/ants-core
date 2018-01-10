package cn.jants.plugin.sqlmap;

import ognl.*;

import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class OgnlCache {

    private static final Map<String, Node> EXPRESSION_CACHE = new ConcurrentHashMap<>();

    public static Object getValue(String expression, Object root) {
        try {
            return Ognl.getValue(parseExpression(expression), root);
        } catch (OgnlException e) {
            throw new IllegalArgumentException("Error evaluating expression '" + expression + "'. Cause: "+e.getMessage());
        }
    }

    private static Object parseExpression(String expression) throws OgnlException {
        try {
            Node node = EXPRESSION_CACHE.get(expression);
            if (node == null) {
                node = new OgnlParser(new StringReader(expression)).topLevelExpression();
                EXPRESSION_CACHE.put(expression, node);
            }
            return node;
        } catch (ParseException e) {
            throw new ExpressionSyntaxException(expression, e);
        } catch (TokenMgrError e) {
            throw new ExpressionSyntaxException(expression, e);
        }
    }

}