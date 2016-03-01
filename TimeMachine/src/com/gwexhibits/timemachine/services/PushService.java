package com.gwexhibits.timemachine.services;

import android.content.Intent;
import android.os.Bundle;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.push.PushNotificationInterface;

/**
 * Created by psyfu on 2/25/2016.
 */
public class PushService implements PushNotificationInterface {
    @Override
    public void onPushMessageReceived(Bundle message) {
        Intent mServiceIntent = new Intent(SalesforceSDKManager.getInstance().getAppContext(), OrdersSyncService.class);
        SalesforceSDKManager.getInstance().getAppContext().startService(mServiceIntent);
    }
}
