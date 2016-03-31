package com.gwexhibits.timemachine.async;

import android.os.AsyncTask;

import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.utils.DbManager;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by psyfu on 3/25/2016.
 */
public class TaskLoader extends AsyncTask<Long, Integer, Time> {

    @Override
    protected Time doInBackground(Long... params) {

        try {
            return DbManager.getInstance().getTimeObject(params[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
