package com.gwexhibits.timemachine.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gwexhibits.timemachine.objects.pojo.Order;

import java.io.IOException;

/**
 * Created by psyfu on 3/17/2016.
 */
public class OrderSerializer extends JsonSerializer<Order> {
    @Override
    public void serialize(Order value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeString(value.getId());
    }
}
