package com.gwexhibits.timemachine.async;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by psyfu on 9/22/2016.
 */
public class CheckUploadTask extends AsyncTask<String, Void, Metadata> {

    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onUploadComplete(Metadata result);
        void onError(Exception e);
    }

    public CheckUploadTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(Metadata result) {
        super.onPostExecute(result);
        if (mException != null) {
            mCallback.onError(mException);
        } else if (result == null) {
            mCallback.onError(null);
        } else {
            mCallback.onUploadComplete(result);
        }
    }

    @Override
    protected Metadata doInBackground(String... params) {
        if (params.length > 0) {

            String dropboxPath = params[0];

            try {
                return mDbxClient.files().getMetadata(dropboxPath);
            } catch (DbxException e) {
                mException = e;
            }
        }

        return null;
    }
}
