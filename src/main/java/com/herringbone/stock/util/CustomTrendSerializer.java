package com.herringbone.stock.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.herringbone.stock.model.Trendtype;

import java.io.IOException;

public class CustomTrendSerializer extends JsonSerializer<Trendtype> {
    @Override
    public void serialize(Trendtype value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("Description", value.getDescription());
        jgen.writeNumberField("TrendValue", value.getTrendvalue());
        jgen.writeNumberField("Id", value.getId());
        jgen.writeEndObject();
    }
}
