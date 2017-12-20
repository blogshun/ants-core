package com.ants.plugin.sqlmap;

/**
 * Ognl只能调用public static静态方法或属性 例如下
 *
 * @author MrShun
 * @version 1.0
 * @com.acxy.ants.core.plugin.sqlmap.OgnlExpression@TEST
 * @com.acxy.ants.core.plugin.sqlmap.OgnlExpression@TestMethod('Mr liu')
 * @Date 2017-06-01
 */
public class OgnlExpression {

    public static String NAME = "Mr liu";

    private static boolean TestMethod(String name) {
        return true;
    }
}
