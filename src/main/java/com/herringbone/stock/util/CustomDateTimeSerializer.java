package com.herringbone.stock.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

    private static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void serialize
            (ZonedDateTime value, JsonGenerator gen, SerializerProvider arg2)
            throws IOException {
        gen.writeString(formatter.format(value));
    }
}
