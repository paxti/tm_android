package com.gwexhibits.timemachine.utils;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.pojo.Photo;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.objects.sf.PhotoObject;
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.smartstore.store.QuerySpec;
import com.salesforce.androidsdk.smartstore.store.SmartSqlHelper;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;
import com.salesforce.androidsdk.smartsync.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by psyfu on 3/18/2016.
 */
public class DbManager {

    final public static int PAGE_SIZE = 2000;

    private UserAccount account;
    private SmartStore smartStore;
    private ObjectMapper mapper;
    private Context context;

    private static DbManager ourInstance = new DbManager();

    public static DbManager getInstance() {
        return ourInstance;
    }

    private DbManager() {
        this.account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        this.smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
        this.context = SmartSyncSDKManager.getInstance().getAppContext();
        this.mapper = new ObjectMapper();
        PreferencesManager.initializeInstance(context);
    }

    public Time getTimeObject() throws JSONException, IOException {
        Long taskId = PreferencesManager.getInstance().getCurrentTask();
        return getTimeObject(taskId);
    }

    public Time getTimeObject(Long taskId) throws JSONException, IOException {
        mapper.reader(Time.class);
        ObjectReader jsonReader = mapper.reader(Time.class);
        return (Time) jsonReader.readValue(smartStore.retrieve(TimeObject.TIME_SUPE, taskId).getString(0));
    }

    public Order getOrderObject() throws JSONException, IOException {
        Long orderId = PreferencesManager.getInstance().getCurrentOrder();
        mapper.reader(Order.class);
        ObjectReader jsonReader = mapper.reader(Order.class);
        return (Order) jsonReader.readValue(smartStore.retrieve(OrderObject.ORDER_SUPE, orderId).getString(0));
    }

    public Order getOrderObject(Long orderId) throws JSONException, IOException {
        mapper.reader(Order.class);
        ObjectReader jsonReader = mapper.reader(Order.class);
        return (Order) jsonReader.readValue(smartStore.retrieve(OrderObject.ORDER_SUPE, orderId).getString(0));
    }

    public Order getOrderById(String id) throws JSONException, IOException {
        String getOrderRequest = String.format("SELECT {%1$s:%2$s} FROM {%1$s} where {%1$s:%3$s} = '%4$s'",
                OrderObject.ORDER_SUPE,
                SmartSqlHelper.SOUP,
                Constants.ID,
                id);

        mapper.reader(Order.class);
        ObjectReader jsonReader = mapper.reader(Order.class);

        String res = smartStore.query(QuerySpec.buildSmartQuerySpec(getOrderRequest, 1), 0)
                .getJSONArray(0).getString(0);

        return (Order) jsonReader.readValue(res);
    }

    public Time saveTime(Time timeEntry) throws JSONException, IOException {
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectReader jsonReader = mapper.reader(Time.class);

        JSONObject objectToSave = new JSONObject(mapper.valueToTree(timeEntry).toString());
        JSONObject savedObject = smartStore.create(TimeObject.TIME_SUPE, objectToSave);
        return (Time) jsonReader.readValue(savedObject.toString());
    }

    public Time updateTime(Time timeEntry) throws JSONException, IOException {
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectReader jsonReader = mapper.reader(Time.class);

        JSONObject objectToSave = new JSONObject(mapper.valueToTree(timeEntry).toString());
        JSONObject savedObject = smartStore.update(TimeObject.TIME_SUPE, objectToSave, timeEntry.getEntyId());
        return (Time) jsonReader.readValue(savedObject.toString());
    }

    public Time startTask(String orderId, String phase) throws JSONException, IOException{
        Time newEntry = new Time(orderId, phase).start();
        return saveTime(newEntry);
    }

    public Time stopTask() throws IOException, JSONException {
        Time currentTime = getTimeObject();
        currentTime.stop();
        return updateTime(currentTime);
    }

    public Time updateTimeNote(String note) throws IOException, JSONException {
        Time currentTime = getTimeObject();
        currentTime.setNote(note);
        return updateTime(currentTime);
    }

    public Photo savePhoto(Photo photoEntry) throws JSONException, IOException {
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectReader jsonReader = mapper.reader(Photo.class);

        JSONObject objectToSave = new JSONObject(mapper.valueToTree(photoEntry).toString());
        JSONObject savedObject = smartStore.create(PhotoObject.PHOTOS_SUPE, objectToSave);
        return (Photo) jsonReader.readValue(savedObject.toString());
    }

    public List<Photo> getAllNotUploadedPhotos() throws JSONException, IOException {
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        ObjectReader jsonReader = mapper.reader(Photo.class);

        JSONArray res = getAllInSoup(PhotoObject.PHOTOS_SUPE);

        List<Photo> photos = new ArrayList<>();

        for (int i = 0; i < res.length(); i++){
            photos.add((Photo) jsonReader.readValue(res.getJSONArray(i).getJSONObject(0).toString()));
        }

        return  photos;
    }

    public List<Time> getAllTimes() throws JSONException, IOException {
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        ObjectReader jsonReader = mapper.reader(Time.class);

        JSONArray res = getAllInSoup(TimeObject.TIME_SUPE);

        List<Time> times = new ArrayList<>();

        for (int i = 0; i < res.length(); i++){
            times.add((Time) jsonReader.readValue(res.getJSONArray(i).getJSONObject(0).toString()));
        }

        return  times;
    }

    private JSONArray getAllInSoup(String soup) throws JSONException, IOException {
        String getAllRequest = String.format("SELECT {%1$s:%2$s} FROM {%1$s}",
                soup,
                SmartSqlHelper.SOUP);
        return smartStore.query(QuerySpec.buildSmartQuerySpec(getAllRequest, PAGE_SIZE), 0);
    }

    public void deletePhoto(Photo photo){
        smartStore.delete(PhotoObject.PHOTOS_SUPE, photo.getEntyId());
    }
}
