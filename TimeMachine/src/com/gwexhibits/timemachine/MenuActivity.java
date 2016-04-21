package com.gwexhibits.timemachine;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gwexhibits.timemachine.services.DropboxService;
import com.gwexhibits.timemachine.services.OrdersSyncService;
import com.gwexhibits.timemachine.services.TimesSyncService;
import com.gwexhibits.timemachine.utils.Utils;

/**
 * Created by psyfu on 4/6/2016.
 */
public class MenuActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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
                Toast.makeText(this, getString(R.string.toast_you_need_internet), Toast.LENGTH_LONG).show();
            }

            return true;
        }

        if (id == R.id.sync_orders) {
            if (Utils.isInternetAvailable(this)) {
                Intent mServiceIntent = new Intent(this, OrdersSyncService.class);
                startService(mServiceIntent);
            } else {
                Toast.makeText(this, getString(R.string.toast_you_need_internet), Toast.LENGTH_LONG).show();
            }

            return true;
        }

        if (id == R.id.sync_tasks) {
            if (Utils.isInternetAvailable(this)) {
                Intent timeSyncSerice = new Intent(this, TimesSyncService.class);
                startService(timeSyncSerice);
            } else {
                Toast.makeText(this, getString(R.string.toast_you_need_internet), Toast.LENGTH_LONG).show();
            }

            return true;
        }

        if (id == R.id.sync_photos) {
            if (Utils.isInternetAvailable(this)) {
                Intent dropBoxService = new Intent(this, DropboxService.class);
                startService(dropBoxService);
            } else {
                Toast.makeText(this, getString(R.string.toast_you_need_internet), Toast.LENGTH_LONG).show();
            }

            return true;
        }

        if (id == android.R.id.home){
            Intent backToMain = new Intent(this, MainActivity.class);
            startActivity(backToMain);
        }

        return super.onOptionsItemSelected(item);
    }

}
