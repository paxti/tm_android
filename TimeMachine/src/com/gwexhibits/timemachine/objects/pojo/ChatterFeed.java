package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by psyfu on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatterFeed implements Serializable {

    @JsonProperty("elements")
    private List<ChatterPost> chatterPosts;

    public ChatterFeed(){

    }

    public List<ChatterPost> getChatterchatterPosts() {
        return chatterPosts;
    }

    public ChatterPost getChatterEntry(int position){
        return chatterPosts.get(position);
    }

    public void setChatterchatterPosts(List<ChatterPost> chatterchatterPosts) {
        this.chatterPosts = chatterchatterPosts;
    }
}
