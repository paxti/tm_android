package com.gwexhibits.timemachine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.baoyz.widget.PullRefreshLayout;
import com.dropbox.core.android.Auth;
import com.gwexhibits.timemachine.listeners.SearchBarListener;
import com.gwexhibits.timemachine.services.DropboxService;
import com.gwexhibits.timemachine.services.OrdersSyncService;
import com.gwexhibits.timemachine.utils.PreferencesManager;
import com.gwexhibits.timemachine.utils.Utils;
import com.quinny898.library.persistentsearch.SearchBox;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.smartstore.ui.SmartStoreInspectorActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends SalesforceDropboxActivity {

    private RestClient client;

    @Bind(R.id.swipeRefreshLayout) PullRefreshLayout swipeRefreshLayout;
    @Bind(R.id.main_relative) RelativeLayout relativeLayout;
    @Bind(R.id.searchbox) SearchBox search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                Intent mServiceIntent = new Intent(getApplicationContext(), OrdersSyncService.class);
                startService(mServiceIntent);
            }
        });

        PreferencesManager.initializeInstance(this);
        if(!PreferencesManager.getInstance().isDropBoxTokenSet()) {
            Auth.startOAuth2Authentication(SearchActivity.this, getString(R.string.app_key));
        }

        swipeRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        search.setInputType(InputType.TYPE_CLASS_NUMBER);
        search.setSearchListener(new SearchBarListener(search, this));
        search.setMaxLength(10);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(syncMessageReceiver, new IntentFilter(Utils.SYNC_BROADCAST_NAME));
        if (PreferencesManager.getInstance().isCurrentTaskRunning()){
            Intent showOrderDetails = new Intent(SearchActivity.this, OrderDetailsActivity.class);
            this.startActivity(showOrderDetails);
            finish();
        }
    }

    public void onResume(RestClient client) {
        this.client = client;
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
        Intent mServiceIntent = new Intent(this, DropboxService.class);
        startService(mServiceIntent);
    }
}
