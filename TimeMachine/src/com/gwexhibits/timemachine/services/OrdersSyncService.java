package com.gwexhibits.timemachine.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.gwexhibits.timemachine.objects.OrderObject;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.smartstore.store.IndexSpec;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;
import com.salesforce.androidsdk.smartsync.manager.SyncManager;
import com.salesforce.androidsdk.smartsync.util.SOQLBuilder;
import com.salesforce.androidsdk.smartsync.util.SoqlSyncDownTarget;
import com.salesforce.androidsdk.smartsync.util.SyncDownTarget;
import com.salesforce.androidsdk.smartsync.util.SyncOptions;
import com.salesforce.androidsdk.smartsync.util.SyncState;

import org.json.JSONException;

import java.net.URL;

/**
 * Created by psyfu on 2/24/2016.
 */
public class OrdersSyncService extends IntentService {

    private static final String ORDER_SUPE = "Orders";
    private static final String ORDER_SF_OBJECT = "Order";
    private static final Integer LIMIT = 100;
    private static final String TAG = "OrderSyncService";

    private static final String WORKORDER = "Workorder";
    private static final String SOS = "SoS";
    private static final String R_R = "R&R";
    private static final String CUSTOM_FUB = "Custom Fab Request";

    private static final String ORDER_TYPE = "Order_Type__c ";
    private static final String ORDER_SABMITED_STATUS = "Order_Submitted__c";
    private static final String ORDER_STATUS = "Status";

    private static final String ORDER_STATUS_DRATF = "Draft";
    private static final String ORDER_STATUS_COMPLITED = "Completed";


    public  static final String[] STATUS_NOT_TO_SYNC = {
            ORDER_STATUS_DRATF,
            ORDER_STATUS_COMPLITED
    };

    public static final String[] LIST_OF_ORDERS_TO_SYNC = {
            WORKORDER,
            SOS,
            R_R,
            CUSTOM_FUB
    };


    private UserAccount account;
    private SmartStore smartStore;
    private SyncManager syncMgr;
    private long syncId = -1;



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

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public OrdersSyncService() {
        super("name");
        account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
        syncMgr = SyncManager.getInstance(account);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        syncDown();
    }

    public synchronized void syncDown() {
        smartStore.registerSoup("Orders", ORDERS_INDEX_SPEC);
        final SyncManager.SyncUpdateCallback callback = new SyncManager.SyncUpdateCallback() {


            @Override
            public void onUpdate(SyncState sync) {
                Log.d(TAG, "Callback onUpdate");
            }
        };
        try {
            if (syncId == -1) {
                final SyncOptions options = SyncOptions.optionsForSyncDown(SyncState.MergeMode.OVERWRITE);
                // IMPORTANT
                final String soqlQuery = SOQLBuilder.getInstanceWithFields(OrderObject.ORDER_FIELDS_SYNC_DOWN)
                        .from(ORDER_SF_OBJECT).where(buildWhereRequest()).limit(LIMIT).build();
                final SyncDownTarget target = new SoqlSyncDownTarget(soqlQuery);
                final SyncState sync = syncMgr.syncDown(target, options, ORDER_SUPE, callback);
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

    private String buildWhereRequest(){
        return ORDER_TYPE + " IN ('" + TextUtils.join("','", LIST_OF_ORDERS_TO_SYNC) + "')"
                + " AND " +
                ORDER_SABMITED_STATUS + "=True" + " AND " +
                ORDER_STATUS + " NOT IN ('" + TextUtils.join("','", STATUS_NOT_TO_SYNC) + "')"
                ;
    }
}
