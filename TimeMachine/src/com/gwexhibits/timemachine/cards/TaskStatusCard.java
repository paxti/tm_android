package com.gwexhibits.timemachine.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.utils.Utils;

import org.json.JSONException;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by psyfu on 3/7/2016.
 */
public class TaskStatusCard extends Card {

    private Context context;
    private Button stopTask;
    private TextView title;
    private TextView details;

    public TaskStatusCard(Context context) {
        this(context, R.layout.order_details_status_card);
    }

    public TaskStatusCard(Context context, int innerLayout) {
        super(context, innerLayout);
        this.context = context;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        stopTask = (Button) parent.findViewById(R.id.stop_task);

        stopTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Utils.stopCurrentTask(context, "My best note ever");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        title = (TextView) parent.findViewById(R.id.task_status_title);
        title.setText(context.getText(R.string.order_status_card_title));

        details = (TextView) parent.findViewById(R.id.details);
        details.setText(context.getText(R.string.current_task_running_for));
    }

    private void setHeader(){
        this.setTitle(context.getString(R.string.order_status_card_title));
    }

}
