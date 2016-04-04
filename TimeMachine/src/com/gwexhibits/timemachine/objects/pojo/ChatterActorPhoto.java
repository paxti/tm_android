package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by psyfu on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatterActorPhoto implements Serializable {

    @JsonProperty("fullEmailPhotoUrl")
    private String photoUrl;

    public ChatterActorPhoto(){

    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

