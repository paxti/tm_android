package com.gwexhibits.timemachine.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;
import com.salesforce.androidsdk.smartsync.manager.SyncManager;
import com.salesforce.androidsdk.smartsync.util.SOQLBuilder;
import com.salesforce.androidsdk.smartsync.util.SoqlSyncDownTarget;
import com.salesforce.androidsdk.smartsync.util.SyncDownTarget;
import com.salesforce.androidsdk.smartsync.util.SyncOptions;
import com.salesforce.androidsdk.smartsync.util.SyncState;
import com.salesforce.androidsdk.smartsync.util.SyncUpTarget;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by psyfu on 3/1/2016.
 */
public class TimesSyncService extends IntentService {

    private static final Integer LIMIT = 10000;
    private static final String TAG = "TimeSyncService";

    private UserAccount account;
    private SmartStore smartStore;
    private SyncManager syncMgr;

    public TimesSyncService() {
        super("TimesSyncService");
        account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
        syncMgr = SyncManager.getInstance(account);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        syncUp(intent);
    }

    /**
     * Pushes local changes up to the server.
     */
    public synchronized void syncUp(Intent intent) {


        List<String> listOfFields = Arrays.asList(TimeObject.TIME_FIELDS_SYNC_UP);
        if (intent.getStringExtra("mode")!= null){
            listOfFields = Arrays.asList(TimeObject.TIME_FIELDS_UPDATE);
        }

        smartStore.registerSoup(TimeObject.TIME_SUPE, TimeObject.TIMES_INDEX_SPEC);
        final SyncUpTarget target = new SyncUpTarget();
        final SyncOptions options = SyncOptions.optionsForSyncUp(listOfFields,
                SyncState.MergeMode.OVERWRITE);

        final SyncManager.SyncUpdateCallback callback = new SyncManager.SyncUpdateCallback() {

            @Override
            public void onUpdate(SyncState sync) {
                if (SyncState.Status.DONE.equals(sync.getStatus())) {
                    syncDown();
                    Log.d(TAG, "UPLOAD COMPLITED");
                }
            }
        };

        try {
            syncMgr.syncUp(target, options, TimeObject.TIME_SUPE, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException occurred while parsing", e);
        } catch (SyncManager.SmartSyncException e) {
            Log.e(TAG, "SmartSyncException occurred while attempting to sync up", e);
        }
    }

    /**
     * Pulls the latest records from the server.
     */
    public synchronized void syncDown() {
        final SyncManager.SyncUpdateCallback callback = new SyncManager.SyncUpdateCallback() {

            @Override
            public void onUpdate(SyncState sync) {
                if (SyncState.Status.DONE.equals(sync.getStatus())) {
                    Log.d(TAG, "DOWNLOAD COMPLITED");
                }
            }
        };
        try {
            final SyncOptions options = SyncOptions.optionsForSyncDown(SyncState.MergeMode.OVERWRITE);
            // IMPORTANT
            final String soqlQuery = SOQLBuilder.getInstanceWithFields(TimeObject.TIME_FIELDS_SYNC_DOWN)
                    .from(TimeObject.TIME_SF_OBJECT)
                    .where(TimeObject.buildWhereRequest())
                    .limit(LIMIT).build();
            final SyncDownTarget target = new SoqlSyncDownTarget(soqlQuery);
            syncMgr.syncDown(target, options, TimeObject.TIME_SUPE, callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException occurred while parsing", e);
        } catch (SyncManager.SmartSyncException e) {
            Log.e(TAG, "SmartSyncException occurred while attempting to sync down", e);
        }
    }


}
