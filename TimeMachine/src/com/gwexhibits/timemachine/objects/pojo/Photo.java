package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.objects.sf.PhotoObject;
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.salesforce.androidsdk.smartstore.store.SmartStore;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * Created by psyfu on 3/17/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo implements Serializable {

    private static final String localFolder = "/data/photos/";

    @JsonProperty(SmartStore.SOUP_ENTRY_ID)
    private Long entyId;

    @JsonProperty(PhotoObject.PATH)
    private String localPath;

    @JsonProperty(PhotoObject.DROPBOX_PATH)
    private String dropboxPath;

    @JsonProperty(PhotoObject.PHASE)
    private String phase;

    @JsonProperty(PhotoObject.ORDER)
    private String orderId;

    public Photo(){

    }

    public Photo(String localPath, String fileName, String phase, Order order) throws UnsupportedEncodingException {
        setLocalPath(localPath);
        setDropboxPath(order, fileName, phase);
        setPhase(phase);
        setOrder(order.getEntyIdInString());
    }

    public Long getEntyId() {
        return entyId;
    }

    public void setEntyId(Long entyId) {
        this.entyId = entyId;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getDropboxPath() {
        return dropboxPath;
    }

    public void setDropboxPath(String dropboxPath) {
        this.dropboxPath = dropboxPath;
    }

    public void setDropboxPath(Order order, String fileName, String phase) throws UnsupportedEncodingException {

        if (phase.equals(OrderObject.LIST_OF_STAGE_SOS[0])) {
            phase = phase + " - " + order.getOrderNumberShort();
        }

        this.dropboxPath = order.getDecodedDropboxLink() + "/" + phase + "/" + fileName;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getOrder() {
        return orderId;
    }

    public void setOrder(String orderId) {
        this.orderId = orderId;
    }

}
