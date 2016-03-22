package com.gwexhibits.timemachine.objects.sf;

import com.salesforce.androidsdk.smartstore.store.IndexSpec;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.manager.SyncManager;
import com.salesforce.androidsdk.smartsync.model.SalesforceObject;
import com.salesforce.androidsdk.smartsync.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by psyfu on 3/15/2016.
 */
public class PhotoObject extends SalesforceObject {

    public static final String PHOTOS_SUPE = "Photos";
    public static final String ORDER_SF_OBJECT = "Photo";

    public static final String PATH = "path";
    public static final String ORDER = "order_id";
    public static final String PHASE = "phase";
    public static final String DROPBOX_PATH = "dropbox_path";

    public static final String LOCAL = "__local__";
    public static final String LOCALY_CREATED = "__locally_created__";

    public static IndexSpec[] PHOTOS_INDEX_SPEC = {
            new IndexSpec(Constants.ID, SmartStore.Type.string),
            new IndexSpec(PATH, SmartStore.Type.string),
            new IndexSpec(ORDER, SmartStore.Type.string),
            new IndexSpec(PHASE, SmartStore.Type.string),
            new IndexSpec(DROPBOX_PATH, SmartStore.Type.string),
            new IndexSpec(LOCAL, SmartStore.Type.string)
    };

    private boolean isLocallyModified;

    /**
     * Parameterized constructor.
     *
     * @param object Raw data for object.
     */
    public PhotoObject(JSONObject object) {
        super(object);

        objectType = "PhotoObject";
        objectId = object.optString(Constants.ID);
        name = object.optString(PATH);

        isLocallyModified = object.optBoolean(SyncManager.LOCALLY_UPDATED) ||
                object.optBoolean(SyncManager.LOCALLY_CREATED) ||
                object.optBoolean(SyncManager.LOCALLY_DELETED);
    }

    public static JSONObject createRecord(String localPath, String dropboxPath, String phase,
                                          String order) throws JSONException{
        JSONObject record = new JSONObject();
        record.put(PATH, localPath);
        record.put(DROPBOX_PATH, dropboxPath);
        record.put(PHASE, phase);
        record.put(ORDER, order);
        record.put(LOCAL, true);
        record.put(LOCALY_CREATED, true);
        return record;
    }
}
