package com.gwexhibits.timemachine;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gwexhibits.timemachine.objects.pojo.ChatterCommentEntity;
import com.gwexhibits.timemachine.objects.pojo.ChatterFeed;
import com.gwexhibits.timemachine.objects.pojo.ChatterPage;
import com.gwexhibits.timemachine.objects.pojo.ChatterPost;
import com.gwexhibits.timemachine.utils.ChatterManager;
import com.gwexhibits.timemachine.utils.ChatterPostAdapter;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by psyfu on 4/7/2016.
 */
public class ChatterPostFragment extends Fragment {

    private static final String CHATTER_POST = "chatter_post";

    @Bind(R.id.chatter_comments_list) RecyclerView recyclerView;

    private ChatterPostAdapter chatterAdapter = null;
    private List<ChatterCommentEntity> comments = new ArrayList<>();

    private ChatterPostFragment.OnFragmentInteractionListener listener;
    private ChatterPost chatterPost;
    ProgressDialog progress;
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

            if(chatterPost.getCapabilities().getCommentsPage().getPage().getNextPageUrl() != null){
                progress = ProgressDialog.show(getActivity(),
                        getString(R.string.load_dialog_title),
                        getString(R.string.load_dialog_text),
                        true);
                ChatterManager.getInstance().getDataFromUrl(
                        chatterPost.getCapabilities().getCommentsPage().getPage().getNextPageUrl(),
                        onChatterDataReceived);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chatter_comments_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            chatterAdapter = new ChatterPostAdapter(getContext(), comments);
            recyclerView.setAdapter(chatterAdapter);
            recyclerView.setHasFixedSize(false);
        }

        return view;
    }

    RestClient.AsyncRequestCallback onChatterDataReceived = new RestClient.AsyncRequestCallback() {
        @Override
        public void onSuccess(RestRequest request, RestResponse response) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.readerFor(ChatterPage.class);
                ObjectReader jsonReader = mapper.readerFor(ChatterPage.class);
                ChatterPage nextPage = (ChatterPage) jsonReader.readValue(response.asString());

                for (ChatterCommentEntity comment : nextPage.getCommentEntityList()){
                    comments.add(comment);
                }
                chatterAdapter.notifyDataSetChanged();
                progress.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Exception exception) {

        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener {
        void onItemViewClicked(String postUrl);

    }
}
