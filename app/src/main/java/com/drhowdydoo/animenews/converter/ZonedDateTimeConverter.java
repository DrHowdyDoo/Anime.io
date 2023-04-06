package com.drhowdydoo.animenews.converter;

import androidx.room.TypeConverter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeConverter {
    private static final DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault());

    @TypeConverter
    public static ZonedDateTime toZonedDateTime(String value) {
        if (value == null) {
            return null;
        }
        return ZonedDateTime.parse(value, isoFormatter);
    }

    @TypeConverter
    public static String toString(ZonedDateTime value) {
        if (value == null) {
            return null;
        }
        return value.format(isoFormatter);
    }
}

