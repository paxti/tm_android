package com.gwexhibits.timemachine.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
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

    public static final String PHOTOS_PATH = "/data/photos/";

    public static SharedPreferences getSharedPreferences(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return sharedPreferences;
    }


    public static SharedPreferences.Editor getSharedPreferencesEditor(Context context){
        SharedPreferences settings = Utils.getSharedPreferences(context);
        return settings.edit();
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






    public static void showSnackbar(View view, String message){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void showSnackbar(Intent intent, View view, String messageKey){
        String message = intent.getStringExtra(messageKey);
        Utils.showSnackbar(view, message);
    }


    public static String getPhotosPath(Context context){
        return Environment.getExternalStorageDirectory().getPath() + "/" +
                context.getPackageName() + PHOTOS_PATH;
    }

    public static String buildPhotosName(){
        return System.currentTimeMillis() + ".jpg";
    }

}
