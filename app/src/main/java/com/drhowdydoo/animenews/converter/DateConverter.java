package com.drhowdydoo.animenews.converter;

import com.tickaroo.tikxml.TypeConverter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateConverter implements TypeConverter<String> {

    private DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z");
    private DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy h:mm a");

    @Override
    public String read(String value) throws Exception {
        OffsetDateTime inputDateTime = OffsetDateTime.parse(value, inputFormatter);
        ZoneId localTimeZone = ZoneId.systemDefault();
        ZonedDateTime localDateTime = inputDateTime.atZoneSameInstant(localTimeZone);
        return localDateTime.format(outputFormatter);
    }

    @Override
    public String write(String value) throws Exception {
        OffsetDateTime inputDateTime = OffsetDateTime.parse(value, inputFormatter);
        ZoneId localTimeZone = ZoneId.systemDefault();
        ZonedDateTime localDateTime = inputDateTime.atZoneSameInstant(localTimeZone);
        return localDateTime.format(outputFormatter);
    }
}
