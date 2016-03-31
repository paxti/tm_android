package com.gwexhibits.timemachine.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.async.UploadFileTask;
import com.gwexhibits.timemachine.objects.pojo.Photo;
import com.gwexhibits.timemachine.utils.DbManager;
import com.gwexhibits.timemachine.utils.DropboxClientFactory;
import com.gwexhibits.timemachine.utils.NotificationHelper;

import java.io.File;
import java.util.List;

/**
 * Created by psyfu on 3/15/2016.
 */
public class DropboxService extends IntentService {

    private NotificationCompat.Builder notificationBuilder;
    private int progress = 0;

    public DropboxService(){
        super(DropboxService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            List<Photo> photos = DbManager.getInstance().getAllNotUploadedPhotos();

            if (photos.size() > 0) {
                notificationBuilder = NotificationHelper.getNotificationBuilder(this);
                notificationBuilder.setProgress(100, 1, true);
                NotificationHelper.updateUploadNotification(DropboxService.this, notificationBuilder);
            }

            uploadFiles(photos);
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(this, getString(R.string.toast_cant_read_from_db), Toast.LENGTH_LONG).show();

            notificationBuilder.setContentText(this.getString(R.string.notification_upload_failed));
            notificationBuilder.setProgress(1, 1, false);
            NotificationHelper.updateUploadNotification(this, notificationBuilder);
        }
    }

    public void uploadFiles(List<Photo> photos){
        final int size = photos.size();
        for(final Photo photo : photos){
            final File file = new File(photo.getLocalPath());

            new UploadFileTask(this, DropboxClientFactory.getClient(), new UploadFileTask.Callback() {
                @Override
                public void onUploadComplete(FileMetadata result) {
                    DbManager.getInstance().deletePhoto(photo);
                    file.delete();

                    if (++progress == size) {
                        notificationBuilder.setProgress(size, size, false);
                        notificationBuilder.setContentText(getString(R.string.notification_uploaded));
                    }else{
                        notificationBuilder.setProgress(size, progress, true);

                        notificationBuilder.setContentText(
                                String.format(getString(R.string.notification_progress),
                                        progress, size)
                        );
                    }
                    NotificationHelper.updateUploadNotification(DropboxService.this, notificationBuilder);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DropboxService.this,
                            getString(R.string.toast_cant_upload),
                            Toast.LENGTH_LONG).show();
                    progress++;
                }
            }).execute(file.getAbsolutePath(), photo.getDropboxPath());
        }
    }
}
