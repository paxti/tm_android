package com.gwexhibits.timemachine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gwexhibits.timemachine.async.DropboxUploader;
import com.gwexhibits.timemachine.cards.OrderDetailsSections;
import com.gwexhibits.timemachine.cards.TaskStatusCard;
import com.gwexhibits.timemachine.objects.OrderDetails;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.pojo.Photo;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.utils.DbManager;
import com.gwexhibits.timemachine.utils.NotificationHelper;
import com.gwexhibits.timemachine.utils.PreferencesManager;
import com.gwexhibits.timemachine.utils.Utils;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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

    public static final String ORDER_KEY = "order";
    public static final String PHASE_KEY = "phase";

    @BindString(R.string.sfid_title) String sfidTitle;

    @Bind(R.id.coordinator) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.subtitle) TextView subtitle;
    @Bind(R.id.start_new_task) FloatingActionButton startNewTaskButton;
    @Bind(R.id.camear) FloatingActionButton camera;
    @Bind(R.id.cards_recyclerview) CardRecyclerView recyclerView;

    private ArrayList<Card> cards = new ArrayList<>();
    private CardArrayRecyclerViewAdapter cardArrayAdapter;

    private String phase = "";
    private Order order = null;
    private File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        PreferencesManager.initializeInstance(this);
        cardArrayAdapter = new CardArrayRecyclerViewAdapter(this, cards);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideInLeftAnimator());

        if (recyclerView != null) {
            recyclerView.setAdapter(cardArrayAdapter);
        }

        if(PreferencesManager.getInstance().isCurrentTaskRunning()){
            hideStartNewTaskButton();
        }

        DataLoader runner = new DataLoader();
        runner.execute(getIntent().getLongExtra(ORDER_KEY, -1));
        phase = getIntent().getStringExtra(PHASE_KEY);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(PreferencesManager.PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(syncMessageReceiver, new IntentFilter(Utils.SYNC_BROADCAST_NAME));
    }

    @Override
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncMessageReceiver);
    }

    @OnClick(R.id.start_new_task)
    public void startTask(View view) {
        try {
            Time savedTime = DbManager.getInstance().startTask(order.getId(), phase);
            PreferencesManager.getInstance().setCurrents(order.getEntyId(), savedTime.getEntyId());
            NotificationHelper.createNotification(this, order);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.toast_cant_start), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.camear)
    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = new File(Utils.getPhotosPath(this), Utils.buildPhotosName());
        Uri uri = Uri.fromFile(photoFile);
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
        cardArrayAdapter.notifyItemChanged(position);
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
        if (key.equals(PreferencesManager.CURRENT_TASK_KEY)) {
            if(PreferencesManager.getInstance().isCurrentTaskRunning()){
                hideStartNewTaskButton();
            }else{
                showStartNewTaskButton();

                /*Intent mServiceIntent = new Intent(getApplicationContext(), TimesSyncService.class);
                startService(mServiceIntent);*/
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 0:
                switch(resultCode) {
                    case Activity.RESULT_OK:
                        if (photoFile.exists()) {
                            String fileName =  photoFile.getName();
                            try {
                                String dropboxRootFolder = order.getDecodedDropboxLink();
                                String dropboxFullPath = dropboxRootFolder + "/" + phase + "/" + fileName;

                                if (Utils.isInternetAvailable(getApplicationContext())){
                                    DropboxUploader uploader = new DropboxUploader(this);
                                    uploader.execute(photoFile.getAbsolutePath(),
                                            dropboxFullPath,
                                            order.getEntyIdInString(),
                                            phase);
                                }else{
                                    Photo photo = new Photo(photoFile.getAbsolutePath(),
                                            dropboxFullPath,
                                            phase,
                                            order.getEntyIdInString());
                                    DbManager.getInstance().savePhoto(photo);
                                }
                            } catch (UnsupportedEncodingException ue) {
                                ue.printStackTrace();
                                Toast.makeText(this,
                                        getString(R.string.toast_bad_dropbox_link),
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this,
                                        getString(R.string.toast_total_failure),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(this,
                                    getString(R.string.toast_total_failure),
                                    Toast.LENGTH_LONG).show();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    default:
                        Toast.makeText(this,
                                getString(R.string.toast_unsupported_code) + resultCode,
                                Toast.LENGTH_LONG).show();
                }
                break;
            default:
                Toast.makeText(this, getString(R.string.toast_error), Toast.LENGTH_LONG).show();
        }
    }

    private BroadcastReceiver syncMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.showSnackbar(intent, coordinatorLayout, Utils.SYNC_BROADCAST_MESSAGE_KEY);
        }
    };

    private class DataLoader extends AsyncTask<Long, Integer, String> {

        Order currentOrder;

        @Override
        protected String doInBackground(Long... params) {

            try {
                if (params[0] > 0) {
                    currentOrder = DbManager.getInstance().getOrderObject(params[0]);
                }else{
                    currentOrder = DbManager.getInstance().getOrderObject();
                }

            } catch (JSONException jsonex) {
                jsonex.printStackTrace();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }
            return currentOrder.getId();
        }

        protected void onPostExecute(String result) {
            setTitles(currentOrder);
            loadDataFromDB(currentOrder);
            order = this.currentOrder;

            if (order.getDropboxLink() != null && !order.getDropboxLink().equals("")){
                camera.setVisibility(View.VISIBLE);
            }
        }

        private void setTitles(Order order){
            collapsingToolbar.setTitle(sfidTitle + order.getSfid());
            subtitle.setText(order.getOrderTitle());
        }

        private void loadDataFromDB(Order order){

            int position = cards.size();
            OrderDetails details = new OrderDetails(order);

            for (OrderDetailsSections section : details.getDetailsSection()){
                if (section.getListItems().size() > 0) {
                    cards.add(section);
                    section.init();

                    cardArrayAdapter.notifyItemInserted(position);
                    position++;
                }
            }

        }

    }
}
