package com.gwexhibits.timemachine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.gwexhibits.timemachine.objects.OrderDetails;
import com.gwexhibits.timemachine.cards.OrderDetailsSections;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.cards.TaskStatusCard;
import com.gwexhibits.timemachine.objects.sf.PhotoObject;
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.gwexhibits.timemachine.services.DropboxService;
import com.gwexhibits.timemachine.services.TimesSyncService;
import com.gwexhibits.timemachine.utils.NotificationHelper;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class OrderDetailsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int STATUS_CARD_POSITION = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public static final String ORDER_KEY = "order";
    public static final String PHASE_KEY = "phase";

    @BindString(R.string.sfid_title) String sfidTitle;
    @BindString(R.string.app_name) String appName;
    @BindString(R.string.notification_subject) String notificationSubject;
    @BindString(R.string.notication_go_to_order) String notificationGoToOrder;
    @BindString(R.string.notification_stop) String notificationStop;

    @Bind(R.id.coordinator) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.subtitle) TextView subtitle;
    @Bind(R.id.start_new_task) FloatingActionButton startNewTaskButton;
    @Bind(R.id.camear) FloatingActionButton camera;
    @Bind(R.id.cards_recyclerview) CardRecyclerView recyclerView;

    private JSONObject currentOrder;
    private ArrayList<Card> cards = new ArrayList<>();
    private CardArrayRecyclerViewAdapter cardArrayAdapter;
    private File imageFile;
    DropboxAPI<AndroidAuthSession> mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        this.setPassedData();
        this.setTitles();

        cardArrayAdapter = new CardArrayRecyclerViewAdapter(this, cards);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideInLeftAnimator());

        if (recyclerView != null) {
            recyclerView.setAdapter(cardArrayAdapter);
        }

        this.loadDataFromDB();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(syncMessageReceiver, new IntentFilter(Utils.SYNC_BROADCAST_NAME));
    }

    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncMessageReceiver);
    }

    private void setPassedData(){
        try {
            currentOrder = new JSONObject(getIntent().getStringExtra(ORDER_KEY));
            if(getIntent().getStringExtra(PHASE_KEY) != null) {
                currentOrder.put(TimeObject.PHASE, getIntent().getStringExtra(PHASE_KEY));
            }
        } catch (JSONException e) {
            Utils.showSnackbar(coordinatorLayout, "Can't get information about this order");
        }
    }

    private void setTitles(){
        String account = this.getResources().getString(R.string.empty);
        String sfid = this.getResources().getString(R.string.error_message);
        String show = this.getResources().getString(R.string.empty);

        if (currentOrder != null){
            try {
                account = Utils.getStringValue(currentOrder, OrderObject.CLIENT_NAME);
                sfid = currentOrder.getString(OrderObject.SFID);
                show = android.text.Html.fromHtml(currentOrder.getString(OrderObject.SHOW_NAME)).toString();
            } catch (JSONException e) {
                Utils.showSnackbar(coordinatorLayout, "Couldn't output order information");
            }
        }

        collapsingToolbar.setTitle(sfidTitle + sfid);
        subtitle.setText(account + "@" + show);
    }

    private void loadDataFromDB(){

        int position = 0;

        try {
            OrderDetails details = new OrderDetails(currentOrder.getLong(SmartStore.SOUP_ENTRY_ID));

            if(Utils.isCurrentTaskRunning(this)){
                hideStartNewTaskButton();
            }

            for (OrderDetailsSections section : details.getDetailsSection()){
                if (section.getListItems().size() > 0) {
                    cards.add(section);
                    section.init();

                    cardArrayAdapter.notifyItemInserted(position);
                    position++;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.start_new_task)
    public void startTask(View view) {
        try {
            JSONObject newTaskEntry = TimeObject.createTimeObjectStartedNow(
                    currentOrder.getString(Constants.ID),
                    currentOrder.getString(TimeObject.PHASE));

            JSONObject createdTaskEntry = Utils.saveToSmartStore(TimeObject.TIME_SUPE, newTaskEntry);

            Utils.addCurrentTask(this, createdTaskEntry.getString(SmartStore.SOUP_ENTRY_ID));
            Utils.addCurrentOrder(this, currentOrder);
            NotificationHelper.createNotification(this, currentOrder);

        } catch (JSONException e) {
            Utils.showSnackbar(coordinatorLayout, "Wasn't able to create task");
        }
    }

    @OnClick(R.id.camear)
    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //TODO: Move from activity
        String path = Environment.getExternalStorageDirectory().getPath() + "/" +
                getApplicationContext().getPackageName() +
                "/data/photos";

        String fileName = null;
        try {
            fileName = currentOrder.getString(OrderObject.SFID) + "_" +
                    currentOrder.getString(TimeObject.PHASE) + "_" +
                    new Date().toString() +
                    ".jpg";

        } catch (JSONException e) {
            e.printStackTrace();
        }

        imageFile = new File(path, fileName);
        Uri uri = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 0);
    }

    private void hideStartNewTaskButton(){
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) startNewTaskButton.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        startNewTaskButton.setLayoutParams(p);
        startNewTaskButton.setVisibility(View.GONE);

        TaskStatusCard card = new TaskStatusCard(this, R.layout.order_details_status_card);
        addCardToPosition(card, STATUS_CARD_POSITION);
    }

    private void showStartNewTaskButton(){
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) startNewTaskButton.getLayoutParams();
        p.setAnchorId(R.id.app_bar);
        startNewTaskButton.setLayoutParams(p);

        startNewTaskButton.setVisibility(View.VISIBLE);
        removeCardFromPosition(STATUS_CARD_POSITION);
    }

    private void addCardToPosition(Card card, int position){
        cards.add(position, card);
        cardArrayAdapter.notifyItemInserted(position);
        recyclerView.scrollToPosition(position);
    }

    private void removeCardFromPosition(int position){
        cards.remove(position);
        cardArrayAdapter.notifyItemRemoved(position);
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Utils.CURRENT_ORDER)) {
            if(Utils.isCurrentTaskRunning(this)){
                hideStartNewTaskButton();
            }else{
                showStartNewTaskButton();

                Intent mServiceIntent = new Intent(getApplicationContext(), TimesSyncService.class);
                startService(mServiceIntent);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 0:
                switch(resultCode) {
                    case Activity.RESULT_OK:
                        if (imageFile.exists()) {
                            try {
                                FileInputStream inputStream = null;
                                inputStream = new FileInputStream(imageFile);

                                String path = imageFile.getAbsolutePath();
                                String fileName =  imageFile.getName();
                                String phase = "";
                                String order = "";
                                try {
                                    order = currentOrder.getString(Constants.ID);
                                    phase = currentOrder.getString(TimeObject.PHASE);

                                    Utils.saveToSmartStore(PhotoObject.PHOTOS_SUPE,
                                            PhotoObject.createRecord(path, "/test_tm/" + phase + "/" +fileName, phase, order));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Intent mServiceIntent = new Intent(getApplicationContext(), DropboxService.class);
                                startService(mServiceIntent);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            AlertDialog.Builder alert =
                                    new AlertDialog.Builder(this);
                            alert.setTitle("Error").setMessage(
                                    "Returned OK but image not created!").show();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    default:
                        Toast.makeText(this,
                                "Unexpected resultCode: " + resultCode,
                                Toast.LENGTH_LONG).show();
                }
                break;
            default:
                Toast.makeText(this,
                        "UNEXPECTED ACTIVITY COMPLETION",
                        Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private BroadcastReceiver syncMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.showSnackbar(intent, coordinatorLayout, Utils.SYNC_BROADCAST_MESSAGE_KEY);
        }
    };
}
