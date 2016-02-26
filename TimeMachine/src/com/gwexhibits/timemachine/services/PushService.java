package com.gwexhibits.timemachine.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gwexhibits.timemachine.SearchActivity;
import com.salesforce.androidsdk.push.PushNotificationInterface;

/**
 * Created by psyfu on 2/25/2016.
 */
public class PushService implements PushNotificationInterface {
    @Override
    public void onPushMessageReceived(Context context, Bundle message) {
        Intent mServiceIntent = new Intent(context, OrdersSyncService.class);
        context.startService(mServiceIntent);
    }
}
