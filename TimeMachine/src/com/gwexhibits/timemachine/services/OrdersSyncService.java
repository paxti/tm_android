package com.gwexhibits.timemachine.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.gwexhibits.timemachine.objects.sf.OrderObject;
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
    private static final String TAG = "OrderSyncService";


    private UserAccount account;
    private SmartStore smartStore;
    private SyncManager syncMgr;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public OrdersSyncService() {
        super("OrdersSyncService");
        account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
        syncMgr = SyncManager.getInstance(account);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        syncDown();
    }

    public synchronized void syncDown() {
        smartStore.registerSoup(OrderObject.ORDER_SUPE, OrderObject.ORDERS_INDEX_SPEC);
        final SyncManager.SyncUpdateCallback callbackSync = new SyncManager.SyncUpdateCallback() {

            @Override
            public void onUpdate(SyncState sync) {
                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(OrdersSyncService.this.getApplicationContext(), "Updating data", Toast.LENGTH_SHORT).show();
                    }
                });
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

}
