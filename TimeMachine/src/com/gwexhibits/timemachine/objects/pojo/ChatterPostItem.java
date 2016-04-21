package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by psyfu on 4/19/2016.
 */
public class ChatterPostItem {

    @JsonProperty("feedElementType")
    private String elementType;

    @JsonIgnoreProperties("subjectId")
    private String subjectId;

    @JsonProperty("body")
    private ChatterBody chatterBody;

    public ChatterPostItem(){

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

    public ChatterBody getChatterBody() {
        return chatterBody;
    }

    public void setChatterBody(ChatterBody chatterBody) {
        this.chatterBody = chatterBody;
    }
}
