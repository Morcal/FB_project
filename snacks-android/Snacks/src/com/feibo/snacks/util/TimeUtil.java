package com.feibo.snacks.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    /**
     * 生成时间戳
     * 
     * @return
     */
    public static String generateTimestamp() {
        Date date = new Date(new Date().getTime());

        return getTimestampSDF().format(date);
    }

    /**
     * 取得时间戳的 SimpleDateFormat
     * 
     * @return
     */
    private static SimpleDateFormat getTimestampSDF() {
        return new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public static String getTimeStr() {
        Date date = new Date(new Date().getTime());
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
    }

    public static String transformTime(long second) {
        if (second <= 0) {
            return "已结束";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("仅剩");
        int time = (int) (second / 60 / 60 / 24);
        if (time >= 1) {
            builder.append(time + "天");
            second = (second % (time * 60 * 60 * 24));
        }
        time = (int) (second / 60 / 60);
        if (time >= 1) {
            if (time < 10) {
                builder.append("0");
            }
            builder.append(time + ":");
            second = (second % (time * 60 * 60));
        }else {
            builder.append("00:");
        }
        time = (int) (second / 60);
        if (time >= 1) {
            if (time < 10) {
                builder.append("0");
            }
            builder.append(time + ":");
            second =(second % (time * 60));
        } else {
            builder.append("00:");
        }
        if (second < 10) {
            builder.append("0");
        }
        builder.append(second);
        return builder.toString();
    }

    /**
     * @param preTime 单位秒
     * @return
     */
    public static long getSpaceTimeTransSecond(long preTime) {
        long time = System.currentTimeMillis() / 1000;
        return time - preTime;
    }

    public static long getCurrentTimeTransSecond() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getTime(long endTime) {
        long time = System.currentTimeMillis() / 1000;
        return String.valueOf(endTime - time);
    }

    public static String transDetailTime(long second) {
        if (second < 0) {
            return "00秒";
        }
        StringBuilder builder = new StringBuilder();
        int time = (int) (second / 60 / 60 / 24);
        if (time >= 1) {
            builder.append(time + "天");
            second = (second % (time * 60 * 60 * 24));
        }
        time = (int) (second / 60 / 60);
        if (time >= 1) {
            if (time < 10) {
                builder.append("0");
            }
            builder.append(time + "小时");
            second = (second % (time * 60 * 60));
        }else {
            builder.append("00小时");
        }
        time = (int) (second / 60);
        if (time >= 1) {
            if (time < 10) {
                builder.append("0");
            }
            builder.append(time + "分");
            second =(second % (time * 60));
        } else {
            builder.append("00分");
        }
        if (second < 10) {
            builder.append("0");
        }
        builder.append(second + "秒");
        return builder.toString();
    }

    public static boolean isEnd(long time) {
        return time - (System.currentTimeMillis() / 1000) <= 0 ? true : false;
    }

    public static String getTimeRange(long startTime, long endTime) {
        StringBuilder builder = new StringBuilder();
        return builder.append(timeStampToTime(startTime)).append(" - ").append(timeStampToTime(endTime)).toString();
    }

    public static String timeStampToTime(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
        String date = sdf.format(new Date(timeStamp*1000L));
        return date;
    }
}
