package com.gwexhibits.timemachine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.gwexhibits.timemachine.objects.OrderDetails;
import com.gwexhibits.timemachine.cards.OrderDetailsSections;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.cards.TaskStatusCard;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.smartstore.store.SmartStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class OrderDetailsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int STATUS_CARD_POSITION = 0;

    public static final String ORDER_KEY = "order";
    public static final String PHASE_KEY = "phase";

    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.subtitle) TextView subtitle;
    @Bind(R.id.start_new_task) FloatingActionButton startNewTaskButton;
    @Bind(R.id.cards_recyclerview) CardRecyclerView recyclerView;

    private JSONObject currentOrder;
    private String stage = "";
    private ArrayList<Card> cards = new ArrayList<>();
    private CardArrayRecyclerViewAdapter cardArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        this.setPassedData();
        this.setTitles();

        cardArrayAdapter = new CardArrayRecyclerViewAdapter(this, cards);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideInLeftAnimator());

        if (recyclerView != null) {
            recyclerView.setAdapter(cardArrayAdapter);
        }

        this.loadDataFromDB();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setPassedData(){
        try {
            currentOrder = new JSONObject(getIntent().getStringExtra(ORDER_KEY));
            stage = getIntent().getStringExtra(PHASE_KEY);
        } catch (JSONException e) {
            //TODO: Add message
            e.printStackTrace();
        }
    }

    private void setTitles(){
        String account = this.getResources().getString(R.string.empty);
        String sfid = this.getResources().getString(R.string.error_message);
        String show = this.getResources().getString(R.string.empty);

        if (currentOrder != null){
            try {
                account = Utils.getStringValue(currentOrder, OrderObject.CLIENT_NAME);
                sfid = currentOrder.getString(OrderObject.SFID);
                show = android.text.Html.fromHtml(currentOrder.getString(OrderObject.SHOW_NAME)).toString();
            } catch (JSONException e) {
                // TODO: Add message
                e.printStackTrace();
            }
        }

        collapsingToolbar.setTitle(this.getResources().getString(R.string.sfid_title) + sfid);
        subtitle.setText(account + "@" + show);
    }

    private void loadDataFromDB(){

        int position = 0;

        try {
            OrderDetails details = new OrderDetails(currentOrder.getLong(SmartStore.SOUP_ENTRY_ID));

            for (OrderDetailsSections section : details.getDetailsSection()){
                if (section.getListItems().size() > 0) {
                    cards.add(section);
                    section.init();

                    cardArrayAdapter.notifyItemInserted(position);
                    position++;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.start_new_task)
    public void startTask(View view) {
        try {
            Utils.addCurrentTask(this, currentOrder.getString(SmartStore.SOUP_ENTRY_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void hideStartNewTaskButton(){
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) startNewTaskButton.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        startNewTaskButton.setLayoutParams(p);
        startNewTaskButton.setVisibility(View.GONE);

        TaskStatusCard card = new TaskStatusCard(this, R.layout.order_details_status_card);
        addCardToPosition(card, STATUS_CARD_POSITION);
    }

    private void showStartNewTaskButton(){
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) startNewTaskButton.getLayoutParams();
        p.setAnchorId(R.id.app_bar);
        startNewTaskButton.setLayoutParams(p);

        startNewTaskButton.setVisibility(View.VISIBLE);
        removeCardFromPosition(STATUS_CARD_POSITION);
    }

    private void addCardToPosition(Card card, int position){
        cards.add(position, card);
        cardArrayAdapter.notifyItemInserted(position);
        recyclerView.scrollToPosition(position);
    }

    private void removeCardFromPosition(int position){
        cards.remove(position);
        cardArrayAdapter.notifyItemRemoved(position);
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Utils.CURRENT_ORDER)) {
            if(Utils.isCurrentTaskRunning(this)){
                hideStartNewTaskButton();
            }else{
                showStartNewTaskButton();
            }
        }
    }

}
