package com.gwexhibits.timemachine.objects.pojo;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by psyfu on 4/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatterSegment implements Serializable {

    private static final String TYPE_TEXT = "Text";
    private static final String TYPE_MENTION = "Mention";

    @JsonProperty("type")
    private String type;

    @JsonProperty("text")
    private String text;

    @JsonProperty("htmlTag")
    private String htmlTag;

    @JsonProperty("markupType")
    private String markupType;

    @Nullable @JsonProperty("id")
    private String id;

    public ChatterSegment(){

    }

    public ChatterSegment asText(String text){
        this.setText(text);
        this.setType(TYPE_TEXT);
        return this;
    }

    public ChatterSegment asMention(String id){
        this.setId(id);
        this.setType(TYPE_MENTION);
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHtmlTag() {
        return htmlTag;
    }

    public void setHtmlTag(String htmlTag) {
        this.htmlTag = htmlTag;
    }

    public String getMarkupType() {
        return markupType;
    }

    public void setMarkupType(String markupType) {
        this.markupType = markupType;
    }

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }
}
