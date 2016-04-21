package com.gwexhibits.timemachine;

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
import com.gwexhibits.timemachine.objects.pojo.ChatterCommentEntity;
import com.gwexhibits.timemachine.objects.pojo.ChatterPage;
import com.gwexhibits.timemachine.objects.pojo.ChatterPost;
import com.gwexhibits.timemachine.utils.ChatterManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by psyfu on 4/7/2016.
 */
public class ChatterCommentsFragment extends Fragment implements ChatterShowList, SwipeRefreshLayout.OnRefreshListener {

    private static final String CHATTER_POST = "chatter_post";

    @Bind(R.id.chatter_comments_list) RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_comments) SwipeRefreshLayout pullToRefresh;

    private ChatterPostsAdapter chatterCommentsAdapter = null;
    private List<ChatterCommentEntity> comments = new ArrayList<>();

    private ChatterPost chatterPost;


    public ChatterCommentsFragment(){

    }

    public static ChatterCommentsFragment newInstance(ChatterPost post) {
        ChatterCommentsFragment fragment = new ChatterCommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable(CHATTER_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatterPost = (ChatterPost) getArguments().getSerializable(CHATTER_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chatter_comments_list, container, false);
        ButterKnife.bind(this, view);

        if (recyclerView instanceof RecyclerView) {
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            chatterCommentsAdapter = new ChatterPostsAdapter(getContext(), comments, chatterPost);
            recyclerView.setAdapter(chatterCommentsAdapter);
            pullToRefresh.setOnRefreshListener(this);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        refreshList();
    }

    private void loadComments() {
        try {
            ChatterManager.getInstance().getPostComments(chatterPost.getPostId(), onChatterDataReceived);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    RestClient.AsyncRequestCallback onChatterDataReceived = new RestClient.AsyncRequestCallback() {
        @Override
        public void onSuccess(RestRequest request, RestResponse response) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.readerFor(ChatterPage.class);
                ObjectReader jsonReader = mapper.readerFor(ChatterPage.class);
                setAdapterValues((ChatterPage) jsonReader.readValue(response.asString()));
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

    private void setAdapterValues(ChatterPage pageOfComments){
        comments.clear();
        for (ChatterCommentEntity comment : pageOfComments.getCommentEntityList()) {
            comments.add(comment);
        }
        chatterCommentsAdapter.notifyDataSetChanged();
    }

    @Override
    public String getRecordId() {
        return chatterPost.getPostId();
    }

    @Override
    public String getPostType() {
        return ChatterChat.COMMENT_ITEM;
    }

    @Override
    public void refreshList() {
        showProgress();
        loadComments();
    }

    @Override
    public void onRefresh() {
        loadComments();
    }

    private void showProgress(){
        ((ChatterChat) getParentFragment()).showProgress();
    }

    private void hideProgress(){
        ((ChatterChat) getParentFragment()).hideProgress();
    }
}
