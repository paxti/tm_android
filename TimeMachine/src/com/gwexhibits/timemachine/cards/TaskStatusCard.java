package com.gwexhibits.timemachine.cards;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.utils.DbManager;
import com.gwexhibits.timemachine.utils.NotificationHelper;
import com.gwexhibits.timemachine.utils.PreferencesManager;

import org.apache.commons.codec.DecoderException;
import org.json.JSONException;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by psyfu on 3/7/2016.
 */
public class TaskStatusCard extends Card {


    @Bind(R.id.status_card_start_task) Button startNewTask;
    @Bind(R.id.status_card_stop_task) Button stopTask;
    @Bind(R.id.status_card_update_note) Button updateNote;
    @Bind(R.id.status_card_title) TextView title;
    @Bind(R.id.status_card_note) EditText note;

    private Context context;
    private Order order;
    private String phase;

    public TaskStatusCard(Context context, int innerLayout) {
        super(context, innerLayout);
        this.context = context;
    }

    @Override
    public View getInnerView(Context context, ViewGroup parent) {
        View view = super.getInnerView(context, parent);
        ButterKnife.bind(this, view);
        updateData();
        return view;
    }

    public void setData(Order order, String phase){
        this.order = order;
        this.phase = phase;
    }

    private void updateData(){


        if (PreferencesManager.getInstance().isCurrentTaskRunning()){

            try {
                Time time = PreferencesManager.getInstance().getCurrentTask();
                startNewTask.setVisibility(View.INVISIBLE);
                stopTask.setVisibility(View.VISIBLE);
                updateNote.setVisibility(View.INVISIBLE);
                title.setText(context.getText(R.string.order_status_card_title));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            startNewTask.setVisibility(View.VISIBLE);
            stopTask.setVisibility(View.INVISIBLE);
            updateNote.setVisibility(View.INVISIBLE);
            note.setVisibility(View.INVISIBLE);
            title.setText("Start new task");
        }
    }

    @OnClick(R.id.status_card_start_task)
    public void onStartTaskButtonClick(Button button){
        try {
            Time time = DbManager.getInstance().startTask(order.getOrderNumber(), phase);
            PreferencesManager.getInstance().setCurrents(order, time);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.status_card_stop_task)
    public void onStopTaskButtonClick(Button button){
        try {
            DbManager.getInstance().stopTask();
            PreferencesManager.getInstance().removeCurrent();
            NotificationHelper.stopNotification(context);
            note.setText("");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (DecoderException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.status_card_update_note)
    public void onNoteUpdateButtonClick(Button button){
        try {
            DbManager.getInstance().updateTimeNote(note.getText().toString());
            updateNote.setVisibility(View.INVISIBLE);
            note.clearFocus();
            if (mInnerView != null) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mInnerView.getWindowToken(), 0);
            }
            stopTask.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (DecoderException e) {
            e.printStackTrace();
        }
    }


    private TextWatcher noteTextWatcher = new TextWatcher() {

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
                stopTask.setVisibility(View.INVISIBLE);
                stopTask.setVisibility(View.INVISIBLE);
                updateNote.setVisibility(View.VISIBLE);
            }
        }

    };

}
