package com.gwexhibits.timemachine;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gwexhibits.timemachine.objects.pojo.ChatterCommentEntity;
import com.gwexhibits.timemachine.objects.pojo.ChatterPage;
import com.gwexhibits.timemachine.objects.pojo.ChatterPost;
import com.gwexhibits.timemachine.utils.ChatterManager;
import com.gwexhibits.timemachine.utils.Utils;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by psyfu on 4/7/2016.
 */
public class ChatterPostFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String CHATTER_POST = "chatter_post";

    @Bind(R.id.chatter_comments_list) UltimateRecyclerView recyclerView;

    private ChatterCommentsAdapter chatterCommentsAdapter = null;
    private List<ChatterCommentEntity> comments = new ArrayList<>();

    private ChatterPost chatterPost;

    public ChatterPostFragment(){

    }

    public static ChatterPostFragment newInstance(ChatterPost post) {
        ChatterPostFragment fragment = new ChatterPostFragment();
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
            comments = chatterPost.getCapabilities().getCommentsPage().getPage().getCommentEntityList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chatter_comments_list, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatterCommentsAdapter = new ChatterCommentsAdapter(getContext(), comments);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNormalHeader(initHeader(inflater));
        recyclerView.setAdapter(chatterCommentsAdapter);

        return view;
    }

    private View initHeader(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.chatter_comments_header, recyclerView, false);

        TextView title = ButterKnife.findById(view, R.id.chatter_comments_header_title);
        TextView content = ButterKnife.findById(view, R.id.chatter_comments_header_content);
        TextView date = ButterKnife.findById(view, R.id.chatter_comments_header_date);
        ImageView icon = ButterKnife.findById(view, R.id.chatter_comments_header_icon);

        title.setText(Html.fromHtml(chatterPost.getHeader().getTittle()));
        content.setText(Html.fromHtml(chatterPost.getContent()));
        date.setText(Utils.transformDateToHuman(chatterPost.getCreatedDate()));
        Picasso.with(getContext())
                .load(chatterPost.getActor().getPhoto().getPhotoUrl())
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.ic_cancel_black_24dp)
                .fit()
                .into(icon);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        loadAll();
    }

    private void loadAll() {
        if (chatterCommentsAdapter.getItemCount() < chatterPost.getCapabilities().getTotal()){
            ChatterManager.getInstance().getDataFromUrl(
                    chatterPost.getCapabilities().getCommentsPage().getPage().getNextPageUrl(),
                    onChatterDataReceived);
        }
    }

    @Override
    public void onRefresh() {

    }

    RestClient.AsyncRequestCallback onChatterDataReceived = new RestClient.AsyncRequestCallback() {
        @Override
        public void onSuccess(RestRequest request, RestResponse response) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.readerFor(ChatterPage.class);
                ObjectReader jsonReader = mapper.readerFor(ChatterPage.class);
                ChatterPage nextPage = (ChatterPage) jsonReader.readValue(response.asString());

                for (ChatterCommentEntity comment : nextPage.getCommentEntityList()) {
                    comments.add(comment);
                }

                chatterCommentsAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Exception exception) {

        }
    };
}
