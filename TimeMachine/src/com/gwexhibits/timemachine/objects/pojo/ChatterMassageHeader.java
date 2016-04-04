package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by psyfu on 4/7/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatterMassageHeader implements Serializable {

    @JsonProperty("messageSegments")
    private List<ChatterSegment> segments;

    @JsonProperty("text")
    private String text;

    public ChatterMassageHeader(){

    }

    public List<ChatterSegment> getSegments() {
        return segments;
    }

    public void setSegments(List<ChatterSegment> segments) {
        this.segments = segments;
    }

    public void addSegment(ChatterSegment segment){
        this.segments.add(segment);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTittle(){
        if (segments.size() == 1){
            return segments.get(0).getText();
        } else if (segments.size() == 2) {
            if (segments.get(1).getText().contains("The approval is at step 1 of 2")){
                return segments.get(0).getText() + " <small>requested approval</small>";
            } else {
                return segments.get(0).getText() + " <small>approved order</small>";
            }
        } else {
            return segments.get(0).getText() + " <small>changed status</small>";
        }
    }
}
