package com.gwexhibits.timemachine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.gwexhibits.timemachine.async.UploadFileTask;
import com.gwexhibits.timemachine.objects.pojo.ChatterPost;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.pojo.Photo;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.utils.DbManager;
import com.gwexhibits.timemachine.utils.DropboxClientFactory;
import com.gwexhibits.timemachine.utils.PreferencesManager;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.rest.RestResponse;


import org.apache.commons.codec.DecoderException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetailsActivity extends MenuActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String PATH_TO_PHOTO = "path_to_photo";

    public static final String ORDER_KEY = "order";
    public static final String PHASE_KEY = "phase";

    @Bind(R.id.order_details_toolbar) Toolbar toolbar;
    @Bind(R.id.order_details_viewpager) ViewPager viewPager;
    @Bind(R.id.order_details_tabs) TabLayout tabLayout;

    private String phase = null;
    private Order order = null;
    private Time time = null;
    private File photoFile = null;
    private int backButtonCount;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(PATH_TO_PHOTO)){
            photoFile = new File(savedInstanceState.getString(PATH_TO_PHOTO));
        }

        backButtonCount = 0;
        setArguments();
        setToolbar();
        setTabs();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (photoFile != null) {
            savedInstanceState.putString(PATH_TO_PHOTO, photoFile.getAbsolutePath());
        }
        super.onSaveInstanceState(savedInstanceState);

    }

    private void setToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getOrder().getTitleForOptions());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setTabs(){
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(
                DetailsFragment.newInstance(getOrder(), getPhase()), getString(R.string.order_details_tab_name));
        if (Utils.isInternetAvailable(this)) {
            adapter.addFrag(ChatterChat.newInstance(getOrder().getId(), ChatterChat.RECORDS_FEED_TYPE), getString(R.string.order_chatter_tab_name));
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setArguments(){
        PreferencesManager.initializeInstance(this);
        if (PreferencesManager.getInstance().isCurrentTaskRunning()){
            try {
                setOrder(PreferencesManager.getInstance().getCurrentOrder());
                setTime(DbManager.getInstance().getTime(PreferencesManager.getInstance().getCurrentTask().getEntyIdInString()));
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            setPhase(getIntent().getStringExtra(PHASE_KEY));
            setOrder((Order) getIntent().getSerializableExtra(ORDER_KEY));
        }
    }

    @Override
    public void onBackPressed() {

        //TODO: DIRTY FIX
        FragmentManager fm = getSupportFragmentManager();
        for (Fragment frag : fm.getFragments()) {
            if (frag.isVisible()) {
                FragmentManager childFm = frag.getChildFragmentManager();
                if (childFm.getBackStackEntryCount() > 1) {
                    childFm.popBackStack();
                    return;
                }
            }
        }


        if (!PreferencesManager.getInstance().isCurrentTaskRunning()) {
            Intent backToMain = new Intent(this, MainActivity.class);
            startActivity(backToMain);
        } else {
            if( backButtonCount >= 1) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.exit_notification), Toast.LENGTH_SHORT).show();
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
                break;
            }
            case Utils.MY_PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture(null);
                } else {
                    Toast.makeText(this, getString(R.string.no_permission_for_storage), Toast.LENGTH_LONG);
                }
                break;
            }
        }
    }

    @OnClick(R.id.camera)
    public void takePicture(View view) {
        if (Utils.isCameraPermissionGranted(this) && Utils.isStoragePermissionGranted(this)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = new File(Utils.getPhotosPath(this), Utils.buildPhotosName());
            Uri uri = Uri.fromFile(photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri );
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        } else {
            Utils.requestPermissions(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    public void uploadFile(final File file, final String phase, final Order order){

        final Photo photo = savePhotoLocally(file.getName());

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

                    if (file.delete()) {
                        DbManager.getInstance().deletePhoto(photo);
                    } else {
                        Toast.makeText(OrderDetailsActivity.this,
                                getString(R.string.toast_cant_delete),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    showCantUploadToDropBox();
                    savePhotoLocally(file.getName());

                }
            }).execute(file.getAbsolutePath(), photo.getDropboxPath());

        } catch (IllegalStateException ise){
            ise.printStackTrace();
            showCantUploadToDropBox();
            savePhotoLocally(file.getName());
        }
    }

    private Photo savePhotoLocally(String fileName){
        Photo photo = null;
        try {
            photo = new Photo(photoFile.getAbsolutePath(),
                    fileName,
                    phase,
                    order);
            DbManager.getInstance().savePhoto(photo);
            Toast.makeText(OrderDetailsActivity.this,
                    getString(R.string.toast_saved_locally),
                    Toast.LENGTH_SHORT)
                    .show();
        }catch (UnsupportedEncodingException ue) {
            ue.printStackTrace();
            Toast.makeText(this,
                    getString(R.string.toast_bad_dropbox_link),
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(OrderDetailsActivity.this,
                    getString(R.string.toast_total_failure),
                    Toast.LENGTH_LONG).show();
        }

        return photo;
    }

    private void showCantUploadToDropBox(){
        Toast.makeText(OrderDetailsActivity.this,
                getString(R.string.toast_cant_upload),
                Toast.LENGTH_SHORT)
                .show();
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    public String getPhase() {
        if (this.time != null){
            return this.time.getPhase();
        } else {
            return this.phase;
        }
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
