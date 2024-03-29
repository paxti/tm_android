package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gwexhibits.timemachine.objects.EndAfterStartException;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;
import com.salesforce.androidsdk.smartsync.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by psyfu on 3/17/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Time implements Serializable {

    @JsonProperty(SmartStore.SOUP_ENTRY_ID)
    private Long entyId;

    @JsonProperty(Constants.ID)
    private String id;

    @JsonProperty(TimeObject.ORDER)
    @JsonView({Views.Full.class, Views.SimpleOrder.class})
    private String orderId;

    @JsonProperty(TimeObject.NOTE)
    private String note;

    @JsonProperty(TimeObject.PHASE)
    @JsonView(Views.Full.class)
    private String phase;

    @JsonProperty(TimeObject.START_TIME)
    @JsonFormat(locale = "en", shape = JsonFormat.Shape.STRING, pattern = Utils.SF_FORMAT, timezone = Utils.GREENWICH_TIME_ZONE )
    private Date startTime;

    @JsonProperty(TimeObject.END_TIME)
    @JsonFormat(locale = "en", shape = JsonFormat.Shape.STRING, pattern = Utils.SF_FORMAT, timezone = Utils.GREENWICH_TIME_ZONE )
    private Date endTime;

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

    public Time(){}

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Date getEndTimeForPicker(){
        if (endTime == null){
            return startTime;
        } else {
            return endTime;
        }
    }

    public void setEndTime(Date endTime) {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getLocallyDeleted() {
        return locallyDeleted;
    }

    public void setLocallyDeleted(Boolean locallyDeleted) {
        this.locallyDeleted = locallyDeleted;
    }

    public String getSyncStatus(){
        if (this.local){
            return "Not synced";
        } else {
            return "Synced";
        }
    }

    public Time start(){
        setLocal(true);
        setLocallyCreated(true);
        setLocallyDeleted(false);
        setLocallyUpdated(false);
        setStartTime(new Date());
        setId(String.valueOf(System.currentTimeMillis()));
        Attribute attribute = new Attribute();
        attribute.setType(TimeObject.TIME_SF_OBJECT);
        setAttribute(attribute);
        return this;
    }

    public Time stop(){
        setEndTime(new Date());
        setLocallyModified();
        return this;
    }

    public void changeStartTime(Date date) throws EndAfterStartException {

        if(date.after(endTime)){
            throw new EndAfterStartException();
        }

        setStartTime(date);
        setLocallyModified();
    }

    public void changeEndTime(Date date) throws EndAfterStartException {

        if(startTime.after(date)){
            throw new EndAfterStartException();
        }

        setEndTime(date);
        setLocallyModified();
    }

    public void changeOrder(String orderId){
        setOrderId(orderId);
        setLocallyModified();
    }

    private void setLocallyModified(){
        setLocal(true);
        setLocallyUpdated(true);
    }

    public void changeDate(Date time) {
        Calendar newDate = Calendar.getInstance();
        newDate.setTime(time);

        setStartTime(changeDateOnly(newDate, getStartTime()));
        setEndTime(changeDateOnly(newDate, getEndTime()));

        setLocallyModified();
    }

    public void changePhase(String phase) {
        setPhase(phase);
        setLocallyModified();
    }

    private Date changeDateOnly(Calendar newDate, Date oldDateTime){
        Calendar c = Calendar.getInstance();
        c.setTime(oldDateTime);
        c.set(Calendar.YEAR, newDate.get(Calendar.YEAR));
        c.set(Calendar.MONTH, newDate.get(Calendar.MONTH));
        c.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }
}
