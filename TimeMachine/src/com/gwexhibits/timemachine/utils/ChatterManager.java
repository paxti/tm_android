package com.gwexhibits.timemachine.utils;

import android.util.Log;

import com.gwexhibits.timemachine.HistoryAdapter;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by psyfu on 4/5/2016.
 */
public class ChatterManager {

    final private static String ORDER_FEED_URL = "/services/data/v36.0/chatter/feeds/record/%s/feed-elements";
    final private static String MY_FEED = "/services/data/v36.0/chatter/feeds/to/me/feed-elements";
    final private static String POST_URL = "/services/data/v36.0/chatter/feed-elements";
    final private static String COMMMENT_URL = "/services/data/v36.0/chatter/feed-elements/%s/capabilities/comments/items";

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

    private RestRequest buildPostRequest(String path, JSONObject object) throws UnsupportedEncodingException {
        StringEntity stringEntity = new StringEntity(object.toString(), HTTP.UTF_8);
        stringEntity.setContentType("application/json");
        return new RestRequest(RestRequest.RestMethod.POST, path, stringEntity);
    }

    public RestResponse postNewComment(JSONObject object, String orderId) throws IOException {

        return restClient.sendSync(buildPostRequest(String.format(COMMMENT_URL, orderId), object));
//        restClient.sendAsync(buildPostRequest(String.format(COMMMENT_URL, orderId), object), callback);
    }

}
