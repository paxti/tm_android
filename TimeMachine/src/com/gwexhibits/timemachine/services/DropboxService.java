package com.gwexhibits.timemachine.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.pojo.Photo;
import com.gwexhibits.timemachine.objects.sf.PhotoObject;
import com.gwexhibits.timemachine.utils.DbManager;
import com.gwexhibits.timemachine.utils.DropBoxHelper;
import com.gwexhibits.timemachine.utils.NotificationHelper;
import com.gwexhibits.timemachine.utils.PreferencesManager;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.smartstore.store.QuerySpec;
import com.salesforce.androidsdk.smartstore.store.SmartSqlHelper;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;
import com.salesforce.androidsdk.smartsync.manager.SyncManager;
import com.salesforce.androidsdk.smartsync.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by psyfu on 3/15/2016.
 */
public class DropboxService extends IntentService {

    NotificationCompat.Builder notificationBuilder;

    private DropboxAPI<AndroidAuthSession> mDBApi;

    public DropboxService(){
        super(DropboxService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        notificationBuilder = NotificationHelper.getNotificationBuilder(this);
        mDBApi = DropBoxHelper.getInstance().getAPI();

        List<Photo> photos = new ArrayList<>();

        try {
            photos = DbManager.getInstance().getAllNotUploadedPhotos();
            uploadFiles(photos);
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(this, getString(R.string.toast_cant_read_from_db), Toast.LENGTH_LONG).show();
        }
    }

    public void uploadFiles(List<Photo> photos){

        int i = 0;
        int size = photos.size();
        FileInputStream inputStream = null;
        for(Photo photo : photos){
            try {
                File file = new File(photo.getLocalPath());
                inputStream = new FileInputStream(file);
                mDBApi.putFile(photo.getDropboxPath(), inputStream, size, null, null);
                DbManager.getInstance().deletePhoto(photo);
                file.delete();
            } catch (DropboxException dex) {
                dex.printStackTrace();
                Toast.makeText(this, getString(R.string.toast_cant_upload), Toast.LENGTH_LONG).show();
            }catch (FileNotFoundException fex) {
                fex.printStackTrace();
                Toast.makeText(this, getString(R.string.toast_file_not_found), Toast.LENGTH_LONG).show();
            }finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                notificationBuilder.setProgress(size, i, true);
                NotificationHelper.updateUploadNotification(this, notificationBuilder);
                i++;
            }
        }

        notificationBuilder.setContentText(this.getString(R.string.notification_uploaded));
        notificationBuilder.setProgress(1, 1, false);
        NotificationHelper.updateUploadNotification(this, notificationBuilder);
    }

}
