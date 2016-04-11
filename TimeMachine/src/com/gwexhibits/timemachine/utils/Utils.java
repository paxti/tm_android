package com.gwexhibits.timemachine.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by psyfu on 3/7/2016.
 */
public class Utils {

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 112;

    public static final String SYNC_BROADCAST_NAME = "detailsBroadcast";
    public static final String SYNC_BROADCAST_MESSAGE_KEY = "sync_message";

    public static final String SF_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final String PHOTOS_PATH = "/data/photos/";

    public static final String STL_TIME_ZONE = "US/Central";
    public static final String GREENWICH_TIME_ZONE = "GMT";

    public static SimpleDateFormat getDateFormatter(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(SF_FORMAT, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone(GREENWICH_TIME_ZONE));
        return dateFormat;
    }

    public static SimpleDateFormat getDateFormatter(String format, String timeZone){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return dateFormat;
    }

    public static String getCurrentTimeInSfFormat(Date date){
        return getDateFormatter().format(date);
    }

    public static Date getDate(String dateInString) throws ParseException {
        return getDateFormatter(SF_FORMAT, GREENWICH_TIME_ZONE).parse(dateInString);
    }

    public static boolean isInternetAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void showSnackbar(View view, String message){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void showSnackbar(Intent intent, View view, String messageKey){
        String message = intent.getStringExtra(messageKey);
        Utils.showSnackbar(view, message);
    }

    public static String getPhotosPath(Context context){
        return Environment.getExternalStorageDirectory().getPath() + "/" +
                context.getPackageName() + PHOTOS_PATH;
    }

    public static String buildPhotosName(){
        return System.currentTimeMillis() + ".jpg";
    }

    public static String transformTimeToHuman(Date date){
        if (date == null){
            return "";
        } else {
            return getDateFormatter("hh:mm a", STL_TIME_ZONE).format(date);
        }
    }

    public static String transformDateToHuman(Date date){
        return getDateFormatter("EEE, d MMM", STL_TIME_ZONE).format(date);
    }

    public static void requestPermission(Activity activity, String permission, int permissionCode){
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, permissionCode);
        }
    }

    public static boolean checkPermissionGranted(Activity activity, String permission){
        if (Build.VERSION.SDK_INT >= 23){
            return ContextCompat.checkSelfPermission(activity, permission)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public static void requestCameraPermission(Activity activity){
        requestPermission(activity, Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA);
    }

    public static boolean isCameraPermissionGranted(Activity activity){
        return checkPermissionGranted(activity, Manifest.permission.CAMERA);
    }


}
