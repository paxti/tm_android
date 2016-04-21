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

import java.util.Date;

/**
 * Created by psyfu on 3/1/2016.
 */
public class TimeObject extends SalesforceObject {

    public static final String TIME_SUPE = "Times";
    public static final String TIME_SF_OBJECT = "Times__c";

    public static final String ORDER = "order__c";
    public static final String NOTE = "note__c";
    public static final String PHASE = "phase__c";
    public static final String START_TIME = "startTime__c";
    public static final String END_TIME = "endTime__c";

    public static final String OBJECT_TYPE_KEY = "type";
    public static final String ATTRIBUTES = "attributes";
    public static final String LOCAL = "__local__";
    public static final String LOCALY_CREATED = "__locally_created__";
    public static final String LOCALY_UPDATED = "__locally_updated__";
    public static final String LOCALY_DELETED = "__locally_deleted__";

    public static final String ORDER_STATUS = "order__r.Status";



    public static IndexSpec[] TIMES_INDEX_SPEC = {
            new IndexSpec(Constants.ID, SmartStore.Type.string),
            new IndexSpec(ORDER, SmartStore.Type.string),
            new IndexSpec(PHASE, SmartStore.Type.string),
            new IndexSpec(NOTE, SmartStore.Type.string),
            new IndexSpec(START_TIME, SmartStore.Type.string),
            new IndexSpec(END_TIME, SmartStore.Type.string),
            new IndexSpec(LOCAL, SmartStore.Type.string)
    };

    public static final String[] TIME_FIELDS_SYNC_UP = {
            ORDER,
            PHASE,
            NOTE,
            START_TIME,
            END_TIME,
    };

    public static final String[] TIME_FIELDS_UPDATE = {
            PHASE,
            NOTE,
            START_TIME,
            END_TIME,
    };

    public static final String[] TIME_FIELDS_SYNC_DOWN = {
            Constants.ID,
            PHASE,
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
        return "CreatedById='" +
                SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser().getUserId() +
                "'" + " AND " + ORDER_STATUS + " != " + "'Complited'";
    }

    public static JSONObject createTimeObjectStartedNow(String orderId, String phase) throws JSONException{
        JSONObject object = new JSONObject();

        JSONObject additionalInfo = new JSONObject();
        additionalInfo.put(OBJECT_TYPE_KEY, TIME_SF_OBJECT);

        object.put(Constants.ID, String.valueOf(System.currentTimeMillis()));
        object.put(START_TIME, Utils.getCurrentTimeInSfFormat(new Date()));
        object.put(ORDER, orderId);
        object.put(PHASE, phase);
        object.put(LOCAL, true);
        object.put(LOCALY_CREATED, true);
        object.put(LOCALY_UPDATED, false);
        object.put(LOCALY_DELETED, false);
        object.put(ATTRIBUTES, additionalInfo);

        return object;
    }


    public static JSONObject createTimeObjectStopedNow(JSONObject object) throws JSONException {
        object.put(TimeObject.END_TIME, Utils.getCurrentTimeInSfFormat(new Date()));
        object.put(LOCAL, true);
        object.put(LOCALY_UPDATED, true);

        return object;
    }
}
