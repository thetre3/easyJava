package com.easyjava.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;


public class DateUtils {
    public static String format(LocalDateTime localDateTime, String formatter) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
        String time = localDateTime.format(dateTimeFormatter);
        return time;
    }

    public static LocalDateTime parse(String date, String formatter) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
            TemporalAccessor parse = dateTimeFormatter.parse(date);
            return LocalDateTime.from(parse);
        } catch (DateTimeParseException ignored) {
        }
        return null;
    }
}
