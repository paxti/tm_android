package com.gwexhibits.timemachine.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.sf.PhotoObject;
import com.gwexhibits.timemachine.utils.NotificationHelper;
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
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by psyfu on 3/15/2016.
 */
public class DropboxService extends IntentService {

    final static public String APP_KEY = "dy4k23ukfavt3dc";
    final static public String APP_SECRET = "mmh1uz7cue45804";

    final public static int PAGE_SIZE = 2000;

    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;
    NotificationCompat.InboxStyle richNotification;

    private DropboxAPI<AndroidAuthSession> mDBApi;


    private UserAccount account;
    private SmartStore smartStore;

    public DropboxService(){
        super(DropboxService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        NotificationCompat.Builder notification = NotificationHelper.getNotificationBuilder(this);

        AndroidAuthSession session = buildSession();
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
        smartStore.registerSoup(PhotoObject.PHOTOS_SUPE, PhotoObject.PHOTOS_INDEX_SPEC);

        try {
            String getAllPhotos = String.format("SELECT {%s:%s} FROM {%s}",
                    PhotoObject.PHOTOS_SUPE, SmartSqlHelper.SOUP, PhotoObject.PHOTOS_SUPE);
            final QuerySpec smartQuerySpec = QuerySpec.buildSmartQuerySpec(getAllPhotos, PAGE_SIZE);
            JSONArray results = smartStore.query(smartQuerySpec, 0);

            for (int i = 0; i < results.length(); i++){
                JSONObject entry = results.getJSONArray(i).getJSONObject(0);

                File file = new File(entry.getString(PhotoObject.PATH));
                FileInputStream inputStream = new FileInputStream(file);

                String dropboxPath = entry.getString(PhotoObject.DROPBOX_PATH);

                mDBApi.putFile(dropboxPath, inputStream, file.length(), null, null);

                notification.setProgress(results.length(), i, true);
                NotificationHelper.updateUploadNotification(this, notification);

                smartStore.delete(PhotoObject.PHOTOS_SUPE, Long.parseLong(entry.getString(SmartStore.SOUP_ENTRY_ID)));
                file.delete();
            }

            notification.setContentText(getString(R.string.notification_uploaded));
            notification.setProgress(1, 1, false);
            NotificationHelper.updateUploadNotification(this, notification);


        } catch (JSONException e) {
            notificationBuilder.setContentText(this.getString(R.string.notification_upload_failed));
            notificationBuilder.setProgress(1, 1, false);
            notificationManager.notify(NotificationHelper.PROGRESS, richNotification.build());
        } catch (FileNotFoundException fe){

            fe.printStackTrace();
        } catch (DropboxException e) {
            notificationBuilder.setContentText(this.getString(R.string.notification_upload_failed));
            notificationBuilder.setProgress(1, 1, false);
            notificationManager.notify(NotificationHelper.PROGRESS, richNotification.build());
        }
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(DropboxService.APP_KEY, DropboxService.APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    private void loadAuth(AndroidAuthSession session) {
        session.setOAuth2AccessToken(Utils.getDropBoxToken(getApplicationContext()));
    }
}
