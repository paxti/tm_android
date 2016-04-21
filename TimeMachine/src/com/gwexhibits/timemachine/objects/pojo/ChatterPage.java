package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by psyfu on 4/7/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatterPage implements Serializable {

    @JsonProperty("currentPageUrl")
    private String currentPageUrl;

    @JsonProperty("nextPageUrl")
    private String nextPageUrl;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("items")
    private List<ChatterCommentEntity> commentEntityList;

    public ChatterPage() {

    }

    public String getCurrentPageUrl() {
        return currentPageUrl;
    }

    public void setCurrentPageUrl(String currentPageUrl) {
        this.currentPageUrl = currentPageUrl;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<ChatterCommentEntity> getCommentEntityList() {
        return commentEntityList;
    }

    public void setCommentEntityList(List<ChatterCommentEntity> commentEntityList) {
        this.commentEntityList = commentEntityList;
    }
}
