package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gwexhibits.timemachine.objects.sf.OrderObject;

import java.io.Serializable;

/**
 * Created by psyfu on 3/17/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Opportunity implements Serializable {

    @JsonProperty(OrderObject.ATTRIBUTES)
    private Attribute attribute;

    @JsonProperty(OrderObject.CONFIGURATION)
    private Configuration configuration;

    Opportunity(){

    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
