package com.gwexhibits.timemachine.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.utils.NotificationHelper;
import com.gwexhibits.timemachine.utils.Utils;

import org.json.JSONException;

/**
 * Created by psyfu on 3/14/2016.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Utils.stopCurrentTask(context);
            NotificationHelper.stopNotification(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
