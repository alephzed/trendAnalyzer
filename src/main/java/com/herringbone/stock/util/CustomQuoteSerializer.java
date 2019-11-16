package com.herringbone.stock.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.herringbone.stock.model.QuoteBase;

import java.io.IOException;

public class CustomQuoteSerializer extends JsonSerializer<QuoteBase> {

    @Override
    public void serialize
            (QuoteBase value, JsonGenerator jgen, SerializerProvider arg2)
            throws IOException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", value.getId());
        jgen.writeNumberField("open", value.getOpen());
        jgen.writeNumberField("high", value.getHigh());
        jgen.writeNumberField("low", value.getLow());
        jgen.writeNumberField("close", value.getClose());
        jgen.writeEndObject();
    }
}
