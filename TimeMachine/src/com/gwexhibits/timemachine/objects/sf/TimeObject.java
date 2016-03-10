package com.gwexhibits.timemachine.objects.sf;


import android.text.TextUtils;
import android.util.Log;

import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.smartstore.store.IndexSpec;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;
import com.salesforce.androidsdk.smartsync.manager.SyncManager;
import com.salesforce.androidsdk.smartsync.model.SalesforceObject;
import com.salesforce.androidsdk.smartsync.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by psyfu on 3/1/2016.
 */
public class TimeObject extends SalesforceObject {

    public static final String TIME_SUPE = "Times";
    public static final String TIME_SF_OBJECT = "Times__c";

    public static final String ORDER = "order__c";
    public static final String NOTE = "note__c";
    public static final String START_TIME = "startTime__c";
    public static final String END_TIME = "endTime__c";
    public static final String LOCAL = "__local__";


    public static IndexSpec[] TIMES_INDEX_SPEC = {
            new IndexSpec("Id", SmartStore.Type.string),
            new IndexSpec(ORDER, SmartStore.Type.string),
            new IndexSpec(NOTE, SmartStore.Type.string),
            new IndexSpec(START_TIME, SmartStore.Type.string),
            new IndexSpec(END_TIME, SmartStore.Type.string),
            new IndexSpec(LOCAL, SmartStore.Type.string)
    };

    public static final String[] TIME_FIELDS_SYNC_UP = {
            ORDER,
            NOTE,
            START_TIME,
            END_TIME,
    };

    public static final String[] TIME_FIELDS_UPDATE = {
            NOTE,
            START_TIME,
            END_TIME,
    };

    public static final String[] TIME_FIELDS_SYNC_DOWN = {
            Constants.ID,
            ORDER,
            NOTE,
            START_TIME,
            END_TIME,
            Constants.LAST_MODIFIED_DATE
    };

    private boolean isLocallyModified;


    /**
     * Parameterized constructor.
     *
     * @param object Raw data for object.
     */
    public TimeObject(JSONObject object) {
        super(object);


        objectType = "TimeObject";
        objectId = object.optString(Constants.ID);
        name = object.optString(ORDER) + " " + object.optString(Constants.ID) ;

        isLocallyModified = object.optBoolean(SyncManager.LOCALLY_UPDATED) ||
                object.optBoolean(SyncManager.LOCALLY_CREATED) ||
                object.optBoolean(SyncManager.LOCALLY_DELETED);
    }

    /**
     * Returns whether the times has been locally modified or not.
     *
     * @return True - if the contact has been locally modified, False - otherwise.
     */
    public boolean isLocallyModified() {
        return isLocallyModified;
    }

    private String sanitizeText(String text) {
        if (TextUtils.isEmpty(text) || text.equals(Constants.NULL_STRING)) {
            return Constants.EMPTY_STRING;
        }
        return text;
    }

    public static String buildWhereRequest(){
        Log.d("TIME OBJECT", SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser().getUserId());
        return "CreatedById='" +
                SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser().getUserId() +
                "'";
    }

    public static JSONObject createTimeObjectStartedNow(String orderId) throws JSONException{
        JSONObject object = new JSONObject();

        JSONObject additionalInfo = new JSONObject();


        additionalInfo.put("type", TIME_SF_OBJECT);
        object.put("Id", String.valueOf(System.currentTimeMillis()));
        object.put(START_TIME, Utils.getCurrentTimeInSfFormat());
        object.put(ORDER, orderId);
        object.put("__local__", true);
        object.put("__locally_created__", true);
        object.put("__locally_updated__", false);
        object.put("__locally_deleted__", false);
        object.put("attributes", additionalInfo);

        return object;
    }

    public static JSONObject createTimeObjectStopedNow(JSONObject object) throws JSONException {
        object.put(TimeObject.END_TIME, Utils.getCurrentTimeInSfFormat());
        object.put("__local__", true);
        object.put("__locally_updated__", true);

        return object;
    }
}
