package com.gwexhibits.timemachine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.baoyz.widget.PullRefreshLayout;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gwexhibits.timemachine.listeners.SearchBarListener;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.services.DropboxService;
import com.gwexhibits.timemachine.services.OrdersSyncService;
import com.gwexhibits.timemachine.utils.PreferencesManager;
import com.gwexhibits.timemachine.utils.Utils;
import com.quinny898.library.persistentsearch.SearchBox;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.accounts.UserAccountManager;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.security.PasscodeManager;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartstore.ui.SmartStoreInspectorActivity;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;
import com.salesforce.androidsdk.smartsync.manager.SyncManager;
import com.salesforce.androidsdk.util.EventsObservable;
import com.salesforce.androidsdk.util.UserSwitchReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity{

    private PasscodeManager passcodeManager;
    private UserSwitchReceiver userSwitchReceiver;

    private RestClient client;
    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Bind(R.id.swipeRefreshLayout) PullRefreshLayout swipeRefreshLayout;
    @Bind(R.id.main_relative) RelativeLayout relativeLayout;
    @Bind(R.id.searchbox) SearchBox search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gets an instance of the passcode manager.
        passcodeManager = SalesforceSDKManager.getInstance().getPasscodeManager();
        userSwitchReceiver = new ActivityUserSwitchReceiver();
        registerReceiver(userSwitchReceiver, new IntentFilter(UserAccountManager.USER_SWITCH_INTENT_ACTION));

        // Lets observers know that activity creation is complete.
        EventsObservable.get().notifyEvent(EventsObservable.EventType.MainActivityCreateComplete, this);

        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        AndroidAuthSession session = buildSession();
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                Intent mServiceIntent = new Intent(getApplicationContext(), OrdersSyncService.class);
                startService(mServiceIntent);
            }
        });
        swipeRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        search.setInputType(InputType.TYPE_CLASS_NUMBER);
        search.setSearchListener(new SearchBarListener(search, this));
        search.setMaxLength(10);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Brings up the passcode screen if needed.
        if (passcodeManager.onResume(this)) {

            // Gets login options.
            final String accountType = SalesforceSDKManager.getInstance().getAccountType();
            final ClientManager.LoginOptions loginOptions = SalesforceSDKManager.getInstance().getLoginOptions();
            AndroidAuthSession session = mDBApi.getSession();
            // Gets a rest client.
            new ClientManager(this, accountType, loginOptions,
                    SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {

                @Override
                public void authenticatedRestClient(RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(SearchActivity.this);
                        return;
                    }
                    onResume(client);

                    // Lets observers know that rendition is complete.
                    EventsObservable.get().notifyEvent(EventsObservable.EventType.RenditionComplete);
                }
            });
        }

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                Utils.saveDropBoxToken(this, mDBApi.getSession().getOAuth2AccessToken());
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(syncMessageReceiver, new IntentFilter(Utils.SYNC_BROADCAST_NAME));
        PreferencesManager.initializeInstance(this);
        if (PreferencesManager.getInstance().isCurrentTaskRunning()){
            Intent showOrderDetails = new Intent(SearchActivity.this, OrderDetailsActivity.class);
            this.startActivity(showOrderDetails);
        }
    }

    public void onResume(RestClient client) {
        this.client = client;
    }

    @Override
    public void onUserInteraction() {
        passcodeManager.recordUserInteraction();
    }

    @Override
    public void onPause() {
        super.onPause();
        passcodeManager.onPause(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncMessageReceiver);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(userSwitchReceiver);
        super.onDestroy();
    }

    /**
     * Refreshes the client if the user has been switched.
     */
    protected void refreshIfUserSwitched() {
        if (passcodeManager.onResume(this)) {

            // Gets login options.
            final String accountType = SalesforceSDKManager.getInstance().getAccountType();
            final ClientManager.LoginOptions loginOptions = SalesforceSDKManager.getInstance().getLoginOptions();

            // Gets a rest client.
            new ClientManager(this, accountType, loginOptions,
                    SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {

                @Override
                public void authenticatedRestClient(RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(SearchActivity.this);
                        return;
                    }
                    onResume(client);

                    // Lets observers know that rendition is complete.
                    EventsObservable.get().notifyEvent(EventsObservable.EventType.RenditionComplete);
                }
            });
        }
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(DropboxService.APP_KEY, DropboxService.APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    private void loadAuth(AndroidAuthSession session) {

        if(Utils.isDropBoxTokenSet(this)){
            session.setOAuth2AccessToken(Utils.getDropBoxToken(this));
        }else{
            AppKeyPair appKeys = new AppKeyPair(DropboxService.APP_KEY, DropboxService.APP_SECRET);
            session = new AndroidAuthSession(appKeys);
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            mDBApi.getSession().startOAuth2Authentication(SearchActivity.this);
        }
    }

    /**
     * Acts on the user switch event.
     *
     * @author bhariharan
     */
    private class ActivityUserSwitchReceiver extends UserSwitchReceiver {
        @Override
        protected void onUserSwitch() {
            refreshIfUserSwitched();
        }
    }

    private BroadcastReceiver syncMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.showSnackbar(intent, relativeLayout, Utils.SYNC_BROADCAST_MESSAGE_KEY);
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    @OnClick(R.id.button)
     public void sayHi(Button button) {
        Intent mServiceIntent = new Intent(this, OrdersSyncService.class);
        startService(mServiceIntent);
    }

    @OnClick(R.id.button2)
    public void button2Clicked(Button button) {
        final Intent i = new Intent(this, SmartStoreInspectorActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.button3)
    public void button3Clicked(Button button) {
        UserAccount account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        SmartStore smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
        SyncManager syncMgr = SyncManager.getInstance(account);

        try {
            JSONArray array = smartStore.retrieve(OrderObject.ORDER_SUPE, (long) 1, (long) 2, (long) 3, (long) 4, (long) 5);

            for(int i = 0; i < array.length(); i++ ){

                JSONObject object = array.getJSONObject(i);

                ObjectMapper mapper = new ObjectMapper();
                final ObjectReader r = mapper.reader(Order.class);
                Order user = r.readValue(object.toString());
                String sfid = user.getSfid();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("ERROR", e.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
