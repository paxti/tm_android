package com.gwexhibits.timemachine.async;

import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.utils.DbManager;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by psyfu on 3/25/2016.
 */
public class TaskLoader extends AsyncTask<Long, Integer, Time> {

    private static final String TAG = TaskLoader.class.getName();

    @Override
    protected Time doInBackground(Long... params) {

        try {
            return DbManager.getInstance().getTimeObject(params[0]);
        } catch (JSONException e) {
            e.printStackTrace();
            Crashlytics.log(Log.DEBUG, TAG, e.getMessage());
        } catch (IOException io) {
            io.printStackTrace();
            Crashlytics.log(Log.DEBUG, TAG, "Params: " +
                    String.valueOf(params[0])+ " " + io.getMessage());
        }

        return null;
    }
}
