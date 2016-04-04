package com.gwexhibits.timemachine;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @Bind(R.id.cards_history_recyclerview) RecyclerView recyclerView;
//    @Bind(R.id.history_progress_bar) ProgressBar progressBar;

    private OnListFragmentInteractionListener mListener;

    private ArrayList<HistoryCard> cards = new ArrayList<>();
    private CardArrayRecyclerViewAdapter cardArrayAdapter;
    private HistoryAdapter historyAdapter = null;

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

    public void updateData(){
        DataLoader loader = new DataLoader();
        loader.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_image_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            setHistoryAdapter();
        }


        return view;
    }

    private void setHistoryAdapter(){
        cards.clear();
        historyAdapter = new HistoryAdapter(getContext(), cards, historyCardCallback);
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setHasFixedSize(false);
        historyAdapter.notifyDataSetChanged();

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
                            order);
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
            historyAdapter.addAll(result);
        }

        @Override
        protected  void onPreExecute(){
//            progressBar.setVisibility(View.VISIBLE);
        }
    }

    HistoryAdapter.Callback historyCardCallback = new HistoryAdapter.Callback() {

        @Override
        public void onStartTimeChange(HistoryCard card) {
            showTimePicker(TimePickerFragment.CHANGE_START_TIME,
                    TimePickerFragment.DIALOG_TYPE_TIME,
                    card,
                    card.getTime().getStartTime());
        }

        @Override
        public void onEndTimeChange(HistoryCard card) {
            showTimePicker(TimePickerFragment.CHANGE_END_TIME,
                    TimePickerFragment.DIALOG_TYPE_TIME,
                    card,
                    card.getTime().getEndTime());
        }

        @Override
        public void onOrderChange(HistoryCard card) {
            DialogFragment changeOrderDialog = new ChangeOrderFragment();
            changeOrderDialog.show(getFragmentManager(), "changeOrder");
        }

        @Override
        public void onDateChange(HistoryCard card) {
            showTimePicker(TimePickerFragment.CHANGE_DATE,
                    TimePickerFragment.DIALOG_TYPE_DATE,
                    card,
                    card.getTime().getStartTime());
        }

        @Override
        public void onPhaseChange( HistoryCard card) {
            showTimePicker(TimePickerFragment.CHANGE_DATE,
                    TimePickerFragment.DIALOG_TYPE_SELECTOR,
                    card,
                    card.getTime().getStartTime(),
                    card.getOrder());
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
