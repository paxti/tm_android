package com.gwexhibits.timemachine.utils;

import android.util.Log;

import com.gwexhibits.timemachine.HistoryAdapter;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

/**
 * Created by psyfu on 4/5/2016.
 */
public class ChatterManager {

    final private static String ORDER_FEED_URL = "/services/data/v36.0/chatter/feeds/record/%s/feed-elements";
    final private static String MY_FEED = "/services/data/v36.0/chatter/feeds/to/me/feed-elements";

    private static ChatterManager instance;
    private final RestClient restClient;

    public static synchronized void initializeInstance(RestClient restClient) {
        if (instance == null) {
            instance = new ChatterManager(restClient);
        }
    }

    public static synchronized ChatterManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(ChatterManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return instance;
    }

    private ChatterManager(RestClient client) {
        restClient = client;
    }

    public void getDataFromUrl(String url, RestClient.AsyncRequestCallback callback){
        sendChatterRequest(buildGetRequest(url), callback);
    }

    public void getFeed(String orderId, RestClient.AsyncRequestCallback callback){
        sendChatterRequest(buildGetRequest(String.format(ORDER_FEED_URL, orderId)), callback);
    }

    public void getFeed(RestClient.AsyncRequestCallback callback){
        sendChatterRequest(buildGetRequest(MY_FEED), callback);
    }

    private void sendChatterRequest(RestRequest restRequest, RestClient.AsyncRequestCallback callback) {
        restClient.sendAsync(restRequest, callback);
    }

    private RestRequest buildGetRequest(String path){
        return new RestRequest(RestRequest.RestMethod.GET, path, null);
    }

}
