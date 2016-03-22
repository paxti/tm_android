package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.gwexhibits.timemachine.serializers.OrderSerializer;

/**
 * Created by psyfu on 3/18/2016.
 */
public class Views {
    public static class Full {

    }

    @JsonSerialize(using = OrderSerializer.class)
    public static class SimpleOrder {

    }

    public  static class OrderText extends SimpleOrder{

        @JsonIgnore
        @JsonProperty(TimeObject.ORDER)
        private Order order;
    }
}
