package com.gwexhibits.timemachine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gwexhibits.timemachine.cards.HistoryCard;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.pojo.Photo;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.utils.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by psyfu on 4/1/2016.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryCard> historyItems = null;
    private Context context;
    private HistoryAdapter.Callback callback;

    public interface Callback {
        void onStartTimeChange(HistoryCard card);
        void onOrderChange(HistoryCard card);
        void onEndTimeChange(HistoryCard card);
        void onDateChange(HistoryCard card);
        void onPhaseChange(HistoryCard card);
    }

    public HistoryAdapter(Context context, List<HistoryCard> historyCards, HistoryAdapter.Callback callback ){
        this.context = context;
        this.historyItems = historyCards;
        this.callback = callback;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_card, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {


        holder.setData(this.callback,  historyItems.get(position));
        HistoryCard historyCard = historyItems.get(position);
        holder.endTime.setText(Utils.transformTimeToHuman(historyCard.getTime().getEndTime()));
        holder.startTime.setText(Utils.transformTimeToHuman(historyCard.getTime().getStartTime()));
        holder.changeDate.setText(Utils.transformDateToHuman(historyCard.getTime().getStartTime()));
        holder.sfid.setText(historyCard.getOrder().getTitleForOptions());
        holder.changePhase.setText(historyCard.getTime().getPhase());
        holder.textView2.setText(historyCard.getTime().getSyncStatus());
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public void addAll(List<HistoryCard> cards){
        historyItems.clear();
        historyItems.addAll(cards);
        notifyDataSetChanged();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.history_record_end_time) Button endTime;
        @Bind(R.id.history_record_start_time) Button startTime;
        @Bind(R.id.history_change_order) Button changeOrder;
        @Bind(R.id.history_record_change_date) Button changeDate;
        @Bind(R.id.history_record_change_phase) Button changePhase;
        @Bind(R.id.history_record_sfid) TextView sfid;
        @Bind(R.id.textView2) TextView textView2;

        private HistoryAdapter.Callback callback;
        private HistoryCard card;

        public HistoryViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setData(HistoryAdapter.Callback callback, HistoryCard card){
            this.callback = callback;
            this.card = card;
        }

        @OnClick(R.id.history_change_order)
        public void changeOrder(Button button) {
            this.callback.onOrderChange(this.card);
        }

        @OnClick(R.id.history_record_start_time)
        public void changeStartTime(Button button) {
            this.callback.onStartTimeChange(card);
        }

        @OnClick(R.id.history_record_end_time)
        public void changeEndTime(Button button) {
            callback.onEndTimeChange(card);
        }

        @OnClick(R.id.history_record_change_date)
        public void changeDate(Button button) {
            callback.onDateChange(card);
        }

        @OnClick(R.id.history_record_change_phase)
        public void changePhase(Button button) {
            callback.onPhaseChange(card);
        }


    }
}
