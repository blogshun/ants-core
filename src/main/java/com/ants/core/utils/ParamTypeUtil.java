package com.ants.core.utils;


import com.ants.common.utils.StrUtil;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 类型转换处理工具
 *
 * @author MrShun
 * @version 1.0
 */
public class ParamTypeUtil {

    /**
     * 类型转换
     *
     * @param value
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T parse(String value, Class<T> cls) {
        T result = null;
        if (StrUtil.isBlank(value)) {
            return result;
        }
        try {
            if (cls == String.class || cls == char.class) {
                result = (T) value;
            } else if (cls == Integer.class || cls == int.class) {
                result = (T) Integer.valueOf(value);
            } else if (cls == Boolean.class || cls == boolean.class) {
                result = (T) Boolean.valueOf(value);
            } else if (cls == Long.class || cls == long.class) {
                result = (T) Long.valueOf(value);
            } else if (cls == Byte.class || cls == byte.class) {
                result = (T) Byte.valueOf(value);
            } else if (cls == Short.class || cls == short.class) {
                result = (T) Short.valueOf(value);
            } else if (cls == Float.class || cls == float.class) {
                result = (T) Float.valueOf(value);
            } else if (cls == Double.class || cls == double.class) {
                result = (T) Double.valueOf(value);
            } else if (cls == BigDecimal.class) {
                BigDecimal decimal = new BigDecimal(value);
                result = (T) decimal;
            }
        }

//        catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            throw new IllegalArgumentException("字符编码转换出现异常!");
//        }

        catch (Exception e) {
            throw new IllegalArgumentException("参数异常, 类型不匹配!");
        }
        return result;
    }

    public static <T> T[] parse(String[] value, Class<T> cls) {
        T[] result = null;
        if (value == null) {
            return result;
        }
        try {

            if (cls == String[].class || cls == char[].class) {
                return (T[]) value;
            } else if (cls == Integer[].class || cls == int[].class) {
                result = (T[]) new Integer[value.length];
                for (int i = 0; i < value.length; i++) {
                    result[i] = (T) Integer.valueOf(value[i]);
                }
                return result;
            } else if (cls == Boolean[].class || cls == boolean[].class) {
                result = (T[]) new Boolean[value.length];
                for (int i = 0; i < value.length; i++) {
                    result[i] = (T) Boolean.valueOf(value[i]);
                }
                return result;
            } else if (cls == Long[].class || cls == long[].class) {
                result = (T[]) new Long[value.length];
                for (int i = 0; i < value.length; i++) {
                    result[i] = (T) Long.valueOf(value[i]);
                }
                return result;
            } else if (cls == Byte[].class || cls == byte[].class) {
                result = (T[]) new Byte[value.length];
                for (int i = 0; i < value.length; i++) {
                    result[i] = (T) Byte.valueOf(value[i]);
                }
                return result;
            } else if (cls == Short[].class || cls == short[].class) {
                result = (T[]) new Short[value.length];
                for (int i = 0; i < value.length; i++) {
                    result[i] = (T) Short.valueOf(value[i]);
                }
                return result;
            } else if (cls == Float[].class || cls == float[].class) {
                result = (T[]) new Float[value.length];
                for (int i = 0; i < value.length; i++) {
                    result[i] = (T) Float.valueOf(value[i]);
                }
                return result;
            } else if (cls == Double[].class || cls == double[].class) {
                result = (T[]) new Double[value.length];
                for (int i = 0; i < value.length; i++) {
                    result[i] = (T) Double.valueOf(value[i]);
                }
                return result;
            } else if (cls == BigDecimal.class) {
                result = (T[]) new BigDecimal[value.length];
                for (int i = 0; i < value.length; i++) {
                    BigDecimal decimal = new BigDecimal(value[i]);
                    result[i] = (T) decimal;
                }
                return result;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("参数异常, 类型不匹配!");
        }
        return result;
    }


    /**
     * 处理基本数据类型默认值
     *
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> Object setDefault(Class<T> cls) {
        try {
            if (cls == char.class) {
                return ' ';
            } else if (cls == int.class || cls == byte.class || cls == short.class || cls == long.class) {
                return 0;
            } else if (cls == boolean.class) {
                return false;
            } else if (cls == float.class) {
                return 0.0f;
            } else if (cls == double.class) {
                return 0.0;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("参数转换错误!");
        }
        return null;
    }


    /**
     * 判断一个类是否为基本数据类型。
     *
     * @param clazz 要判断的类。
     * @return true 表示为基本数据类型。
     */
    public static boolean isBaseDataType(Class clazz) {
        return
                (
                        clazz.equals(String.class) ||
                                clazz.equals(Integer.class) ||
                                clazz.equals(Byte.class) ||
                                clazz.equals(Long.class) ||
                                clazz.equals(Double.class) ||
                                clazz.equals(Float.class) ||
                                clazz.equals(Character.class) ||
                                clazz.equals(Short.class) ||
                                clazz.equals(BigDecimal.class) ||
                                clazz.equals(BigInteger.class) ||
                                clazz.equals(Boolean.class) ||
                                clazz.isPrimitive()
                );
    }

}
