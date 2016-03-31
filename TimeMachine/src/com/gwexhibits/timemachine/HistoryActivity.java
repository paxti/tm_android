package com.gwexhibits.timemachine;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gwexhibits.timemachine.cards.HistoryCard;
import com.gwexhibits.timemachine.fragments.ChangeOrderFragment;
import com.gwexhibits.timemachine.fragments.TimePickerFragment;
import com.gwexhibits.timemachine.objects.EndAfterStartException;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.utils.DbManager;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class HistoryActivity extends AppCompatActivity {

    @Bind(R.id.cards_history_recyclerview) CardRecyclerView recyclerView;
    @Bind(R.id.history_progress_bar) ProgressBar progressBar;

    private ArrayList<Card> cards = new ArrayList<>();
    private CardArrayRecyclerViewAdapter cardArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cardArrayAdapter = new CardArrayRecyclerViewAdapter(this, cards);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setItemAnimator(new SlideInLeftAnimator());

        if (recyclerView != null) {
            recyclerView.setAdapter(cardArrayAdapter);
        }
    }

   /* @Override
    public void onResume(){
        super.onResume();
        DataLoader loader = new DataLoader();
        loader.execute();
    }*/

   /* private class DataLoader extends AsyncTask<Long, Integer, List<HistoryCard>> {

        @Override
        protected List<HistoryCard> doInBackground(Long... params) {
            List<HistoryCard> cardsList = new ArrayList<>();
            try {
                List<Time> times = DbManager.getInstance().getAllTimes();
                for(Time time : times){
                    Order order = DbManager.getInstance().getOrderById(time.getOrderId());
                    HistoryCard card = new HistoryCard(
                            getApplicationContext(),
                            R.layout.history_card,
                            time,
                            order,
                            historyCardCallback );
                    cardsList.add(card);
                }
            } catch (JSONException jsonex) {
                jsonex.printStackTrace();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }
            return cardsList;
        }

        @Override
        protected void onPostExecute(List<HistoryCard> result) {
            cards.clear();
            cardArrayAdapter.clear();
            cardArrayAdapter.notifyDataSetChanged();

            int position = cards.size();
            for (HistoryCard card : result){
                cards.add(card);
                cardArrayAdapter.notifyItemInserted(position);
                position++;
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected  void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }
    }
*/
    /*HistoryCard.Callback historyCardCallback = new HistoryCard.Callback() {

        @Override
        public void onStartTimeChange(Time time, String cardId) {
            showTimePicker(TimePickerFragment.CHANGE_START_TIME,
                    TimePickerFragment.DIALOG_TYPE_TIME,
                    cardId,
                    time.getStartTime());
        }

        @Override
        public void onEndTimeChange(Time time, String cardId) {
            showTimePicker(TimePickerFragment.CHANGE_END_TIME,
                    TimePickerFragment.DIALOG_TYPE_TIME,
                    cardId,
                    time.getEndTime());
        }

        @Override
        public void onOrderChange(Time time, String cardId) {
           DialogFragment changeOrderDialog = new ChangeOrderFragment();
           changeOrderDialog.show(getSupportFragmentManager(), "changeOrder");
        }

        @Override
        public void onDateChange(Time time, String cardId) {
            showTimePicker(TimePickerFragment.CHANGE_DATE,
                    TimePickerFragment.DIALOG_TYPE_DATE,
                    cardId,
                    time.getStartTime());
        }

        @Override
        public void onPhaseChange(Time time, String cardId, Order order) {
            showTimePicker(TimePickerFragment.CHANGE_DATE,
                    TimePickerFragment.DIALOG_TYPE_SELECTOR,
                    cardId,
                    time.getStartTime(),
                    order);
        }
    };

    //TODO: may be change
    @Override
    public void onCompleteChangeStartTime(Date time, String cardId) {
        try {
            HistoryCard card = (HistoryCard) findCardById(cardId);
            Time timeObject = card.getTime();
            timeObject.changeStartTime(time);
            saveTimeObject(timeObject, card);
        } catch (EndAfterStartException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompleteChangeEndTime(Date time, String cardId) {

        try {
            HistoryCard card = (HistoryCard) findCardById(cardId);
            Time timeObject = card.getTime();
            timeObject.changeEndTime(time);
            saveTimeObject(timeObject, card);
        } catch (EndAfterStartException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompleteChangeDate(Date date, String cardId) {
        HistoryCard card = (HistoryCard) findCardById(cardId);
        Time timeObject = card.getTime();
        timeObject.changeDate(date);
        saveTimeObject(timeObject, card);
    }

    @Override
    public void onCompleteChangePhase(String phase, String cardId) {
        HistoryCard card = (HistoryCard) findCardById(cardId);
        Time timeObject = card.getTime();
        timeObject.changePhase(phase);
        saveTimeObject(timeObject, card);
    }
*/
   /* private void showTimePicker(Integer action, int dialogType, String cardId, Date time){
        DialogFragment newFragment = new TimePickerFragment();
        Bundle bundle = generalBundle(action, dialogType, cardId, time);
        bundle.putInt(TimePickerFragment.DIALOG_TYPE_KEY, dialogType);
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showTimePicker(Integer action, int dialogType, String cardId, Date time, Order order){
        DialogFragment newFragment = new TimePickerFragment();

        ArrayList<String> phaseOptions = new ArrayList<>();
        Bundle bundle = generalBundle(action, dialogType, cardId, time);
        bundle.putInt(TimePickerFragment.DIALOG_TYPE_KEY, TimePickerFragment.DIALOG_TYPE_SELECTOR);
        bundle.putStringArray(TimePickerFragment.PHASES_OPTIONS_KEY,
                OrderObject.getPhasesForType(order.getOrderType()));
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private Bundle generalBundle(Integer action, int dialogType, String cardId, Date time){
        Bundle bundle = new Bundle();
        bundle.putString(TimePickerFragment.CARD_ID_KEY, cardId);
        bundle.putSerializable(TimePickerFragment.TIME_KEY, time);
        bundle.putInt(TimePickerFragment.ACTION_KEY, action);
        return  bundle;
    }

    private Card findCardById(String cardId){
        Card cardWithId = null;
        for (Card card : cards){
            if (card.getId().equals(cardId)){
                cardWithId = card;
                break;
            }
        }

        return cardWithId;
    }

    private void saveTimeObject(Time timeObject, HistoryCard card){
        Time newTimeObject = null;
        try {
            Time newTimeObject = DbManager.getInstance().updateTime(timeObject);
            card.setTime(newTimeObject);
            card.updateData();
            card.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
