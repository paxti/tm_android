package com.gwexhibits.timemachine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.gwexhibits.timemachine.adapters.OrderDetailsAdapter;
import com.gwexhibits.timemachine.objects.OrderObject;
import com.gwexhibits.timemachine.objects.TimeObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetails extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String CURRENT_ORDER = "current_order";

    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.subtitle) TextView subtitle;
    @Bind(R.id.list) RecyclerView mRecyclerView;
    @Bind(R.id.start_new_task) FloatingActionButton startNewTaskButton;

    private JSONObject order = null;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private OrderDetailsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);



        String account = "";
        String sfid = "Something went wrong";
        String show = "Contact your admin";

        try {
            String s = getIntent().getStringExtra("order");
            order = new JSONObject(s);
            account = order.getJSONObject("Account").getString("Name");
            sfid = order.getString(OrderObject.SFID);
            show = android.text.Html.fromHtml(order.getString(OrderObject.SHOW_NAME)).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        collapsingToolbar.setTitle("SFID: " + sfid);
        subtitle.setText(account + "@" + show);

        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        mAdapter = new OrderDetailsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    /*@OnClick(R.id.button5)
    public void start(Button button) {

        UserAccount account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        SmartStore smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);

//        smartStore.clearSoup(TimeObject.TIME_SUPE);

        JSONObject res = null;
        try {
            res = smartStore.create(TimeObject.TIME_SUPE, createNewRecord());
            Log.d("MAIN", "ID is: " + res);

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("currentTask", res.getString("_soupEntryId"));
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent mServiceIntent = new Intent(this, TimesSyncService.class);
        startService(mServiceIntent);

    }

    @OnClick(R.id.button6)
    public void stop(Button button) {
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String currentTask = prefs.getString("currentTask", null);
        Log.d("MAIN", "CURRENT task id is: " + currentTask);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));


        UserAccount account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        SmartStore smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);

        JSONArray objects = null;
        JSONObject object = null;
        try {
            objects = smartStore.retrieve(TimeObject.TIME_SUPE, Long.parseLong(currentTask));
            object = (JSONObject) objects.get(0);
            object.put(TimeObject.END_TIME, sdf.format(new Date()));
            object.put("__local__", true);
            object.put("__locally_updated__", true);
            smartStore.update(TimeObject.TIME_SUPE, object, Long.parseLong(currentTask));
            prefs.edit().remove("currentTask").commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent mServiceIntent = new Intent(this, TimesSyncService.class);
        mServiceIntent.putExtra("mode", "update");
        startService(mServiceIntent);

    }*/

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private JSONObject createNewRecord(){
        JSONObject object = new JSONObject();

        JSONObject additionalInfo = new JSONObject();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {

            additionalInfo.put("type", TimeObject.TIME_SF_OBJECT);

            object.put("Id", String.valueOf(System.currentTimeMillis()));

            object.put(TimeObject.NOTE, "Test note " + String.valueOf(System.currentTimeMillis()));
            object.put(TimeObject.START_TIME, sdf.format(new Date()));
            object.put(TimeObject.ORDER, order.getString("Id"));
            object.put("__local__", true);
            object.put("__locally_created__", true);
            object.put("__locally_updated__", false);
            object.put("__locally_deleted__", false);
            object.put("attributes", additionalInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return object;
    }

    @OnClick(R.id.start_new_task)
    public void startTask(View view) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        try {
            editor.putString(CURRENT_ORDER, order.getString("_soupEntryId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.commit();

    }


    private void hideStartNewTaskButton(){
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) startNewTaskButton.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        startNewTaskButton.setLayoutParams(p);
        startNewTaskButton.setVisibility(View.GONE);

        mAdapter.addElement(2, 0);
        mRecyclerView.scrollToPosition(0);
    }

    private void showStartNewTaskButton(){
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) startNewTaskButton.getLayoutParams();
        p.setAnchorId(R.id.app_bar);
        startNewTaskButton.setLayoutParams(p);
        startNewTaskButton.setVisibility(View.VISIBLE);

        mAdapter.removeElement(2);
        mRecyclerView.scrollToPosition(0);
    }

    private boolean isTaskRunning(){

        boolean isTaskRunning = false;

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getString(CURRENT_ORDER, "").length() > 1){
            isTaskRunning = true;
        }

        return isTaskRunning;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(CURRENT_ORDER)) {

            if(sharedPreferences.getString(CURRENT_ORDER, "").length() > 0){
                hideStartNewTaskButton();
            }else{
                showStartNewTaskButton();
            }
        }
    }

}
