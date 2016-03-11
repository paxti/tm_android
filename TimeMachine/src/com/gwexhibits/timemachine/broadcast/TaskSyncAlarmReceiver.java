package com.gwexhibits.timemachine.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gwexhibits.timemachine.services.TimesSyncService;
import com.gwexhibits.timemachine.utils.Utils;

/**
 * Created by psyfu on 3/10/2016.
 */
public class TaskSyncAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("ALARM", "try");

        if(Utils.isInternetAvailable(context)) {
            Intent service = new Intent(context, TimesSyncService.class);
            context.startService(service);
        }
    }
}
