package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gwexhibits.timemachine.objects.sf.OrderObject;

import java.io.Serializable;

/**
 * Created by psyfu on 3/17/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account implements Serializable {

    @JsonProperty(OrderObject.ACCOUNT_NAME)
    private String name;

    @JsonProperty(OrderObject.ATTRIBUTES)
    private Attribute attribute;

    Account(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
}
