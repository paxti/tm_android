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
import com.gwexhibits.timemachine.SplashScreenActivity;
import com.gwexhibits.timemachine.broadcast.NotificationReceiver;
import com.gwexhibits.timemachine.objects.OrderDetails;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.sf.OrderObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by psyfu on 3/14/2016.
 */
public class NotificationHelper {

    private static final Integer NOTIFICATION_ID = 53431;
    public static final Integer PROGRESS = 52221;

    public static void createNotification(Context context, Order currentOrder){

        Intent showOrderDetails = new Intent(context, SplashScreenActivity.class);
        showOrderDetails.putExtra(OrderDetailsActivity.ORDER_KEY, currentOrder.toString());
        PendingIntent goToOrder = PendingIntent.getActivity(
                context, (int) System.currentTimeMillis(), showOrderDetails, 0);

        Intent stopTaskBroadcast = new Intent(context, NotificationReceiver.class);
        PendingIntent stopTask = PendingIntent.getBroadcast(context, 1, stopTaskBroadcast, 0);

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
                .addLine(String.format(context.getString(R.string.notification_sfid), currentOrder.getSfid()))
                .addLine(String.format(context.getString(R.string.notification_client), currentOrder.getAccount().getName()))
                .addLine(String.format(context.getString(R.string.notification_show), Html.fromHtml(currentOrder.getShowName())))
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


    public static NotificationCompat.Builder getNotificationBuilder(Context context){

        return new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_photo_uploading))
                .setSmallIcon(R.drawable.sf__icon)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setProgress(100,0,true);

    }
    public static void buildUploadNotification(Context context){
        NotificationCompat.Builder notificationBuilder  = getNotificationBuilder(context);

        NotificationCompat.Style s = notificationBuilder.mStyle;
        NotificationCompat.InboxStyle richNotification = new NotificationCompat.InboxStyle(notificationBuilder);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationHelper.PROGRESS, richNotification.build());
    }

    public static void updateUploadNotification(Context context, NotificationCompat.Builder notificationBuilder){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(NotificationHelper.PROGRESS, notificationBuilder.build());
    }

}
