package com.gwexhibits.timemachine.cards;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.utils.DbManager;
import com.gwexhibits.timemachine.utils.NotificationHelper;
import com.gwexhibits.timemachine.utils.PreferencesManager;
import com.gwexhibits.timemachine.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by psyfu on 3/7/2016.
 */
public class TaskStatusCard extends Card {

    private Context context;
    private Button stopTaskButton = null;
    private Button addNoteButton = null;
    private EditText note = null;
    private TextView title = null;
    private TextView details = null;

    public TaskStatusCard(Context context, int innerLayout) {
        super(context, innerLayout);
        this.context = context;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        Time time = null;
        try {
            time = DbManager.getInstance().getTimeObject();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        title = (TextView) parent.findViewById(R.id.task_status_title);
        title.setText(context.getText(R.string.order_status_card_title));

        note = (EditText) parent.findViewById(R.id.note);
        note.setText(time.getNote());
        note.addTextChangedListener(noteTextWatcher);

        details = (TextView) parent.findViewById(R.id.task_status_details);
        details.setText(context.getText(R.string.current_task_running_for));

        //TODO: Fix bug with Edit text value 'caching' ??
        stopTaskButton = (Button) parent.findViewById(R.id.stop_task);
        stopTaskButton.setOnClickListener(stopTaskListener);
        stopTaskButton.setVisibility(View.VISIBLE);

        addNoteButton = (Button) parent.findViewById(R.id.add_note);
        addNoteButton.setOnClickListener(updateNoteListener);
        addNoteButton.setVisibility(View.INVISIBLE);
    }

    private void setHeader(){
        this.setTitle(context.getString(R.string.order_status_card_title));
    }

    private View.OnClickListener stopTaskListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                DbManager.getInstance().stopTask();
                PreferencesManager.getInstance().removeCurrent();
                NotificationHelper.stopNotification(context);
                note.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener updateNoteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                DbManager.getInstance().updateTimeNote(note.getText().toString());
                addNoteButton.setVisibility(View.INVISIBLE);
                note.clearFocus();
                if (mInnerView != null) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mInnerView.getWindowToken(), 0);
                }
                stopTaskButton.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

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
                stopTaskButton.setVisibility(View.INVISIBLE);
                addNoteButton.setVisibility(View.VISIBLE);
            }
        }

    };

}
