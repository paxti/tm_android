package com.gwexhibits.timemachine.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.objects.pojo.Views;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.gwexhibits.timemachine.serializers.OrderSerializer;
import com.gwexhibits.timemachine.services.OrdersSyncService;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by psyfu on 3/7/2016.
 */
public class Utils {

    public static final String SYNC_BROADCAST_NAME = "detailsBroadcast";
    public static final String SYNC_BROADCAST_MESSAGE_KEY = "sync_message";

    public static final String DROP_BOX_TOKEN = "drop_box_token";

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String CURRENT_ORDER = "current_order";
    public static final String CURRENT_TASK = "current_task";
    public static final String SF_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static SharedPreferences getSharedPreferences(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static Long getCurrentOrder(Context context){
        return Utils.getSharedPreferences(context).getLong(CURRENT_ORDER, -1);
    }

    public static Long getCurrentTask(Context context){
        return Utils.getSharedPreferences(context).getLong(CURRENT_TASK, -1);
    }

    public static Time getCurrentTaskObject(Context context){

        Time currentTask = null;
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader jsonReader = mapper.reader(Time.class);

        try {
            currentTask = (Time) jsonReader.readValue("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currentTask;
    }

    public static SharedPreferences.Editor getSharedPreferencesEditor(Context context){
        SharedPreferences settings = Utils.getSharedPreferences(context);
        return settings.edit();
    }

    public static boolean isCurrentTaskRunning(Context context){
        if (Utils.getCurrentTask(context) > 0){
            return true;
        }else {
            return false;
        }
    }

    public static void addCurrentOrder(Context context, JSONObject order){
        SharedPreferences.Editor editor =  Utils.getSharedPreferencesEditor(context);
        editor.putString(CURRENT_ORDER, order.toString());
        editor.commit();
    }

    public static void addCurrentTask(Context context, String task){
        SharedPreferences.Editor editor =  Utils.getSharedPreferencesEditor(context);
        editor.putString(CURRENT_TASK, task);
        editor.commit();
    }

    public static String getCurrentTimeInSfFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(SF_FORMAT, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = dateFormat.format(new Date());
        return dateFormat.format(new Date());
    }

    public static boolean isInternetAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static String saveToSmartStore(String soupName, Time task) throws JSONException {
        UserAccount account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        SmartStore smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);

        JSONObject object = smartStore.create(soupName, new JSONObject(timeObjectToStringNoOrder(task)));
        return object.getString(SmartStore.SOUP_ENTRY_ID);
    }

    public static void removeCurrentTaskAndOrder(Context context){
        SharedPreferences.Editor editor = Utils.getSharedPreferencesEditor(context);
        editor.remove(CURRENT_ORDER);
        editor.remove(CURRENT_TASK);
        editor.commit();
    }

    public static void stopCurrentTask(Context context) throws JSONException {
        UserAccount account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        SmartStore smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);

        Time currentTask = getCurrentTaskObject(context);
        currentTask.setEndTime(Utils.getCurrentTimeInSfFormat());

        smartStore.update(TimeObject.TIME_SUPE, new JSONObject(timeObjectToString(currentTask)),
                currentTask.getEntyId());

        Utils.removeCurrentTaskAndOrder(context);
    }

    public static void updateNote(Context context, String note) throws JSONException {
        UserAccount account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        SmartStore smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);

        Time currentTask = getCurrentTaskObject(context);
        currentTask.setNote(note);

        smartStore.update(TimeObject.TIME_SUPE, new JSONObject(timeObjectToString(currentTask)),
                currentTask.getEntyId());
        addCurrentTask(context, Utils.timeObjectToString(currentTask));
    }

    public static void showSnackbar(View view, String message){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void showSnackbar(Intent intent, View view, String messageKey){
        String message = intent.getStringExtra(messageKey);
        Utils.showSnackbar(view, message);
    }

    public static void saveDropBoxToken(Context context, String token){
        SharedPreferences.Editor editor =  Utils.getSharedPreferencesEditor(context);
        editor.putString(DROP_BOX_TOKEN, token);
        editor.commit();
    }

    public static String getDropBoxToken(Context context){
        return Utils.getSharedPreferences(context).getString(DROP_BOX_TOKEN, "");
    }

    public static boolean isDropBoxTokenSet(Context context){
        if (Utils.getSharedPreferences(context).getString(DROP_BOX_TOKEN, "").length() > 0 ){
            return true;
        }else {
            return false;
        }
    }

    public static String timeObjectToString(Time currentTask) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.writerWithView(Views.Full.class);
        JsonNode node = mapper.valueToTree(currentTask);
        return node.toString();
    }

    public static String timeObjectToStringNoOrder(Time currentTask) {

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Order.class, new OrderSerializer());

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.writerWithView(Views.SimpleOrder.class);
        mapper.registerModule(simpleModule);

        JsonNode node = mapper.valueToTree(currentTask);
        return node.toString();
    }


}
