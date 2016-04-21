package com.gwexhibits.timemachine;

/**
 * Created by psyfu on 4/21/2016.
 */
public interface ChatterShowList  {

    public static final String FEED_ITEM = "FeedItem";
    public static final String COMMENT_ITEM = "Comment";

    public String getRecordId();
    public String getPostType();
    public void refreshList();

}
