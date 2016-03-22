package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gwexhibits.timemachine.objects.sf.OrderObject;

import java.io.Serializable;

/**
 * Created by psyfu on 3/17/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration implements Serializable {

    @JsonProperty(OrderObject.CONFIGURATION_NAME)
    private String name;

    @JsonProperty(OrderObject.CONFIGURATION_TIME_PRE_STAGE)
    private String preStageTime;

    @JsonProperty(OrderObject.CONFIGURATION_TIME_UP)
    private String upTime;

    @JsonProperty(OrderObject.CONFIGURATION_TIME_DOWN)
    private String downTime;

    @JsonProperty(OrderObject.CONFIGURATION_TIME_RI)
    private String riTime;

    @JsonProperty(OrderObject.ATTRIBUTES)
    private Attribute attribute;

    Configuration(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreStageTime() {
        return preStageTime;
    }

    public void setPreStageTime(String preStageTime) {
        this.preStageTime = preStageTime;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public String getDownTime() {
        return downTime;
    }

    public void setDownTime(String downTime) {
        this.downTime = downTime;
    }

    public String getRiTime() {
        return riTime;
    }

    public void setRiTime(String riTime) {
        this.riTime = riTime;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
}
