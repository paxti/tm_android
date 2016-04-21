package com.gwexhibits.timemachine.utils;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by psyfu on 4/5/2016.
 */
public class ChatterManager {

    final private static String ORDER_FEED_URL = "/services/data/v36.0/chatter/feeds/record/%s/feed-elements";
    final private static String MY_FEED = "/services/data/v36.0/chatter/feeds/news/me/feed-elements";
    final private static String FEED_TO ="/services/data/v36.0/chatter/feeds/to/%s/feed-elements";
    final private static String FEED_ITEM_POST_URL = "/services/data/v36.0/chatter/feed-elements";
    final private static String COMMMENT_URL = "/services/data/v36.0/chatter/feed-elements/%s/capabilities/comments/items";
    final private static String MENTIONS_URL = "/services/data/v36.0/chatter/mentions/completions?contextId=%s&q=%s";
    final private static String MENTIONS_URL_NO_CONTEXT = "/services/data/v36.0/chatter/mentions/completions?q=%s";

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

    public void getPostComments(String orderId, RestClient.AsyncRequestCallback callback) throws IOException {
        restClient.sendAsync(buildGetRequest(String.format(COMMMENT_URL, orderId)), callback);
    }

    public RestResponse getFromUrl(String url) throws IOException {
        return sendChatterRequest(buildGetRequest(url));
    }

    public void getFromUrlAsync(String url,  RestClient.AsyncRequestCallback callback) {
        sendChatterRequest(buildGetRequest(url), callback);
    }

    public RestResponse getMentions(String feedContext, String text) throws IOException {
        if (feedContext != null) {
            return sendChatterRequest(buildGetRequest(String.format(MENTIONS_URL, feedContext, text)));
        } else {
            return sendChatterRequest(buildGetRequest(String.format(MENTIONS_URL_NO_CONTEXT, text)));
        }
    }

    public RestResponse getFeed() throws IOException {
        return sendChatterRequest(buildGetRequest(MY_FEED));
    }

    public RestResponse getFeed(String orderId) throws IOException {
        return sendChatterRequest(buildGetRequest(String.format(ORDER_FEED_URL, orderId)));
    }

    public void getFeedTo(String orderId, RestClient.AsyncRequestCallback callback){
        sendChatterRequest(buildGetRequest(String.format(FEED_TO, orderId)), callback);
    }

    public void getFeed(String orderId, RestClient.AsyncRequestCallback callback){
        sendChatterRequest(buildGetRequest(String.format(ORDER_FEED_URL, orderId)), callback);
    }

    public void getFeed(RestClient.AsyncRequestCallback callback){
        sendChatterRequest(buildGetRequest(MY_FEED), callback);
    }

    private RestResponse sendChatterRequest(RestRequest restRequest) throws IOException {
        return restClient.sendSync(restRequest);
    }

    private void sendChatterRequest(RestRequest restRequest, RestClient.AsyncRequestCallback callback) {
        restClient.sendAsync(restRequest, callback);
    }

    private RestRequest buildGetRequest(String path){
        return new RestRequest(RestRequest.RestMethod.GET, path, null);
    }

    private RestRequest buildPostRequest(String path, String object) throws UnsupportedEncodingException {
        StringEntity stringEntity = new StringEntity(object, HTTP.UTF_8);
        stringEntity.setContentType("application/json");
        return new RestRequest(RestRequest.RestMethod.POST, path, stringEntity);
    }

    public void postNewComment(String object, String orderId, RestClient.AsyncRequestCallback callback) throws IOException {
        restClient.sendAsync(buildPostRequest(String.format(COMMMENT_URL, orderId), object), callback);
    }

    public RestResponse postNewFeedItem(String object) throws IOException {
        return  restClient.sendSync(buildPostRequest(FEED_ITEM_POST_URL, object));
    }

    public void postNewFeedItem(String object, RestClient.AsyncRequestCallback callback) throws IOException {
        restClient.sendAsync(buildPostRequest(FEED_ITEM_POST_URL, object), callback);
    }

}
