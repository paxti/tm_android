package com.gwexhibits.timemachine.services;

import android.os.Bundle;
import android.util.Log;

import com.salesforce.androidsdk.push.PushNotificationInterface;

/**
 * Created by psyfu on 2/25/2016.
 */
public class PushService implements PushNotificationInterface {
    @Override
    public void onPushMessageReceived(Bundle message) {
        Log.d("MESSAGE", message.toString());
    }
}
