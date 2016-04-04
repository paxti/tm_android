package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by psyfu on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatterBody implements Serializable {

    @JsonProperty("messageSegments")
    private List<ChatterSegment> segments;

    public ChatterBody(){

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

    public String getContent(){

        String res = "";

        for(ChatterSegment segment : segments){

            switch (segment.getType()){

                case "MarkupBegin": res += "<" + segment.getHtmlTag() + ">";
                    break;
                case  "MarkupEnd": res += "</" + segment.getHtmlTag() + "/>";
                    break;
                case "Text": res += segment.getText();
                    break;
                case "Mention": res += "<font color='blue'>" + segment.getText()+ "</font>";
                    break;
            }
        }

        return res;
    }
}
