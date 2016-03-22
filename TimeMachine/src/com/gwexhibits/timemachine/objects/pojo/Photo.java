package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gwexhibits.timemachine.objects.sf.PhotoObject;
import com.salesforce.androidsdk.smartstore.store.SmartStore;

import java.io.Serializable;

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

    public Photo(String localPath, String dropboxPath, String phase, String orderId){
        setLocalPath(localPath);
        setDropboxPath(dropboxPath);
        setPhase(phase);
        setOrder(orderId);
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
