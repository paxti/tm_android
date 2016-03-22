package com.gwexhibits.timemachine.async;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.pojo.Photo;
import com.gwexhibits.timemachine.utils.DbManager;
import com.gwexhibits.timemachine.utils.DropBoxHelper;
import com.gwexhibits.timemachine.utils.NotificationHelper;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by psyfu on 3/21/2016.
 */
public class DropboxUploader extends AsyncTask<String, String, String> {


    private DropboxAPI<AndroidAuthSession> mDBApi;
    private Context context;
    private NotificationCompat.Builder notification;

    public DropboxUploader(Context context){
        this.context = context;
        notification = NotificationHelper.getNotificationBuilder(context);
    }

    @Override
    protected String doInBackground(String... params) {

        String localPath = params[0];
        String dropboxPath = params[1];
        String orderId = params[2];
        String phase = params[3];

        mDBApi = DropBoxHelper.getInstance().getAPI();

        File file = new File(localPath);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            mDBApi.putFile(dropboxPath, inputStream, file.length(), null, null);
            file.delete();
        } catch (Exception e) {
            try {
                Photo newPhoto = new Photo(localPath, dropboxPath, orderId, phase);
                DbManager.getInstance().savePhoto(newPhoto);
                return context.getString(R.string.toast_error_while_uploading);
            } catch (Exception e1) {
                return context.getString(R.string.toast_total_failure);
            }
        }

        return context.getString(R.string.toast_uploaded);
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(context, context.getString(R.string.toast_uploading), Toast.LENGTH_LONG).show();
    }
}