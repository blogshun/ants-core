//package com.ants.plugin.orm;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 实体解析成SQL
// *
// * @author MrShun
// * @version 1.0
// * @Date 2017-09-07
// */
//public class SqlParamsUtil {
//
//
//    public static <T> SqlParams getSql(Symbol symbol, T obj) {
//        SqlParams result = new SqlParams();
//        result.setSymbol(symbol);
//        Class<?> cls = obj.getClass();
//        Table table = cls.getAnnotation(Table.class);
//        if (table == null) throw new RuntimeException("映射实体没有指定表名称!");
//        result.setUnique(table.name());
//        Field[] fields = cls.getDeclaredFields();
//        StringBuffer sql = new StringBuffer();
//        //执行查询操作
//        if (symbol == Symbol.SELECT) {
//            String labelStr = result.getLabelStr();
//            sql.append("select " + labelStr);
//            setConditionsAndValues(fields, obj, result);
//            if ("".equals(labelStr)) sql.append("*");
//            sql.append(" from " + table.name()+" _");
//            if (!"".equals(result.getLeftJoinStr())) sql.append(" " + result.getLeftJoinStr());
//            String whereStr = result.getWhereStr();
//            if ("".equals(whereStr)) whereStr = result.getConditions();
//            else {
//                List<Object> values = result.getValues();
//                if (values != null) result.setParams(values.toArray());
//            }
//            result.setSql(sql + whereStr + result.getOrderByStr() + result.getLimitStr());
//        }
//        //执行增加操作 insert into table() values()
//        else if (symbol == Symbol.INSERT) {
//            sql.append("insert into " + table.name() + "(");
//            StringBuffer sb = new StringBuffer(" values(");
//            if (fields != null && fields.length != 0) {
//                List<Object> objects = new ArrayList<>();
//                for (Field field : fields) {
//                    Column column = field.getAnnotation(Column.class);
//                    if (column != null) {
//                        try {
//                            field.setAccessible(true);
//                            Object o = field.get(obj);
//                            if (o != null && field.getAnnotation(Id.class) == null) {
//                                sql.append(column.name() + ",");
//                                objects.add(o);
//                                sb.append("?,");
//                            }
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                result.setParams(objects.toArray());
//            }
//            if (!"".equals(result.getConditions())
//                    || !"".equals(result.getOrderByStr())
//                    || !"".equals(result.getLimitStr())
//                    || !"".equals(result.getLabelStr())
//                    || !"".equals(result.getLeftJoinStr())
//                    || !"".equals(result.getWhereStr()))
//                throw new RuntimeException("insert 操作不能带有 and()  or()  search()  orderBy()  limit() label() leftJoin() where() 等方法!");
//            sb.delete(sb.length() - 1, sb.length());
//            sb.append(")");
//            //剔除最后一个','字符
//            sql.delete(sql.length() - 1, sql.length());
//            sql.append(")" + sb);
//            result.setSql(sql.toString());
//        }
//        //执行删除操作 delete from table
//        else if (symbol == Symbol.DELETE) {
//            sql.append("delete from " + table.name());
//            setConditionsAndValues(fields, obj, result);
//            if (!"".equals(result.getOrderByStr())
//                    || !"".equals(result.getLimitStr())
//                    || !"".equals(result.getLabelStr())
//                    || !"".equals(result.getLeftJoinStr()))
//                throw new RuntimeException("delete 操作不能带有 orderBy() limit() label() leftJoin() 等方法!");
//            result.setSql(sql.toString() + result.getConditions());
//        }
//
//        //执行修改操作 update table set = ?
//        else if (symbol == Symbol.UPDATE) {
//            sql.append("update " + table.name() + " set");
//            if (fields != null && fields.length != 0) {
//                List<Object> objects = new ArrayList<>();
//                for (Field field : fields) {
//                    try {
//                        Column column = field.getAnnotation(Column.class);
//                        field.setAccessible(true);
//                        Object o = field.get(obj);
//                        if (o != null && column != null) {
//                            if (field.getAnnotation(Id.class) == null) {
//                                sql.append(" " + column.name() + "=?,");
//                                objects.add(o);
//                            } else {
//                                result.addCondition("and " + column.name() + "=? ");
//                                result.addValue(o);
//                            }
//                        }
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//                result.setParams(objects.toArray());
//            }
//            if (!"".equals(result.getOrderByStr())
//                    || !"".equals(result.getLimitStr())
//                    || !"".equals(result.getLabelStr())
//                    || !"".equals(result.getLeftJoinStr()))
//                throw new RuntimeException("update 操作不能带有orderBy() limit() label() leftJoin() 等方法!");
//            //剔除最后一个','字符
//            sql.delete(sql.length() - 1, sql.length());
//
//            String sqlStr = sql.toString();
//            if ("".equals(result.getWhereStr()))
//                sqlStr = sqlStr + result.getConditions();
//            else
//                sqlStr = sqlStr + result.getWhereStr();
//
//            result.setSql(sqlStr);
//        }
//        //执行修改操作 select count(1)
//        else if (symbol == Symbol.COUNT) {
//            sql.append("select count(1) as count");
//            setConditionsAndValues(fields, obj, result);
//            sql.append(" from " + table.name() + result.getLeftJoinStr());
//            String whereStr = result.getWhereStr();
//            if ("".equals(whereStr)) whereStr = result.getConditions();
//            else result.setParams(result.getValues().toArray());
//            result.setSql(sql + whereStr);
//        }
//        return result;
//    }
//
//    public static <T> SqlParams getSql(Symbol symbol, T obj, Object id) {
//        Class<?> cls = obj.getClass();
//        Table table = cls.getAnnotation(Table.class);
//        if (table == null) throw new RuntimeException("映射实体没有指定表名称!");
//        StringBuffer sql = new StringBuffer();
//        if (symbol == Symbol.DELETE) {
//            Field[] fields = cls.getDeclaredFields();
//            if (fields != null && fields.length != 0) {
//                for (Field field : fields) {
//                    Column column = field.getAnnotation(Column.class);
//                    if (column != null && field.getAnnotation(Id.class) != null) {
//                        sql.append("delete from " + table.name()+" where " + column.name() + "=?");
//                        break;
//                    }
//                }
//            }
//        }
//        if("".equals(sql)) throw new RuntimeException("映射实体 ["+cls+"] 没有指定Id注解和Column注解!");
//        return new SqlParams(sql.toString(), id);
//    }
//
//    private static <T> void setConditionsAndValues(Field[] fields, T obj, SqlParams result) {
//        try {
//            if (fields != null && fields.length != 0) {
//                for (Field field : fields) {
//                    Column column = field.getAnnotation(Column.class);
//                    if (column != null) {
//                        field.setAccessible(true);
//                        Object o = field.get(obj);
//                        if (o != null) {
//                            result.addCondition("and " + column.name() + "=? ");
//                            result.addValue(o);
//                        }
//                    }
//                }
//            }
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
