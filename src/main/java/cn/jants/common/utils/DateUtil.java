package cn.jants.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author MrShun
 * @version 1.0
 */
public class DateUtil {
    /**
     * 微信朋友圈时间提示效果
     *
     * @param date      需要判断的时间
     * @param now       当前时间
     * @param formatStr 格式化字符串
     * @return 提示字符串
     */
    public static String toTips(Date date, Date now, String formatStr) {
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

        if (day > 0) {
            if (day == 1) {
                return "昨天";
            } else if (day < 30) {
                return (day + "天前");
            } else {
                DateFormat df = new SimpleDateFormat(formatStr);
                return (df.format(date));
            }
        }
        if (hour > 0) {
            return hour + "小时前";
        }
        if (min > 0) {
            return min + "分钟前";
        }
        return s + "秒前";
    }

    /**
     * 秒杀时间提示效果
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 提示字符串
     */
    public static String toMsTips(Date startDate, Date endDate) {
        long toDay = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();
        if (startDate == null && endDate == null) {
            return "";
        } else if (startDate == null) {
            long endTime = endDate.getTime();
            if (toDay > endTime) {
                return "已结束";
            } else {
                long l = endTime - toDay;
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);
                long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);

                if (day > 0) {
                    sb.append(day + "天");
                }
                if (hour > 0) {
                    sb.append(hour + "时");
                }
                if (min > 0) {
                    sb.append(min + "分");
                }
            }
            return sb.toString();
        } else if (endDate == null) {
            long startTime = startDate.getTime();
            if (toDay > startTime) {
                return "进行中";
            } else {
                return "已结束";
            }
        } else {
            long startTime = startDate.getTime();
            long endTime = endDate.getTime();
            if (toDay < startTime) {
                sb.append("未开始");
            } else if (toDay >= startTime && toDay <= endTime) {
                sb.append("剩 ");
                long l = endTime - toDay;
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);
                long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);

                if (day > 0) {
                    sb.append(day + "天");
                }
                if (hour > 0) {
                    sb.append(hour + "时");
                }
                if (min > 0) {
                    sb.append(min + "分");
                }
            } else if (toDay > endTime) {
                sb.append("已结束");
            }
            return sb.toString();
        }
    }

    /**
     * 判断发布时间是否是今天
     *
     * @param date 需要判断的时间
     * @param now  当前时间
     * @return 布尔值
     */
    public static boolean isDay(Date date, Date now) {
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        return day > 0 ? false : true;
    }

    /**
     * 根据时间格式转换成字符串
     *
     * @param formatStr 时间格式
     * @return 格式化字符串
     */
    public static String getDataTime(String formatStr) {
        DateFormat df = new SimpleDateFormat(formatStr);
        return df.format(new Date());
    }

    public static String toStrDate(Date date, String formatStr) {
        DateFormat df = new SimpleDateFormat(formatStr);
        return df.format(date);
    }

    public static Date toDate(String date, String formatStr) {
        DateFormat df = new SimpleDateFormat(formatStr);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
