package com.gwexhibits.timemachine;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.Button;

import com.gwexhibits.timemachine.listeners.SearchBarListener;
import com.gwexhibits.timemachine.services.OrdersSyncService;
import com.quinny898.library.persistentsearch.SearchBox;
import com.salesforce.androidsdk.accounts.UserAccountManager;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.security.PasscodeManager;
import com.salesforce.androidsdk.smartstore.ui.SmartStoreInspectorActivity;
import com.salesforce.androidsdk.util.EventsObservable;
import com.salesforce.androidsdk.util.UserSwitchReceiver;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity{

    private PasscodeManager passcodeManager;
    private UserSwitchReceiver userSwitchReceiver;

    private RestClient client;

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

}
