package com.gwexhibits.timemachine.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
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

import org.json.JSONException;

/**
 * Created by psyfu on 2/24/2016.
 */
public class OrdersSyncService extends IntentService {

    private static final Integer LIMIT = 10000;
    private static final String TAG = OrdersSyncService.class.getName();


    private UserAccount account;
    private SmartStore smartStore;
    private SyncManager syncMgr;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public OrdersSyncService() {
        super(OrdersSyncService.class.getName());
        account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
        syncMgr = SyncManager.getInstance(account);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(Utils.isInternetAvailable(getApplicationContext())) {
            showToast(this, getString(R.string.toast_started_order_service));
            this.syncDown();
        }else {
            showToast(this, getString(R.string.you_are_offline));
        }
    }

    public synchronized void syncDown() {
        final SyncManager.SyncUpdateCallback callbackSync = new SyncManager.SyncUpdateCallback() {

            @Override
            public void onUpdate(SyncState sync) {
                if(SyncState.Status.DONE.equals(sync.getStatus())){
                    showToast(getApplication(), getString(R.string.toast_finished_order_service));
                }else if(SyncState.Status.FAILED.equals(sync.getStatus())){
                    showToast(getApplication(), getString(R.string.error_message));
                }
            }
        };

        try {
            final SyncOptions options = SyncOptions.optionsForSyncDown(SyncState.MergeMode.OVERWRITE);
            // IMPORTANT
            final String soqlQuery = SOQLBuilder.getInstanceWithFields(OrderObject.ORDER_FIELDS_SYNC_DOWN)
                    .from(OrderObject.ORDER_SF_OBJECT).where(OrderObject.buildWhereRequest()).limit(LIMIT).build();
            final SyncDownTarget target = new SoqlSyncDownTarget(soqlQuery);
            syncMgr.syncDown(target, options, OrderObject.ORDER_SUPE, callbackSync);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException occurred while parsing", e);
        } catch (SyncManager.SmartSyncException e) {
            Log.e(TAG, "SmartSyncException occurred while attempting to sync down", e);
        }
    }

    private void showToast(final Context context, final String text){
        Handler h = new Handler(context.getMainLooper());

        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
