package com.gwexhibits.timemachine.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gwexhibits.timemachine.objects.OrderObject;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.smartstore.store.IndexSpec;
import com.salesforce.androidsdk.smartstore.store.QuerySpec;
import com.salesforce.androidsdk.smartstore.store.SmartSqlHelper;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;
import com.salesforce.androidsdk.smartsync.manager.SyncManager;
import com.salesforce.androidsdk.smartsync.util.SOQLBuilder;
import com.salesforce.androidsdk.smartsync.util.SoqlSyncDownTarget;
import com.salesforce.androidsdk.smartsync.util.SyncDownTarget;
import com.salesforce.androidsdk.smartsync.util.SyncOptions;
import com.salesforce.androidsdk.smartsync.util.SyncState;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iurii Tverezovskyi on 2/24/2016.
 */
public class OrderLoader extends AsyncTaskLoader<List<OrderObject>> {

    public static final String ORDER_SOUP = "Orders";
    public static final Integer LIMIT = 2000;
    public static final String LOAD_COMPLETE_INTENT_ACTION = "com.gwexhibits.timemachine.loaders.LIST_LOAD_COMPLETE";
    private static final String TAG = "SmartSyncExplorer: OrderLoader";
    private static IndexSpec[] ORDERS_INDEX_SPEC = {
            new IndexSpec("Id", SmartStore.Type.string),
            new IndexSpec(OrderObject.SFID, SmartStore.Type.string),
            new IndexSpec(OrderObject.SHOW_NAME, SmartStore.Type.string),
            new IndexSpec(OrderObject.CLIENT_NAME, SmartStore.Type.string),
            new IndexSpec(OrderObject.SHIPPING_DATE, SmartStore.Type.string),
            new IndexSpec(OrderObject.INSTRUCTIONS, SmartStore.Type.string),
            new IndexSpec(OrderObject.CONFIGURATION_NAME, SmartStore.Type.string),
            new IndexSpec(OrderObject.CONFIGURATION_TIME_PRE_STAGE, SmartStore.Type.string),
            new IndexSpec(OrderObject.CONFIGURATION_TIME_UP, SmartStore.Type.string),
            new IndexSpec(OrderObject.CONFIGURATION_TIME_DOWN, SmartStore.Type.string),
            new IndexSpec(OrderObject.CONFIGURATION_TIME_RI, SmartStore.Type.string)
    };

    private SmartStore smartStore;
    private SyncManager syncMgr;
    private long syncId = -1;

    /**
     * Parameterized constructor.
     *
     * @param context Context.
     * @param account User account.
     */
    public OrderLoader(Context context, UserAccount account) {
        super(context);
        smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
        syncMgr = SyncManager.getInstance(account);
    }

    @Override
    public List<OrderObject> loadInBackground() {
        if (!smartStore.hasSoup(ORDER_SOUP)) {
            return null;
        }
        final QuerySpec querySpec = QuerySpec.buildAllQuerySpec(ORDER_SOUP,
                OrderObject.SFID, QuerySpec.Order.ascending, LIMIT);
        JSONArray results = null;
        List<OrderObject> times = new ArrayList<OrderObject>();
        try {
            results = smartStore.query(querySpec, 0);
            for (int i = 0; i < results.length(); i++) {
                times.add(new OrderObject(results.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException occurred while parsing", e);
        } catch (SmartSqlHelper.SmartSqlException e) {
            Log.e(TAG, "SmartSqlException occurred while fetching data", e);
        }
        return times;
    }

    /**
     * Pulls the latest records from the server.
     */
    public synchronized void syncDown() {
        smartStore.registerSoup(OrderLoader.ORDER_SOUP, ORDERS_INDEX_SPEC);
        final SyncManager.SyncUpdateCallback callback = new SyncManager.SyncUpdateCallback() {

            @Override
            public void onUpdate(SyncState sync) {
                if (SyncState.Status.DONE.equals(sync.getStatus())) {
                    fireLoadCompleteIntent();
                }
            }
        };
        try {
            if (syncId == -1) {
                final SyncOptions options = SyncOptions.optionsForSyncDown(SyncState.MergeMode.LEAVE_IF_CHANGED);
                // IMPORTANT
                final String soqlQuery = SOQLBuilder.getInstanceWithFields(OrderObject.ORDER_FIELDS_SYNC_DOWN)
                        .from(ORDER_SOUP).limit(OrderLoader.LIMIT).build();
                final SyncDownTarget target = new SoqlSyncDownTarget(soqlQuery);
                final SyncState sync = syncMgr.syncDown(target, options,
                        OrderLoader.ORDER_SOUP, callback);
                syncId = sync.getId();
            } else {
                syncMgr.reSync(syncId, callback);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException occurred while parsing", e);
        } catch (SyncManager.SmartSyncException e) {
            Log.e(TAG, "SmartSyncException occurred while attempting to sync down", e);
        }
    }

    /**
     * Fires an intent notifying a registered receiver that fresh data is
     * available. This is for the special case where the data change has
     * been triggered by a background sync, even though the consuming
     * activity is in the foreground. Loaders don't trigger callbacks in
     * the activity unless the load has been triggered using a LoaderManager.
     */
    private void fireLoadCompleteIntent() {
        final Intent intent = new Intent(LOAD_COMPLETE_INTENT_ACTION);
        SalesforceSDKManager.getInstance().getAppContext().sendBroadcast(intent);
    }
}