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
    @Bind(R.id.textView2) TextView syncStatus;

    private Time time;
    private Order order;


    public HistoryCard(Context context, int innerLayout, Time time, Order order) {
        super(context, innerLayout);
        this.time = time;
        this.order = order;
        this.setId(time.getOrderId());
    }

    public void updateData(){
        endTime.setText(Utils.transformTimeToHuman(time.getEndTime()));
        startTime.setText(Utils.transformTimeToHuman(time.getStartTime()));
        changeDate.setText(Utils.transformDateToHuman(time.getStartTime()));
        sfid.setText(order.getTitleForOptions());
        changePhase.setText(time.getPhase());
        syncStatus.setText(time.getSyncStatus());
    }

    @Override
    public View getInnerView(Context context, ViewGroup parent) {
        View view = super.getInnerView(context, parent);

        ButterKnife.bind(this, view);
        updateData();

        return view;
    }


    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
