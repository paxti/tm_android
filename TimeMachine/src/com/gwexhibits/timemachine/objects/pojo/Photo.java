package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gwexhibits.timemachine.objects.sf.PhotoObject;

import java.io.Serializable;

/**
 * Created by psyfu on 3/17/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo implements Serializable {

    @JsonProperty(PhotoObject.PATH)
    private String localPath;

    @JsonProperty(PhotoObject.DROPBOX_PATH)
    private String dropboxPath;

    @JsonProperty(PhotoObject.PATH)
    private String phase;

    @JsonProperty(PhotoObject.ORDER)
    private Order order;

    Photo(){

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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
