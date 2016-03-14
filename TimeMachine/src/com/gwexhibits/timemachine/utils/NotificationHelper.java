package com.gwexhibits.timemachine.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import com.gwexhibits.timemachine.OrderDetailsActivity;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.broadcast.NotificationReceiver;
import com.gwexhibits.timemachine.objects.OrderDetails;
import com.gwexhibits.timemachine.objects.sf.OrderObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by psyfu on 3/14/2016.
 */
public class NotificationHelper {

    private static final Integer NOTIFICATION_ID = 53431;

    public static void createNotification(Context context, JSONObject currentOrder){


        Intent showOrderDetails = new Intent(context, OrderDetailsActivity.class);
        showOrderDetails.putExtra(OrderDetailsActivity.ORDER_KEY, currentOrder.toString());
        PendingIntent goToOrder = PendingIntent.getActivity(
                context, (int) System.currentTimeMillis(), showOrderDetails, 0);

        Intent stopTaskBroadcast = new Intent(context, NotificationReceiver.class);
        PendingIntent stopTask = PendingIntent.getBroadcast(context, 1, stopTaskBroadcast, 0);


        String sfid = context.getString(R.string.error_message);
        String client = "";
        String show = "";

        try {
            sfid = currentOrder.getString(OrderObject.SFID);
            client = Utils.getStringValue(currentOrder, OrderObject.CLIENT_NAME);
            show = Html.fromHtml(currentOrder.getString(OrderObject.SHOW_NAME)).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder notificationBuilder  = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_subject))
                .setSmallIcon(R.drawable.sf__icon)
                .setContentIntent(goToOrder)
                .setAutoCancel(false)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        notificationBuilder.addAction(R.drawable.ic_notification_stop,
                context.getString(R.string.notification_stop),
                stopTask);

        NotificationCompat.Style s = notificationBuilder.mStyle;

        NotificationCompat.InboxStyle richNotification = new NotificationCompat.InboxStyle(
                notificationBuilder)
                .addLine(String.format(context.getString(R.string.notification_sfid), sfid))
                .addLine(String.format(context.getString(R.string.notification_client), client))
                .addLine(String.format(context.getString(R.string.notification_show), show))
                /*.setSummaryText(String.format(context.getString(R.string.notification_duration), "1"))*/;


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, richNotification.build());

    }

    public static void stopNotification(Context context){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

}
