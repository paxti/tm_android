package com.gwexhibits.timemachine.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.gwexhibits.timemachine.OrderDetailsActivity;
import com.gwexhibits.timemachine.broadcast.TaskSyncAlarmReceiver;
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.gwexhibits.timemachine.utils.Utils;
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
    private static final Integer ALARM_SERVICE_CODE = 23145;
    private static final String TAG = TimesSyncService.class.getName();

    private UserAccount account;
    private SmartStore smartStore;
    private SyncManager syncMgr;
    private AlarmManager alarmMgr;

    public TimesSyncService() {
        super(TimesSyncService.class.getName());
        account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
        syncMgr = SyncManager.getInstance(account);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(Utils.isInternetAvailable(getApplicationContext())) {
            this.syncUp(intent);
        }else {
            showSnackbar("You are offline we will try to sync later");
            startSyncAlarmService();
        }
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

                if (sync.getStatus().equals(SyncState.Status.DONE)) {
                    stopSyncAlarmService();
                    showSnackbar("Times uploaded to SalesForce");
                    TimesSyncService.this.syncDown();
                }else if(sync.getStatus().equals(SyncState.Status.FAILED)){
                    showSnackbar("Something went wrong we will try to sync later");
                    startSyncAlarmService();
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
                    showSnackbar("Sync with SalesForce is completed");
                }
            }
        };
        try {
            final SyncOptions options = SyncOptions.optionsForSyncDown(SyncState.MergeMode.OVERWRITE);
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

    private void startSyncAlarmService(){
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent startTimeSyncService = new Intent(getApplicationContext(), TaskSyncAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), ALARM_SERVICE_CODE, startTimeSyncService, 0);

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                AlarmManager.INTERVAL_HOUR,
                AlarmManager.INTERVAL_HOUR,
                alarmIntent);
    }

    private void stopSyncAlarmService(){
        alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent startTimeSyncService = new Intent(getApplicationContext(), TaskSyncAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), ALARM_SERVICE_CODE, startTimeSyncService, 0);
        alarmMgr.cancel(alarmIntent);
    }

    private void showSnackbar (String message){
        Intent intent = new Intent(Utils.SYNC_BROADCAST_NAME);
        intent.putExtra(Utils.SYNC_BROADCAST_MESSAGE_KEY, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
