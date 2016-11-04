package com.gwexhibits.timemachine;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gwexhibits.timemachine.cards.HistoryCard;
import com.gwexhibits.timemachine.fragments.TimePickerFragment;
import com.gwexhibits.timemachine.objects.EndAfterStartException;
import com.gwexhibits.timemachine.objects.pojo.ChatterActor;
import com.gwexhibits.timemachine.objects.pojo.Photo;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.utils.ChatterManager;
import com.gwexhibits.timemachine.utils.DbManager;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.RestResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends MenuActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HistoryCardFragment.OnListFragmentInteractionListener,
        GalleryFragment.OnGalleryItemInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        TimePickerFragment.OnCompleteListener,
        ChatterMessageSectionFragment.OnFragmentInteractionListener {

    public static final String SEARCH_FRAGMENT = "searchFragment";
    public static final String HISTORY_FRAGMENT = "historyFragment";
    public static final String GALLERY_FRAGMENT = "galleryFragment";
    public static final String CHATTER_FEED_FRAGMENT = "chatterFeedFragment";
    public static final String CHATTER_POST_FRAGMENT = "chatterPostFragment";

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
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


        try{
            RestResponse response = ChatterManager.getInstance().getChatterProfile(
                    SalesforceSDKManager.getInstance().getUserAccountManager().getCurrentUser().getUserId()
            );
            ObjectMapper mapper = new ObjectMapper();
            mapper.readerFor(ChatterActor.class);
            ObjectReader jsonReader = mapper.readerFor(ChatterActor.class);
            ChatterActor actor = (ChatterActor) jsonReader.readValue(response.asString());


            Picasso.with(this)
                    .load(actor.getPhoto().getPhotoUrl())
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .centerCrop()
                    .fit()
                    .into(accountImage);

        } catch (Exception ex){
            ex.printStackTrace();
        }

        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            //TODO: DIRTY FIX
            FragmentManager fm = getSupportFragmentManager();
            for (Fragment frag : fm.getFragments()) {
                if (frag != null && frag.isVisible()) {
                    FragmentManager childFm = frag.getChildFragmentManager();
                    if (childFm.getBackStackEntryCount() > 1) {
                        childFm.popBackStack();
                        return;
                    }
                }
            }

            super.onBackPressed();
        }
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
                    .addToBackStack(HISTORY_FRAGMENT)
                    .commit();

        } else if (id == R.id.nav_gallery) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_layout, GalleryFragment.newInstance(3), GALLERY_FRAGMENT)
                    .addToBackStack(GALLERY_FRAGMENT)
                    .commit();

    } else if (id == R.id.nav_chatter) {

            if (Utils.isInternetAvailable(this)) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.root_layout, ChatterChat.newInstance(
                                SalesforceSDKManager.getInstance().getUserAccountManager().getStoredUserId(),
                                ChatterChat.TO_ME_FEED_TYPE),
                                CHATTER_FEED_FRAGMENT)
                        .addToBackStack(CHATTER_FEED_FRAGMENT)
                        .commit();
            } else {
                Toast.makeText(this, getString(R.string.internet_needed), Toast.LENGTH_LONG).show();
            }
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
            Toast.makeText(this, getString(R.string.toast_end_before_start), Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, getString(R.string.toast_end_before_start), Toast.LENGTH_LONG).show();
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
            DbManager.getInstance().updateTime(timeObject);
            HistoryCardFragment historyFragment = (HistoryCardFragment) getSupportFragmentManager().findFragmentByTag(HISTORY_FRAGMENT);
            historyFragment.updateData();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onListFragmentInteraction(Photo photo) {
        /*DbManager.getInstance().deletePhoto(photo);
        File photoFile = new File(photo.getLocalPath());
        photoFile.delete();*/
        GalleryFragment galleryFragment = (GalleryFragment) getSupportFragmentManager().findFragmentByTag(GALLERY_FRAGMENT);
        galleryFragment.refreshView();
    }

    @Override
    public void onListFragmentInteraction() {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onChatterPostSentSuccess(RestResponse response) {
    }

    @Override
    public void onChatterPostSentError(Exception exception) {
    }
}
