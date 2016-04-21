package com.gwexhibits.timemachine.cards;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gwexhibits.timemachine.OrderDetailsActivity;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.utils.DbManager;
import com.gwexhibits.timemachine.utils.NotificationHelper;
import com.gwexhibits.timemachine.utils.PreferencesManager;
import com.gwexhibits.timemachine.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by psyfu on 3/7/2016.
 */
public class TaskStatusCard extends Card {

    @Bind(R.id.status_card_start_task) Button startTaskButton;
    @Bind(R.id.status_card_stop_task) Button stopTaskButton;
    @Bind(R.id.status_card_update_note) Button updateNoteButton;
    @Bind(R.id.status_card_title) TextView title;
    @Bind(R.id.status_card_note) EditText note;

    private Order order;
    private String phase;

    public TaskStatusCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        ButterKnife.bind(this, view);
        note.addTextChangedListener(noteWatcher);
        note.setVisibility(View.INVISIBLE);
        updateData();
    }

    public void setData(Order order, String phase){
        this.order = order;
        this.phase = phase;
    }

    private void updateData(){

        if (PreferencesManager.getInstance().isCurrentTaskRunning()){
            try {
                showStopButton();
                title.setText(getContext().getText(R.string.order_status_card_title));
                note.setText(DbManager.getInstance().getTime(
                        PreferencesManager.getInstance().getCurrentTask().getEntyIdInString()).getNote());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), getContext().getString(R.string.error_message), Toast.LENGTH_LONG).show();
            }
        } else {
            showStartButton();
            note.setVisibility(View.INVISIBLE);
            title.setText("Start new task");
        }
    }

    @OnClick(R.id.status_card_start_task)
    public void onStartTaskButtonClick(Button button){
        try {
            Time time = DbManager.getInstance().startTask(order.getId(), phase);
            PreferencesManager.getInstance().setCurrents(order, time);
            showStopButton();
            NotificationHelper.createNotification(getContext(), order);
        } catch (Exception e) {
            Toast.makeText(getContext(), getContext().getString(R.string.error_message), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @OnClick(R.id.status_card_stop_task)
    public void onStopTaskButtonClick(Button button){
        try {
            DbManager.getInstance().stopTask();
            PreferencesManager.getInstance().removeCurrent();
            NotificationHelper.stopNotification(getContext());
            showStartButton();
        } catch (Exception e) {
            Toast.makeText(getContext(), getContext().getString(R.string.error_message), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @OnClick(R.id.status_card_update_note)
    public void onNoteUpdateButtonClick(Button button){
        try {
            DbManager.getInstance().updateTimeNote(note.getText().toString());
            Utils.hideKeyboard(getContext(), mInnerView);
            showStopButton();
        } catch (Exception e) {
            Toast.makeText(getContext(), getContext().getString(R.string.error_message), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private TextWatcher noteWatcher = new TextWatcher() {

        String lastValue;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            lastValue = s.subSequence(start, start + count).toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if(!lastValue.equals(s.toString())) {
                showEditButton();
            }
        }
    };

    public void showStartButton(){
        showBackButton();
        startTaskButton.setVisibility(View.VISIBLE);
        stopTaskButton.setVisibility(View.INVISIBLE);
        updateNoteButton.setVisibility(View.INVISIBLE);
        note.setVisibility(View.INVISIBLE);
        note.setText("");
    }

    public void showStopButton(){
        hideBackButton();
        startTaskButton.setVisibility(View.INVISIBLE);
        stopTaskButton.setVisibility(View.VISIBLE);
        updateNoteButton.setVisibility(View.INVISIBLE);
        note.setVisibility(View.VISIBLE);
    }

    public void showEditButton(){
        hideBackButton();
        startTaskButton.setVisibility(View.INVISIBLE);
        stopTaskButton.setVisibility(View.INVISIBLE);
        updateNoteButton.setVisibility(View.VISIBLE);
        note.setVisibility(View.VISIBLE);
    }

    public void hideBackButton(){
        ActionBar actionBar = ((OrderDetailsActivity) getContext()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }

    public void showBackButton(){
        ActionBar actionBar = ((OrderDetailsActivity) getContext()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }
}
