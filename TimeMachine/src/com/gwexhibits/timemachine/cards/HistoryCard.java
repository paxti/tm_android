package com.gwexhibits.timemachine.cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.utils.Utils;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by psyfu on 3/23/2016.
 */
public class HistoryCard extends Card implements Serializable {

    @Bind(R.id.history_record_end_time) Button endTime;
    @Bind(R.id.history_record_start_time) Button startTime;
    @Bind(R.id.history_change_order) Button changeOrder;
    @Bind(R.id.history_record_change_date) Button changeDate;
    @Bind(R.id.history_record_change_phase) Button changePhase;
    @Bind(R.id.history_record_sfid) TextView sfid;

    private Time time;
    private Order order;
    private Callback callback;

    public interface Callback {
        void onStartTimeChange(Time time, HistoryCard card);
        void onOrderChange(Time time, HistoryCard card);
        void onEndTimeChange(Time time, HistoryCard card);
        void onDateChange(Time time, HistoryCard card);
        void onPhaseChange(Time time, HistoryCard card, Order order);
    }

    public HistoryCard(Context context, Time time) {
        super(context);
        this.time = time;
//        this.order = order;
    }

    public HistoryCard(Context context, int innerLayout, Time time, Order order, Callback callback) {
        super(context, innerLayout);
        this.time = time;
        this.callback = callback;
        this.order = order;
        this.setId(time.getOrderId());

/*        CardHeader cardHeader = new CardHeader(getContext());
        cardHeader.setTitle("Some text");

        this.addCardHeader(cardHeader);*/
    }

    public void updateData(){
        endTime.setText(Utils.transformTimeToHuman(time.getEndTime()));
        startTime.setText(Utils.transformTimeToHuman(time.getStartTime()));
        changeDate.setText(Utils.transformDateToHuman(time.getStartTime()));
        sfid.setText(order.getTitleForOptions());
        changePhase.setText(time.getPhase());
    }

    @Override
    public View getInnerView(Context context, ViewGroup parent) {
        setupInnerLayout();
        View view = super.getInnerView(context, parent);

        //This provides a simple implementation with a single title
        if (view != null) {

            if (parent != null) {
                //Add inner view to parent
                parent.removeAllViews();
                parent.addView(view);
            }

            ButterKnife.bind(this, view);
            updateData();
        }
        return view;
    }

    @OnClick(R.id.history_change_order)
    public void changeOrder(Button button) {
        callback.onOrderChange(time, this);
    }

    @OnClick(R.id.history_record_start_time)
    public void changeStartTime(Button button) {
        callback.onStartTimeChange(time, this);
    }

    @OnClick(R.id.history_record_end_time)
    public void changeEndTime(Button button) {
        callback.onEndTimeChange(time, this);
    }

    @OnClick(R.id.history_record_change_date)
    public void changeDate(Button button) {
        callback.onDateChange(time, this);
    }

    @OnClick(R.id.history_record_change_phase)
    public void changePhase(Button button) {
        callback.onPhaseChange(time, this, order);
    }


    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
