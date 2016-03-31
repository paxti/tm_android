package com.gwexhibits.timemachine.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by psyfu on 3/18/2016.
 */
public class PreferencesManager {

    public static final String PREF_NAME = "com.gwexhibits.tm.preferences";

    public static final String CURRENT_TASK_KEY = "current_task";
    public static final String CURRENT_ORDER_KEY = "current_order";
    public static final String DROPBOX_TOKEN_KEY = "dropbox_token_key";
    public static final String FIRST_START_KEY = "first_start_key";

    private static PreferencesManager instance;
    private final SharedPreferences preferences;

    public static synchronized void initializeInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
    }

    public static synchronized PreferencesManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return instance;
    }

    private PreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setCurrents(long order, long task) {
        preferences.edit().putLong(CURRENT_ORDER_KEY, order).commit();
        preferences.edit().putLong(CURRENT_TASK_KEY, task).commit();
    }

    public void removeCurrent() {
        preferences.edit().remove(CURRENT_ORDER_KEY).commit();
        preferences.edit().remove(CURRENT_TASK_KEY).commit();
    }

    public long getCurrentTask() {
        return preferences.getLong(CURRENT_TASK_KEY, -1);
    }

    public long getCurrentOrder() {
        return preferences.getLong(CURRENT_ORDER_KEY, -1);
    }

    public boolean isCurrentTaskRunning(){
        if (getCurrentOrder() > 0){
            return true;
        }else {
            return false;
        }
    }

    public void saveDropBoxToken(String token){
        preferences.edit().putString(DROPBOX_TOKEN_KEY, token).commit();
    }

    public String getDropBoxToken(){
        return preferences.getString(DROPBOX_TOKEN_KEY, null);
    }

    public boolean isDropBoxTokenSet(){
        return getDropBoxToken() != null;
    }

    public Boolean getFirstStart(){
        return preferences.getBoolean(FIRST_START_KEY, true);
    }

    public boolean isFirstStart(){
        return getFirstStart() == null;
    }

    public void setFirstStart(boolean firstStart) {
        preferences.edit().putBoolean(FIRST_START_KEY, firstStart).commit();
    }
}
