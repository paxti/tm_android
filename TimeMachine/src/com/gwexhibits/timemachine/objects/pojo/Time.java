package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.objects.sf.TimeObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by psyfu on 3/17/2016.
 */
public class Time implements Serializable {

    @JsonProperty(TimeObject.ORDER)
    private Order order;

    @JsonProperty(TimeObject.NOTE)
    private String note;

    @JsonProperty(TimeObject.PHASE)
    private String phase;

    @JsonProperty(TimeObject.START_TIME)
    private String startTime;

    @JsonProperty(TimeObject.END_TIME)
    private String endTime;

    @JsonProperty(OrderObject.ATTRIBUTES)
    private Attribute attribute;

    public Time(){

    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getPhotoName(){
        return getOrder().getSfid() + "_" + getPhase() + "_" + new Date().toString() + ".jpg";
    }
}
