package cn.jants.plugin.sqlmap;

/**
 * Ognl只能调用public static静态方法或属性 例如下
 *
 * @author MrShun
 * @version 1.0
 */
public class OgnlExpression {

    public static String NAME = "Mr liu";

    private static boolean TestMethod(String name) {
        return true;
    }
}
