package cn.jants.common.utils;


import java.util.*;

/**
 * @author MrShun
 * @version 1.0
 *          Date 2017-04-27
 */
public class CollectionUtil {

    enum Type {
        /**
         * 排序
         */
        ASC, DESC;
    }

    /**
     * 根据HashCode进行排序
     *
     * @param list 集合对象
     * @param type 排序方式
     */
    public static void hashCodeSort(List<?> list, final Type type) {
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (type.equals(Type.ASC)) {
                    return o1.hashCode() - o2.hashCode();
                } else {
                    return o2.hashCode() - o1.hashCode();
                }
            }
        });
    }

    public static void hashCodeSort(List<?> list) {
        hashCodeSort(list, Type.ASC);
    }


    /**
     * 求两个字符串数组的并集，利用set的元素唯一性
     *
     * @param cls1 对象class1
     * @param cls2 对象class2
     * @return class数组
     */
    public static Class[] union(Class[] cls1, Class[] cls2) {
        Set<Class> set = new HashSet<>();
        if (cls1 != null) {
            for (Class t : cls1) {
                set.add(t);
            }
        }
        if (cls2 != null) {
            for (Class t : cls2) {
                set.add(t);
            }
        }
        ArrayList<Class> ts = new ArrayList<>(set);
        Class[] result = new Class[ts.size()];
        return ts.toArray(result);
    }

    /**
     * 求两个数组的差集
     *
     * @param cls1 对象class1
     * @param cls2 对象class2
     * @return class数组
     */
    public static Class[] minus(Class[] cls1, Class[] cls2) {
        List<Class> ts = new ArrayList();
        for (Class cls : cls1) {
            ts.add(cls);
        }
        if (cls2 != null) {
            List<Class> list = Arrays.asList(cls2);
            for (Class c : list) {
                ts.remove(c);
            }
        }
        Class[] result = new Class[ts.size()];
        return ts.toArray(result);
    }
}
