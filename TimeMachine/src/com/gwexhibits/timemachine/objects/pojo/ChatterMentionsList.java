package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by psyfu on 4/14/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatterMentionsList implements Serializable {

    @JsonProperty("mentionCompletions")
    private List<ChatterMention> mentions;

    public ChatterMentionsList(){

    }

    public List<ChatterMention> getMentions() {
        return mentions;
    }

    public void setMentions(List<ChatterMention> mentions) {
        this.mentions = mentions;
    }
}
