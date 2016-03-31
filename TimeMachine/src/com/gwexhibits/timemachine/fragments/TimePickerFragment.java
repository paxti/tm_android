package com.gwexhibits.timemachine.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gwexhibits.timemachine.OrderDetailsActivity;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.cards.HistoryCard;
import com.gwexhibits.timemachine.objects.EndAfterStartException;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.utils.MyTimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by psyfu on 3/24/2016.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    public static final String CARD_ID_KEY = "card_id";
    public static final String TIME_KEY = "time_key";
    public static final String ACTION_KEY = "action_key";
    public static final String DIALOG_TYPE_KEY = "dialog_type";
    public static final String PHASES_OPTIONS_KEY = "phase_options";

    public static final Integer CHANGE_START_TIME = 1;
    public static final Integer CHANGE_END_TIME = 2;
    public static final Integer CHANGE_DATE = 3;

    public static final Integer DIALOG_TYPE_DATE = 1;
    public static final Integer DIALOG_TYPE_TIME = 2;
    public static final Integer DIALOG_TYPE_SELECTOR = 3;

    private HistoryCard card;
    private Date initialDate;
    private int action;
    private int dialogType;
    private OnCompleteListener onCompleteListener;

    public static interface OnCompleteListener {
        public abstract void onCompleteChangeStartTime(Date time, HistoryCard card);
        public abstract void onCompleteChangeEndTime(Date time, HistoryCard card);
        public abstract void onCompleteChangeDate(Date time, HistoryCard card);
        public abstract void onCompleteChangePhase(String phase, HistoryCard card);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        card = (HistoryCard) bundle.getSerializable(CARD_ID_KEY);
        initialDate = (Date) bundle.getSerializable(TIME_KEY);
        action = bundle.getInt(ACTION_KEY);
        dialogType = bundle.getInt(DIALOG_TYPE_KEY);

        Calendar c = Calendar.getInstance();
        c.setTime(initialDate);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if(dialogType == DIALOG_TYPE_TIME) {
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        } else if (dialogType == DIALOG_TYPE_DATE) {
            return new DatePickerDialog(getActivity(), this, year, month, day);
        } else {

            final String[] options = bundle.getStringArray(PHASES_OPTIONS_KEY);

            return new android.support.v7.app.AlertDialog.Builder(getActivity()).setTitle(R.string.stage_dialog_title)
                .setSingleChoiceItems(options, -1,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            onCompleteListener.onCompleteChangePhase(options[id], card);
                            dialog.dismiss();
                        }
                    }
                ).create();
        }
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Date usersTime = getUpdatedTime(hourOfDay, minute);

        if (action == CHANGE_START_TIME) {
            onCompleteListener.onCompleteChangeStartTime(usersTime, card);

        } else if (action == CHANGE_END_TIME) {
            onCompleteListener.onCompleteChangeEndTime(usersTime, card);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Date usersDate = getUpdatedDate(year, monthOfYear, dayOfMonth);
        onCompleteListener.onCompleteChangeDate(usersDate, card);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.onCompleteListener = (OnCompleteListener) activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    private Date getUpdatedTime(int hourOfDay, int minute){
        Calendar c = Calendar.getInstance();
        c.setTime(initialDate);
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        return c.getTime();
    }

    private Date getUpdatedDate(int year, int monthOfYear, int dayOfMonth){
        Calendar c = Calendar.getInstance();
        c.setTime(initialDate);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        return c.getTime();
    }
}