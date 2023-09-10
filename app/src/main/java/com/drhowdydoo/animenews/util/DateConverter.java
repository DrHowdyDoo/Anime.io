package com.drhowdydoo.animenews.util;

import com.tickaroo.tikxml.TypeConverter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateConverter implements TypeConverter<ZonedDateTime> {

    private DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    private DateTimeFormatter outputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public ZonedDateTime read(String value) throws Exception {
        OffsetDateTime inputDateTime = OffsetDateTime.parse(value, inputFormatter);
        ZoneId localTimeZone = ZoneId.systemDefault();
        return inputDateTime.atZoneSameInstant(localTimeZone);
    }

    @Override
    public String write(ZonedDateTime value) throws Exception {
        return value.format(outputFormatter);
    }
}
