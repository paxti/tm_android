package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gwexhibits.timemachine.utils.Utils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by psyfu on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatterPost implements Serializable {

    @JsonProperty("id")
    private String postId;

    @JsonProperty("body")
    private ChatterBody chatterBody;

    @JsonProperty("actor")
    private ChatterActor actor;

    @JsonProperty("feedElementType")
    private String elementType;

    @JsonProperty("header")
    private ChatterMassageHeader header;

    @JsonProperty("capabilities")
    private ChatterEntryCapabilities capabilities;

    @JsonIgnoreProperties("subjectId")
    private String subjectId;

    @JsonIgnoreProperties("url")
    private String url;

    @JsonProperty("createdDate")
    @JsonFormat(locale = "en", shape = JsonFormat.Shape.STRING, pattern = Utils.CHATTER_FORMAT, timezone = Utils.GREENWICH_TIME_ZONE )
    private Date createdDate;

    public ChatterPost(){

    }

    public ChatterBody getChatterBody() {
        return chatterBody;
    }

    public void setChatterBody(ChatterBody chatterBody) {
        this.chatterBody = chatterBody;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
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

    public ChatterMassageHeader getHeader() {
        return header;
    }

    public void setHeader(ChatterMassageHeader header) {
        this.header = header;
    }

    public ChatterEntryCapabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(ChatterEntryCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent(){
        if(getChatterBody().getContent().equals("")){
            return getHeader().getText();
        } else {
            return  getChatterBody().getContent();
        }
    }

    public List<ChatterCommentEntity> getComments(){
        return this.getCapabilities().getCommentsPage().getPage().getCommentEntityList();
    }
}
