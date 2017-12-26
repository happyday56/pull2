package com.lgh.pull2.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * LocalDateTime工具
 * Created by hot on 2017/5/31.
 */
public class LocalDateTimeUtils {

    /**
     * 获取第二天
     *
     * @param date
     * @return
     */
    public static String getNext(String date) {
        LocalDateTime now = convert(date);
        LocalDateTime next = getNext(now);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return next.format(dateTimeFormatter);
    }

    /**
     * 转为localDate
     *
     * @param date
     * @return
     */
    public static LocalDateTime convert(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
        return localDate.atStartOfDay();
    }

    public static String convert(LocalDateTime date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(dateTimeFormatter);
    }

    /**
     * 获取第二天
     *
     * @return
     */
    public static LocalDateTime getNext(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(localDateTime.plusDays(1).format(formatter)).atStartOfDay();
    }

    /**
     * 获取今天的凌晨时间
     *
     * @return
     */
    public static LocalDateTime getStartDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(LocalDateTime.now().format(formatter)).atStartOfDay();
    }


    /**
     * 转换为date
     *
     * @param localDateTime
     * @return
     * @throws ParseException
     */
    public static Date ConvertToDate(LocalDateTime localDateTime) throws ParseException {
        DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.parse(localDateTime.format(localDateFormatter));
    }

    /**
     * 周几
     *
     * @param date
     * @return
     */
    public static String getWeekName(String date) {
        DayOfWeek dayOfWeek = convert(date).getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY:
                return "周一";
            case TUESDAY:
                return "周二";
            case WEDNESDAY:
                return "周三";
            case THURSDAY:
                return "周四";
            case FRIDAY:
                return "周五";
            case SATURDAY:
                return "周六";
            case SUNDAY:
                return "周日";
        }
        return "";
    }
}
