package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by psyfu on 4/7/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatterEntryCapabilities implements Serializable {

    @JsonProperty("comments")
    private ChatterCommentsPage commentsPage;

    public ChatterEntryCapabilities(){

    }

    public ChatterCommentsPage getCommentsPage() {
        return commentsPage;
    }

    public void setCommentsPage(ChatterCommentsPage commentsPage) {
        this.commentsPage = commentsPage;
    }

    public Integer getTotal(){

        if (commentsPage == null){
            return 0;
        } else {
            return getCommentsPage().getPage().getTotal();
        }
    }

    public String getTotalInString(){
        return String.valueOf(getTotal());
    }
}


