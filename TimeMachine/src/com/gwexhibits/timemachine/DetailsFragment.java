package com.gwexhibits.timemachine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gwexhibits.timemachine.cards.OrderDetailsSections;
import com.gwexhibits.timemachine.cards.TaskStatusCard;
import com.gwexhibits.timemachine.objects.OrderDetails;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.services.TimesSyncService;
import com.gwexhibits.timemachine.utils.DbManager;
import com.gwexhibits.timemachine.utils.PreferencesManager;
import com.gwexhibits.timemachine.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

/**
 * Created by psyfu on 4/6/2016.
 */
public class DetailsFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int STATUS_CARD_POSITION = 0;
    private static final String ORDER_KEY = "orderId";
    private static final String PHASE_KEY = "phase";

    @Bind(R.id.order_details_cards_list) CardRecyclerView recyclerView;

    private ArrayList<Card> cards = new ArrayList<>();
    private CardArrayRecyclerViewAdapter cardArrayAdapter;
    private Order order;
    private String phase;

    public static DetailsFragment newInstance(Order order, String phase) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ORDER_KEY, order);
        args.putString(PHASE_KEY, phase);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable(ORDER_KEY);
            phase = getArguments().getString(PHASE_KEY);
            /*DataLoader runner = new DataLoader();
            runner.execute(orderId);*/
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                PreferencesManager.PREF_NAME,
                Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_details_list, container, false);

        /*if(PreferencesManager.getInstance().isCurrentTaskRunning()){
            hideStartNewTaskButton();
        }*/

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (CardRecyclerView) view;
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            cardArrayAdapter = new CardArrayRecyclerViewAdapter(getContext(), cards);
            recyclerView.setAdapter(cardArrayAdapter);
            recyclerView.setHasFixedSize(false);
        }

        TaskStatusCard card = new TaskStatusCard(getContext(), R.layout.order_details_status_card);
        card.setData(order, phase);
        cards.add(card);

        int position = cards.size();
        OrderDetails details = new OrderDetails(order);
        for (OrderDetailsSections section : details.getDetailsSection()){
            if (section.getListItems().size() > 0) {
                cards.add(section);
                section.init();

                cardArrayAdapter.notifyItemInserted(position);
                position++;
            }
        }

        return view;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /*if (key.equals(PreferencesManager.CURRENT_TASK_KEY)) {
            if(PreferencesManager.getInstance().isCurrentTaskRunning()){
                hideStartNewTaskButton();
            }else{
                showStartNewTaskButton();
                if (Utils.isInternetAvailable(getActivity())) {
                    Intent mServiceIntent = new Intent(getActivity(), TimesSyncService.class);
                    getActivity().startService(mServiceIntent);
                }
            }
        }*/
    }

    private void hideStartNewTaskButton(){
        /*CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) startNewTaskButton.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        startNewTaskButton.setLayoutParams(p);
        startNewTaskButton.setVisibility(View.GONE);
        */

        TaskStatusCard card = new TaskStatusCard(getContext(), R.layout.order_details_status_card);
        addCardToPosition(card, STATUS_CARD_POSITION);
    }

    private void showStartNewTaskButton(){
        /*CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) startNewTaskButton.getLayoutParams();
        p.setAnchorId(R.id.app_bar);
        startNewTaskButton.setLayoutParams(p);*/

//        startNewTaskButton.setVisibility(View.VISIBLE);
        removeCardFromPosition(STATUS_CARD_POSITION);
    }

    private void addCardToPosition(Card card, int position){
        cards.add(position, card);
        cardArrayAdapter.notifyItemChanged(position);
        cardArrayAdapter.notifyItemInserted(position);
        recyclerView.scrollToPosition(position);
    }

    private void removeCardFromPosition(int position) {
        cards.remove(position);
        cardArrayAdapter.notifyItemRemoved(position);
        recyclerView.scrollToPosition(position);
    }

    /*private class DataLoader extends AsyncTask<Long, Integer, String> {

        Order currentOrder;

        @Override
        protected String doInBackground(Long... params) {

            try {
                if (params[0] > 0) {
                    currentOrder = DbManager.getInstance().getOrderObject(params[0]);
                }else{
                    currentOrder = DbManager.getInstance().getOrderObject();
                }

            } catch (JSONException jsonex) {
                jsonex.printStackTrace();
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }
            return currentOrder.getId();
        }

        protected void onPostExecute(String result) {
            loadDataFromDB(currentOrder);
        }

        private void loadDataFromDB(Order order){

            int position = cards.size();
            OrderDetails details = new OrderDetails(order);

            for (OrderDetailsSections section : details.getDetailsSection()){
                if (section.getListItems().size() > 0) {
                    cards.add(section);
                    section.init();

                    cardArrayAdapter.notifyItemInserted(position);
                    position++;
                }
            }
        }
    }*/
}
