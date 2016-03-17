package com.gwexhibits.timemachine.objects.pojo;

import android.text.Html;
import android.text.Spanned;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.util.Constants;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by psyfu on 3/16/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order implements Serializable {

    @JsonProperty(Constants.ID)
    private String id;

    @JsonProperty(SmartStore.SOUP_ENTRY_ID)
    private Long entyId;

    @JsonProperty(OrderObject.ORDER_NUMBER)
    private String orderNumber;

    @JsonProperty(OrderObject.ORDER_TYPE)
    private String orderType;

    @JsonProperty(OrderObject.SFID)
    private String sfid;

    @JsonProperty(OrderObject.SHOW_NAME)
    private String showName;

    @JsonProperty(OrderObject.SHIPPING_DATE)
    private String shippingDate;

    @JsonProperty(OrderObject.INSTRUCTIONS)
    private String instructions;

    @JsonProperty(OrderObject.DROPBOX_LINK)
    private String dropboxLink;

    @JsonProperty(OrderObject.ATTRIBUTES)
    private Attribute attribute;

    @JsonProperty(OrderObject.ACCOUNT)
    private Account account;

    @JsonProperty(OrderObject.RELEATED_OPPORTUNITY)
    private Opportunity opportunity;

    public Order(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderNumberShort(){ return orderNumber.replaceFirst("^0+(?!$)", ""); }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSfid() {
        return sfid;
    }

    public void setSfid(String sfid) {
        this.sfid = sfid;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = Html.escapeHtml(showName);
    }

    public String getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(String shippingDate) {
        this.shippingDate = shippingDate;
    }

    public String getInstructions() {
        return instructions;
    }

    public Spanned getInstructionsInHtml() {
        return Html.fromHtml(this.getInstructions());
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getDropboxLink() {
        return dropboxLink;
    }

    public void setDropboxLink(String dropboxLink) {
        this.dropboxLink = dropboxLink;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    public String getTitleForOptions(){

        return getSfid() + " (*" + getOrderNumberShort() + ") " + getOrderTitle();

    }

    public String getOrderTitle(){
        return getAccount().getName() + "@" + getShowName();
    }
}
