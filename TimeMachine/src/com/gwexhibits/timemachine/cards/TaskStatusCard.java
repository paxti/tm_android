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
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.gwexhibits.timemachine.utils.NotificationHelper;
import com.gwexhibits.timemachine.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by psyfu on 3/7/2016.
 */
public class TaskStatusCard extends Card {

    private Context context;
    private Button stopTask;
    private Button addNote;
    private EditText note;
    private TextView title;
    private TextView details;

    private JSONObject task;

    public TaskStatusCard(Context context, JSONObject order) {
        this(context, R.layout.order_details_status_card);
    }

    public TaskStatusCard(Context context, int innerLayout) {
        super(context, innerLayout);
        this.context = context;
        try {
            this.task = Utils.getCurrentTimeEntry(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        title = (TextView) parent.findViewById(R.id.task_status_title);
        title.setText(context.getText(R.string.order_status_card_title));

        note = (EditText) parent.findViewById(R.id.note);
        try {
            note.setText(task.getString(TimeObject.NOTE));
        } catch (JSONException e) {
           note.setText("");
        }

        details = (TextView) parent.findViewById(R.id.task_status_details);
        details.setText(context.getText(R.string.current_task_running_for));

        stopTask = (Button) parent.findViewById(R.id.stop_task);
        addNote = (Button) parent.findViewById(R.id.add_note);
        addNote.setVisibility(View.INVISIBLE);

        stopTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Utils.stopCurrentTask(context);
                    NotificationHelper.stopNotification(context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Utils.updateNote(context, note.getText().toString());
                    addNote.setVisibility(View.INVISIBLE);
                    note.clearFocus();
                    if (mInnerView != null) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mInnerView.getWindowToken(), 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                addNote.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setHeader(){
        this.setTitle(context.getString(R.string.order_status_card_title));
    }

}
