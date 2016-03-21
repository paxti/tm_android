package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by psyfu on 3/17/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Time implements Serializable {

    @JsonProperty(SmartStore.SOUP_ENTRY_ID)
    private Long entyId;

    @JsonProperty(TimeObject.ORDER)
    @JsonView({Views.Full.class, Views.SimpleOrder.class})
    private String orderId;

    @JsonProperty(TimeObject.NOTE)
    private String note;

    @JsonProperty(TimeObject.PHASE)
    @JsonView(Views.Full.class)
    private String phase;

    @JsonProperty(TimeObject.START_TIME)
    private String startTime;

    @JsonProperty(TimeObject.END_TIME)
    private String endTime;

    @JsonProperty(OrderObject.ATTRIBUTES)
    private Attribute attribute;

    @JsonProperty(TimeObject.LOCAL)
    private Boolean local;

    @JsonProperty(TimeObject.LOCALY_CREATED)
    private Boolean locallyCreated;

    @JsonProperty(TimeObject.LOCALY_UPDATED)
    private Boolean locallyUpdated;

    @JsonProperty(TimeObject.LOCALY_DELETED)
    private Boolean locallyDeleted;


    public Time(){

    }

    public Time(String orderId, String phase){
        setOrderId(orderId);
        setPhase(phase);
    }

    public Long getEntyId() {
        return entyId;
    }

    public String getEntyIdInString() {
        return String.valueOf(entyId);
    }

    public void setEntyId(String entyId) {
        this.entyId = Long.parseLong(entyId);
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getNote() {
        if (note == null){
            return "";
        }else {
            return note;
        }
    }

    public void setNote(String note) {

        if (note != null){
            this.note = note;
        }else{
            this.note = "";
        }
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

    public Boolean getLocal() {
        return local;
    }

    public void setLocal(Boolean local) {
        this.local = local;
    }

    public Boolean getLocallyCreated() {
        return locallyCreated;
    }

    public void setLocallyCreated(Boolean locallyCreated) {
        this.locallyCreated = locallyCreated;
    }

    public Boolean getLocallyUpdated() {
        return locallyUpdated;
    }

    public void setLocallyUpdated(Boolean locallyUpdated) {
        this.locallyUpdated = locallyUpdated;
    }

    public Boolean getLocallyDeleted() {
        return locallyDeleted;
    }

    public void setLocallyDeleted(Boolean locallyDeleted) {
        this.locallyDeleted = locallyDeleted;
    }

    public Time start(){
        setLocal(true);
        setLocallyCreated(true);
        setLocallyDeleted(false);
        setLocallyUpdated(false);
        setStartTime(Utils.getCurrentTimeInSfFormat());
        setEntyId(String.valueOf(System.currentTimeMillis()));
        Attribute attribute = new Attribute();
        attribute.setType(TimeObject.TIME_SF_OBJECT);
        setAttribute(attribute);
        return this;
    }

    public Time stop(){
        setEndTime(Utils.getCurrentTimeInSfFormat());
        return this;
    }
}
