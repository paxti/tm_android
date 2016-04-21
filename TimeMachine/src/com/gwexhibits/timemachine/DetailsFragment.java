package com.gwexhibits.timemachine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.gwexhibits.timemachine.utils.PreferencesManager;
import com.gwexhibits.timemachine.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
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
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        PreferencesManager.getInstance().getPreferences().registerOnSharedPreferenceChangeListener(this);

        if ((cards.get(0) instanceof TaskStatusCard)){
            cards.remove(0);
        }
        cards.add(0, createStatusCard());
        cardArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public  void  onPause() {
        super.onPause();
        PreferencesManager.getInstance().getPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_details_list, container, false);

        if (view instanceof RecyclerView) {
            cardArrayAdapter = new CardArrayRecyclerViewAdapter(getContext(), cards);
            recyclerView = (CardRecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(cardArrayAdapter);
            recyclerView.setHasFixedSize(false);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        if (order.getDropboxLink() != null && !order.getDropboxLink().equals("")){
            ButterKnife.findById(getActivity(), R.id.camera).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesManager.CURRENT_TASK_KEY)) {
            if(PreferencesManager.getInstance().isCurrentTaskRunning()){
                ((TaskStatusCard)cards.get(0)).showStopButton();
            }else{
                ((TaskStatusCard)cards.get(0)).showStartButton();
            }

            if (Utils.isInternetAvailable(getContext())) {
                Intent mServiceIntent = new Intent(getContext(), TimesSyncService.class);
                getContext().startService(mServiceIntent);
            }
        }
    }

    private Card createStatusCard(){
        TaskStatusCard card = new TaskStatusCard(getContext(), R.layout.order_details_status_card);
        card.setData(order, phase);
        return card;
    }
}
