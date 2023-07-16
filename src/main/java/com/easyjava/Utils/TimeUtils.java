package com.easyjava.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

public class TimeUtils {
    private static Logger logger= LoggerFactory.getLogger(TimeUtils.class);
    public final static String DATE_AND_TIME_HYPHEN="yyyy-MM-dd HH:mm:ss";
    public final static  String JUST_DATE_SLASH="yyyy/MM/dd";
    public final static String JUST_DATE_HYPHEN="yyyy-MM-dd";
    //time转化为String
    public static String  format(LocalDateTime localDateTime,String formatter){
        DateTimeFormatter dateTimeFormatter =DateTimeFormatter.ofPattern(formatter);
        String time=localDateTime.format(dateTimeFormatter);
        return time;
    }
    //string转换为时间类型
    public static  LocalDateTime parse(String date,String formatter){
        try {
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern(formatter);
            TemporalAccessor parse = dateTimeFormatter.parse(date);
            return LocalDateTime.from(parse);
        }catch (DateTimeParseException e){
            logger.info("转换时间类型失败",e);
        }
        return null;
    }
}
