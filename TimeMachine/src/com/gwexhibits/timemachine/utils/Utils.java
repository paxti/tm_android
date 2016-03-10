package com.gwexhibits.timemachine.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by psyfu on 3/7/2016.
 */
public class Utils {

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String CURRENT_ORDER = "current_order";
    public static final String CURRENT_TASK = "current_task";
    public static final String SF_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static String getStringValue(JSONObject initialObject, String fieldName) throws JSONException {
        String value = "";
        String[] spitedFieldName = fieldName.split("\\.");
        if (spitedFieldName.length > 1){
            JSONObject object = initialObject.getJSONObject(spitedFieldName[0]);
            for (int i = 1; i < spitedFieldName.length - 1; i++){
                object = object.getJSONObject(spitedFieldName[i]);
            }
            value = object.getString(spitedFieldName[spitedFieldName.length -1]);
        }else{
            value = initialObject.getString(fieldName);
        }
        return value;
    }


    public static SharedPreferences getSharedPreferences(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static String getCurrentOrder(Context context){
        return Utils.getSharedPreferences(context).getString(CURRENT_ORDER, "");
    }

    public static Long getCurrentTask(Context context){
        return Utils.getSharedPreferences(context).getLong(CURRENT_TASK, 0);
    }

    public static SharedPreferences.Editor getSharedPreferencesEditor(Context context){
        SharedPreferences settings = Utils.getSharedPreferences(context);
        return settings.edit();
    }

    public static boolean isCurrentTaskRunning(Context context){
        if (Utils.getCurrentOrder(context).length() > 0 && Utils.getCurrentTask(context) > 0){
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

    public static void addCurrentTask(Context context, String Id){
        SharedPreferences.Editor editor =  Utils.getSharedPreferencesEditor(context);
        editor.putLong(CURRENT_TASK, Long.parseLong(Id));
        editor.commit();
    }

    public static String getCurrentTimeInSfFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(SF_FORMAT, Locale.US);
        return dateFormat.format(new Date());
    }

    public static boolean isInternetAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static JSONObject saveToSmartStore(String soupName, JSONObject newEntry) throws JSONException {
        UserAccount account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        SmartStore smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);

        smartStore.registerSoup(OrderObject.ORDER_SUPE, OrderObject.ORDERS_INDEX_SPEC);
        smartStore.registerSoup(TimeObject.TIME_SUPE, TimeObject.TIMES_INDEX_SPEC);

        return smartStore.create(soupName, newEntry);
    }

    public static void removeCurrentTaskAndOrder(Context context){
        SharedPreferences.Editor editor = Utils.getSharedPreferencesEditor(context);
        editor.remove(CURRENT_ORDER);
        editor.remove(CURRENT_TASK);
        editor.commit();
    }

    public static void stopCurrentTask(Context context, String note) throws JSONException {
        UserAccount account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        SmartStore smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);

        Long taskId = Utils.getCurrentTask(context);

        JSONObject entry = smartStore.retrieve(TimeObject.TIME_SUPE, taskId).getJSONObject(0);
        entry.put(TimeObject.END_TIME, getCurrentTimeInSfFormat());
        entry.put(TimeObject.NOTE, note);
        smartStore.update(TimeObject.TIME_SUPE, entry, taskId);

        Utils.removeCurrentTaskAndOrder(context);
    }
}
