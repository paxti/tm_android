package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gwexhibits.timemachine.utils.Utils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by psyfu on 4/7/2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatterCommentEntity implements Serializable {

    @JsonProperty("body")
    private ChatterBody chatterBody;

    @JsonProperty("user")
    private ChatterActor actor;

    @JsonProperty("createdDate")
    @JsonFormat(locale = "en", shape = JsonFormat.Shape.STRING, pattern = Utils.CHATTER_FORMAT, timezone = Utils.GREENWICH_TIME_ZONE )
    private Date createdDate;

    @JsonIgnoreProperties("url")
    private String url;

    public ChatterCommentEntity() {

    }

    public ChatterBody getChatterBody() {
        return chatterBody;
    }

    public void setChatterBody(ChatterBody chatterBody) {
        this.chatterBody = chatterBody;
    }

    public ChatterActor getActor() {
        return actor;
    }

    public void setActor(ChatterActor actor) {
        this.actor = actor;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
