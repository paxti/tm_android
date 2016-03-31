package com.gwexhibits.timemachine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gwexhibits.timemachine.cards.HistoryCard;
import com.gwexhibits.timemachine.dummy.DummyContent;
import com.gwexhibits.timemachine.fragments.TimePickerFragment;
import com.gwexhibits.timemachine.objects.EndAfterStartException;
import com.gwexhibits.timemachine.objects.pojo.Photo;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.services.DropboxService;
import com.gwexhibits.timemachine.services.OrdersSyncService;
import com.gwexhibits.timemachine.services.TimesSyncService;
import com.gwexhibits.timemachine.utils.DbManager;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.app.SalesforceSDKManager;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HistoryCardFragment.OnListFragmentInteractionListener,
        GalleryFragment.OnGalleryItemInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        TimePickerFragment.OnCompleteListener {

    public static final String SEARCH_FRAGMENT = "searchFragment";
    public static final String HISTORY_FRAGMENT = "historyFragment";
    public static final String GALLERY_FRAGMENT = "galleryFragment";

    @Bind(R.id.drawer_layout) DrawerLayout drawer;
    @Bind(R.id.nav_view) NavigationView navigationView;
    @Nullable @Bind(R.id.account_email) TextView accountName;
    @Nullable @Bind(R.id.account_full_name) TextView accountEmail;
    @Nullable @Bind(R.id.account_image) ImageView accountImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.root_layout, SearchFragment.newInstance(), SEARCH_FRAGMENT)
                    .commit();
        }

        setDrawerToogle();
        setNavigationView();
    }

    private void setDrawerToogle(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setNavigationView(){
        navigationView.setNavigationItemSelectedListener(this);
        accountName = ButterKnife.findById(navigationView.getHeaderView(0), R.id.account_full_name);
        accountEmail = ButterKnife.findById(navigationView.getHeaderView(0), R.id.account_email);
        accountImage = ButterKnife.findById(navigationView.getHeaderView(0), R.id.account_image);

        accountName.setText(SalesforceSDKManager.getInstance().getUserAccountManager().getCurrentUser().getDisplayName());
        accountEmail.setText(SalesforceSDKManager.getInstance().getUserAccountManager().getCurrentUser().getEmail());
        navigationView.getMenu().getItem(0).setChecked(true);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sync_all) {
            if (Utils.isInternetAvailable(this)) {
                Intent mServiceIntent = new Intent(this, OrdersSyncService.class);
                startService(mServiceIntent);

                Intent dropBoxService = new Intent(this, DropboxService.class);
                startService(dropBoxService);

                Intent timeSyncSerice = new Intent(this, TimesSyncService.class);
                startService(timeSyncSerice);
            } else {
                Toast.makeText(this, getString(R.string.toast_you_need_internet), Toast.LENGTH_LONG);
            }

            return true;
        }

        if (id == R.id.sync_orders) {
            if (Utils.isInternetAvailable(this)) {
                Intent mServiceIntent = new Intent(this, OrdersSyncService.class);
                startService(mServiceIntent);
            } else {
                Toast.makeText(this, getString(R.string.toast_you_need_internet), Toast.LENGTH_LONG);
            }

            return true;
        }

        if (id == R.id.sync_tasks) {
            if (Utils.isInternetAvailable(this)) {
                Intent timeSyncSerice = new Intent(this, TimesSyncService.class);
                startService(timeSyncSerice);
            } else {
                Toast.makeText(this, getString(R.string.toast_you_need_internet), Toast.LENGTH_LONG);
            }

            return true;
        }

        if (id == R.id.sync_photos) {
            if (Utils.isInternetAvailable(this)) {
                Intent dropBoxService = new Intent(this, DropboxService.class);
                startService(dropBoxService);
            } else {
                Toast.makeText(this, getString(R.string.toast_you_need_internet), Toast.LENGTH_LONG);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_search){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_layout, SearchFragment.newInstance(), SEARCH_FRAGMENT)
                    .commit();

        }else if (id == R.id.nav_history) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_layout, HistoryCardFragment.newInstance(), HISTORY_FRAGMENT)
                    .commit();

        } else if (id == R.id.nav_gallery) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_layout, GalleryFragment.newInstance(3), GALLERY_FRAGMENT)
                    .commit();

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCompleteChangeStartTime(Date time, HistoryCard card) {
        try {
            Time timeObject = card.getTime();
            timeObject.changeStartTime(time);
            saveTimeObject(timeObject, card);
        } catch (EndAfterStartException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.toast_end_before_start), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onCompleteChangeEndTime(Date time, HistoryCard card) {
        try {
            Time timeObject = card.getTime();
            timeObject.changeEndTime(time);
            saveTimeObject(timeObject, card);
        } catch (EndAfterStartException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.toast_end_before_start), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onCompleteChangeDate(Date date, HistoryCard card) {
        Time timeObject = card.getTime();
        timeObject.changeDate(date);
        saveTimeObject(timeObject, card);
    }

    @Override
    public void onCompleteChangePhase(String phase, HistoryCard card) {
        Time timeObject = card.getTime();
        timeObject.changePhase(phase);
        saveTimeObject(timeObject, card);
    }

    private void saveTimeObject(Time timeObject, HistoryCard card){
        try {
            Time newTimeObject = DbManager.getInstance().updateTime(timeObject);
            card.setTime(newTimeObject);
            card.updateData();
            card.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onListFragmentInteraction(Photo photo) {

        DbManager.getInstance().deletePhoto(photo);
        File photoFile = new File(photo.getLocalPath());
        photoFile.delete();
        GalleryFragment galleryFragment = (GalleryFragment) getSupportFragmentManager().findFragmentByTag(GALLERY_FRAGMENT);
        galleryFragment.refreshView();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

}
