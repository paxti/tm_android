package com.gwexhibits.timemachine.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.TimePicker;

/**
 * Created by psyfu on 3/25/2016.
 */
public class MyTimePickerDialog extends TimePickerDialog {

    public MyTimePickerDialog(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, listener, hourOfDay, minute, is24HourView);
    }

    public MyTimePickerDialog(Context context, int themeResId, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, themeResId, listener, hourOfDay, minute, is24HourView);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                Log.d("PICKER", "FROM time picker 1111");
                break;
            case BUTTON_NEGATIVE:
                Log.d("PICKER", "FROM time picker 222");
//                cancel();
                break;
        }
    }
}
