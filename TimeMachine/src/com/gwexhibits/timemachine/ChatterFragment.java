package com.gwexhibits.timemachine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gwexhibits.timemachine.objects.pojo.ChatterPost;
import com.gwexhibits.timemachine.objects.pojo.ChatterFeed;
import com.gwexhibits.timemachine.utils.ChatterManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatterFragment extends Fragment implements ChatterShowList, SwipeRefreshLayout.OnRefreshListener {

    private static final String CHATTER_URL = "chatter_url";
    private static final String POST_OBJECT_KEY = "post_object";

    @Bind(R.id.chatter_cards_list) RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_posts) SwipeRefreshLayout pullToRefresh;

    private ChatterAdapter chatterAdapter = null;
    private List<ChatterPost> entries = new ArrayList<>();

    public ChatterFragment() {
    }

    public static ChatterFragment newInstance(String chatterUrl, int type) {
        ChatterFragment fragment = new ChatterFragment();
        Bundle args = new Bundle();
        args.putString(CHATTER_URL, chatterUrl);
        args.putInt(ChatterChat.FEED_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().get(POST_OBJECT_KEY) == null) {
            refreshList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chatter_list, container, false);
        ButterKnife.bind(this, view);

        if (recyclerView instanceof RecyclerView) {
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            chatterAdapter = new ChatterAdapter(getContext(), entries, onCardClickCallback);
            recyclerView.setAdapter(chatterAdapter);
            pullToRefresh.setOnRefreshListener(this);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (getArguments().get(POST_OBJECT_KEY) != null && entries.size() == 0) {
            setAdapterValues((List<ChatterPost>) getArguments().getSerializable(POST_OBJECT_KEY));
        }
    }

    ChatterAdapter.Callback onCardClickCallback = new ChatterAdapter.Callback() {
        @Override
        public void onItemClick(ChatterPost post) {
            ((ChatterChat) getParentFragment()).replaceWithComments(post);
        }
    };

    RestClient.AsyncRequestCallback onChatterDataReceived = new RestClient.AsyncRequestCallback() {
        @Override
        public void onSuccess(RestRequest request, RestResponse response) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.readerFor(ChatterFeed.class);
                ObjectReader jsonReader = mapper.readerFor(ChatterFeed.class);
                ChatterFeed feed = ((ChatterFeed) jsonReader.readValue(response.asString()));
                setAdapterValues(feed.getChatterchatterPosts());
                hideProgress();
                pullToRefresh.setRefreshing(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Exception exception) {
            hideProgress();
        }
    };

    private void setAdapterValues(List<ChatterPost> comments){
        entries.clear();
        for (int i = 0; i < comments.size(); i++ ){
            if (!comments.get(i).getElementType().equals("Bundle")){
                entries.add(comments.get(i));
            }
        }
        chatterAdapter.notifyDataSetChanged();
        getArguments().putSerializable(POST_OBJECT_KEY, (Serializable) entries);
    }

    @Override
    public String getRecordId() {
        return getArguments().getString(CHATTER_URL);
    }

    public int getFeedType() {
        return getArguments().getInt(ChatterChat.FEED_TYPE);
    }

    @Override
    public String getPostType() {
        return ChatterChat.FEED_ITEM;
    }

    private void refreshData(){

        if (getFeedType() == ChatterChat.RECORDS_FEED_TYPE) {

            ChatterManager.getInstance().getFeed(getArguments().getString(CHATTER_URL),
                    onChatterDataReceived);
        } else {
            ChatterManager.getInstance().getFeedTo(getArguments().getString(CHATTER_URL),
                    onChatterDataReceived);
        }
    }

    @Override
    public void refreshList() {
        showProgress();
        refreshData();
    }

    private void showProgress(){
        ((ChatterChat) getParentFragment()).showProgress();
    }

    private void hideProgress(){
        ((ChatterChat) getParentFragment()).hideProgress();
    }

    @Override
    public void onRefresh() {
        refreshData();
    }
}
