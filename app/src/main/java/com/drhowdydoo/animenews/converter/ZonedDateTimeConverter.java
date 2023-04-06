package com.drhowdydoo.animenews.converter;

import androidx.room.TypeConverter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeConverter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy h:mm a");

    @TypeConverter
    public static ZonedDateTime toZonedDateTime(String value) {
        if (value == null) {
            return null;
        }
        return ZonedDateTime.parse(value, formatter);
    }

    @TypeConverter
    public static String toString(ZonedDateTime value) {
        if (value == null) {
            return null;
        }
        return value.format(formatter);
    }
}

