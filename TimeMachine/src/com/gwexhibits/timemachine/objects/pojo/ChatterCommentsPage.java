package com.gwexhibits.timemachine.objects.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by psyfu on 4/8/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatterCommentsPage implements Serializable {

    @JsonProperty("page")
    private ChatterPage page;

    public ChatterCommentsPage(){

    }

    public ChatterPage getPage() {
        return page;
    }

    public void setPage(ChatterPage page) {
        this.page = page;
    }
}
