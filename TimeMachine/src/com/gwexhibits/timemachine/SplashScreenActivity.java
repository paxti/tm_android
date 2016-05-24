package com.gwexhibits.timemachine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;

import com.dropbox.core.android.Auth;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.objects.sf.PhotoObject;
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.gwexhibits.timemachine.services.OrdersSyncService;
import com.gwexhibits.timemachine.utils.ChatterManager;
import com.gwexhibits.timemachine.utils.PreferencesManager;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;

public class SplashScreenActivity extends SalesforceDropboxActivity {

    private RestClient client;
    private UserAccount account;
    private SmartStore smartStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        PreferencesManager.initializeInstance(this);

        if (!PreferencesManager.getInstance().isDropBoxTokenSet() && Utils.isInternetAvailable(this)) {
            Auth.startOAuth2Authentication(this, getString(R.string.app_key));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        PreferencesManager.initializeInstance(this);

        if (PreferencesManager.getInstance().isDropBoxTokenSet() || !Utils.isInternetAvailable(this)) {

            if (!PreferencesManager.getInstance().getFirstStart()) {
                startMainActivity();
            }

            if (PreferencesManager.getInstance().isCurrentTaskRunning()) {
                Intent showOrderDetails = new Intent(this, OrderDetailsActivity.class);
                this.startActivity(showOrderDetails);
                finish();
            }
        }
    }

    @Override
    public void onResume(RestClient client) {
        this.client = client;

        ChatterManager.initializeInstance(client);

        if(PreferencesManager.getInstance().getFirstStart()) {
            account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
            smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
            smartStore.registerSoup(TimeObject.TIME_SUPE, TimeObject.TIMES_INDEX_SPEC);
            smartStore.registerSoup(OrderObject.ORDER_SUPE, OrderObject.ORDERS_INDEX_SPEC);
            smartStore.registerSoup(PhotoObject.PHOTOS_SUPE, PhotoObject.PHOTOS_INDEX_SPEC);

            LocalBroadcastManager.getInstance(this).registerReceiver(syncMessageReceiver, new IntentFilter(Utils.SYNC_BROADCAST_NAME));

            PreferencesManager.getInstance().setFirstStart(false);

            Intent mServiceIntent = new Intent(this, OrdersSyncService.class);
            startService(mServiceIntent);
        }
    }

    private BroadcastReceiver syncMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startMainActivity();
        }
    };

    private void startMainActivity(){
        Intent startMainActivity = new Intent(this, MainActivity.class);
        startMainActivity.setFlags(startMainActivity.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(startMainActivity);
        finish();
    }
}
