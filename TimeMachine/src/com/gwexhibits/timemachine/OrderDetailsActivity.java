package com.gwexhibits.timemachine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import com.dropbox.core.v2.files.FileMetadata;
import com.gwexhibits.timemachine.async.UploadFileTask;
import com.gwexhibits.timemachine.cards.OrderDetailsSections;
import com.gwexhibits.timemachine.cards.TaskStatusCard;
import com.gwexhibits.timemachine.objects.OrderDetails;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.pojo.Photo;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.services.TimesSyncService;
import com.gwexhibits.timemachine.utils.DbManager;
import com.gwexhibits.timemachine.utils.DropboxClientFactory;
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
    private static final int REQUEST_TAKE_PHOTO = 1;

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
    private int backButtonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        backButtonCount = 0;

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

    @Override
    public void onBackPressed() {
        // TODO: Change this later
        if (!PreferencesManager.getInstance().isCurrentTaskRunning()) {
            Intent backToMain = new Intent(this, MainActivity.class);
            startActivity(backToMain);
        } else {
            if( backButtonCount >= 1)
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
                backButtonCount++;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture(null);
                } else {
                    Toast.makeText(this, getString(R.string.no_permission_for_camera), Toast.LENGTH_LONG);
                }
                return;
            }
        }
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
        if (Utils.isCameraPermissionGranted(this)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = new File(Utils.getPhotosPath(this), Utils.buildPhotosName());
            Uri uri = Uri.fromFile(photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        } else {
            Utils.requestCameraPermission(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK){
            if (photoFile.exists()) {
                uploadFile(photoFile, phase, order);
            } else {
                Toast.makeText(this, getString(R.string.toast_total_failure), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_error), Toast.LENGTH_LONG).show();
        }
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
                if (Utils.isInternetAvailable(this)) {
                    Intent mServiceIntent = new Intent(getApplicationContext(), TimesSyncService.class);
                    startService(mServiceIntent);
                }
            }
        }
    }

    public void uploadFile(final File file, final String phase, final Order order){

        final String dropboxFullPath = getDropboxPath(file);
        final Photo photo = savePhotoLocally(dropboxFullPath);

        try {
            Toast.makeText(OrderDetailsActivity.this,
                    getString(R.string.toast_uploading),
                    Toast.LENGTH_SHORT)
                    .show();

            new UploadFileTask(this, DropboxClientFactory.getClient(), new UploadFileTask.Callback() {
                @Override
                public void onUploadComplete(FileMetadata result) {
                    Toast.makeText(OrderDetailsActivity.this,
                            getString(R.string.toast_uploaded),
                            Toast.LENGTH_SHORT).show();

                    file.delete();
                    DbManager.getInstance().deletePhoto(photo);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    showCantUploadToDropBox();
                    savePhotoLocally(dropboxFullPath);

                }
            }).execute(file.getAbsolutePath(), dropboxFullPath);

        } catch (IllegalStateException ise){
            ise.printStackTrace();
            showCantUploadToDropBox();
            savePhotoLocally(dropboxFullPath);
        }
    }

    private Photo savePhotoLocally(String dropboxFullPath){
        Photo photo = null;
        try {
            photo = new Photo(photoFile.getAbsolutePath(),
                    dropboxFullPath,
                    phase,
                    order.getEntyIdInString());
            DbManager.getInstance().savePhoto(photo);
            Toast.makeText(OrderDetailsActivity.this,
                    getString(R.string.toast_saved_locally),
                    Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(OrderDetailsActivity.this,
                    getString(R.string.toast_total_failure),
                    Toast.LENGTH_LONG).show();
        }

        return photo;
    }

    private String getDropboxPath(File file){
        String dropboxFullPath = "";
        try {
            String dropboxRootFolder = order.getDecodedDropboxLink();
            dropboxFullPath = dropboxRootFolder + "/" + phase + "/" + file.getName();
        }catch (UnsupportedEncodingException ue) {
            ue.printStackTrace();
            Toast.makeText(this,
                getString(R.string.toast_bad_dropbox_link),
                Toast.LENGTH_LONG).show();
        }

        return dropboxFullPath;
    }

    private void showCantUploadToDropBox(){
        Toast.makeText(OrderDetailsActivity.this,
                getString(R.string.toast_cant_upload),
                Toast.LENGTH_SHORT)
                .show();
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
