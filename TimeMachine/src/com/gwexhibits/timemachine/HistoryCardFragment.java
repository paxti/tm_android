package com.gwexhibits.timemachine;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.gwexhibits.timemachine.cards.HistoryCard;
import com.gwexhibits.timemachine.dummy.DummyContent;
import com.gwexhibits.timemachine.dummy.DummyContent.DummyItem;
import com.gwexhibits.timemachine.fragments.ChangeOrderFragment;
import com.gwexhibits.timemachine.fragments.TimePickerFragment;
import com.gwexhibits.timemachine.objects.EndAfterStartException;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.pojo.Time;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.utils.DbManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class HistoryCardFragment extends Fragment {

    @Bind(R.id.cards_history_recyclerview) CardRecyclerView recyclerView;
//    @Bind(R.id.history_progress_bar) ProgressBar progressBar;

    private OnListFragmentInteractionListener mListener;

    private ArrayList<Card> cards = new ArrayList<>();
    private CardArrayRecyclerViewAdapter cardArrayAdapter;

    public HistoryCardFragment() {
    }

    @SuppressWarnings("unused")
    public static HistoryCardFragment newInstance() {
        HistoryCardFragment fragment = new HistoryCardFragment();
        //TODO: set search params here
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        DataLoader loader = new DataLoader();
        loader.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historycard_list, container, false);
        ButterKnife.bind(this, view);

        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        cardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);
        recyclerView.setAdapter(cardArrayAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    private class DataLoader extends AsyncTask<Long, Integer, List<HistoryCard>> {

        @Override
        protected List<HistoryCard> doInBackground(Long... params) {
            List<HistoryCard> cardsList = new ArrayList<>();
            try {
                List<Time> times = DbManager.getInstance().getAllTimes();
                for(Time time : times){
                    Order order = DbManager.getInstance().getOrderById(time.getOrderId());
                    HistoryCard card = new HistoryCard(
                            getContext(),
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
//            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected  void onPreExecute(){
//            progressBar.setVisibility(View.VISIBLE);
        }
    }

    HistoryCard.Callback historyCardCallback = new HistoryCard.Callback() {

        @Override
        public void onStartTimeChange(Time time, HistoryCard card) {
            showTimePicker(TimePickerFragment.CHANGE_START_TIME,
                    TimePickerFragment.DIALOG_TYPE_TIME,
                    card,
                    time.getStartTime());
        }

        @Override
        public void onEndTimeChange(Time time, HistoryCard card) {
            showTimePicker(TimePickerFragment.CHANGE_END_TIME,
                    TimePickerFragment.DIALOG_TYPE_TIME,
                    card,
                    time.getEndTime());
        }

        @Override
        public void onOrderChange(Time time, HistoryCard card) {
            DialogFragment changeOrderDialog = new ChangeOrderFragment();
            changeOrderDialog.show(getFragmentManager(), "changeOrder");
        }

        @Override
        public void onDateChange(Time time, HistoryCard card) {
            showTimePicker(TimePickerFragment.CHANGE_DATE,
                    TimePickerFragment.DIALOG_TYPE_DATE,
                    card,
                    time.getStartTime());
        }

        @Override
        public void onPhaseChange(Time time, HistoryCard card, Order order) {
            showTimePicker(TimePickerFragment.CHANGE_DATE,
                    TimePickerFragment.DIALOG_TYPE_SELECTOR,
                    card,
                    time.getStartTime(),
                    order);
        }
    };



    private void showTimePicker(Integer action, int dialogType, HistoryCard card, Date time){
        DialogFragment newFragment = new TimePickerFragment();
        Bundle bundle = generalBundle(action, dialogType, card, time);
        bundle.putInt(TimePickerFragment.DIALOG_TYPE_KEY, dialogType);
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void showTimePicker(Integer action, int dialogType, HistoryCard card, Date time, Order order){
        DialogFragment newFragment = new TimePickerFragment();

        ArrayList<String> phaseOptions = new ArrayList<>();
        Bundle bundle = generalBundle(action, dialogType, card, time);
        bundle.putInt(TimePickerFragment.DIALOG_TYPE_KEY, TimePickerFragment.DIALOG_TYPE_SELECTOR);
        bundle.putStringArray(TimePickerFragment.PHASES_OPTIONS_KEY,
                OrderObject.getPhasesForType(order.getOrderType()));
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private Bundle generalBundle(Integer action, int dialogType, HistoryCard card, Date time){
        Bundle bundle = new Bundle();
        bundle.putSerializable(TimePickerFragment.CARD_ID_KEY, card);
        bundle.putSerializable(TimePickerFragment.TIME_KEY, time);
        bundle.putInt(TimePickerFragment.ACTION_KEY, action);
        return  bundle;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(DummyItem item);
    }


}
