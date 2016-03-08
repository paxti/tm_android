package com.gwexhibits.timemachine.utils;

import android.content.Context;
import android.content.SharedPreferences;

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

    public static SharedPreferences.Editor getSharedPreferencesEditor(Context context){
        SharedPreferences settings = Utils.getSharedPreferences(context);
        return settings.edit();
    }

    public static boolean isCurrentTaskRunning(Context context){
        String set = Utils.getSharedPreferences(context).getString(CURRENT_ORDER, "");
        if (Utils.getSharedPreferences(context).getString(CURRENT_ORDER, "").length() >= 1){
            return true;
        }

        return false;
    }

    public static void addCurrentTask(Context context, String Id){
        SharedPreferences.Editor editor =  Utils.getSharedPreferencesEditor(context);
        editor.putString(CURRENT_ORDER, Id);
        editor.commit();
    }

    public static void removeCurrentTask(Context context){
        SharedPreferences.Editor editor =  Utils.getSharedPreferencesEditor(context);
        editor.remove(CURRENT_ORDER);
        editor.commit();
    }

    public static String getCurrentTimeInSfFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(SF_FORMAT, Locale.US);
        return dateFormat.format(new Date());
    }
}
