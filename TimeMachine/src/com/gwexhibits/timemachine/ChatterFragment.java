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
import android.widget.ProgressBar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gwexhibits.timemachine.objects.pojo.ChatterCommentEntity;
import com.gwexhibits.timemachine.objects.pojo.ChatterPost;
import com.gwexhibits.timemachine.objects.pojo.ChatterFeed;
import com.gwexhibits.timemachine.utils.ChatterManager;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.apache.commons.codec.DecoderException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class ChatterFragment extends Fragment {

    private static final String CHATTER_URL = "chatter_url";
    private static final String POST_OBJECT_KEY = "post_object";

    @Bind(R.id.chatter_cards_list) RecyclerView recyclerView;

    private ChatterAdapter chatterAdapter = null;
    private List<ChatterPost> entries = new ArrayList<>();
    private ProgressDialog progress;

    private OnFragmentInteractionListener mListener;

    public ChatterFragment() {
    }

    public static ChatterFragment newInstance(String chatterUrl) {
        ChatterFragment fragment = new ChatterFragment();
        Bundle args = new Bundle();
        args.putString(CHATTER_URL, chatterUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().get(POST_OBJECT_KEY) == null) {
            if (getArguments().get(CHATTER_URL) == null) {
                ChatterManager.getInstance().getFeed(onChatterDataReceived);
            } else {
                ChatterManager.getInstance().getFeed(getArguments().getString(CHATTER_URL),
                        onChatterDataReceived);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chatter_list, container, false);

        if (view instanceof RecyclerView) {
            recyclerView = (RecyclerView) view;
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            chatterAdapter = new ChatterAdapter(getContext(), entries, onCardClickCallback);
            recyclerView.setAdapter(chatterAdapter);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (getArguments().get(POST_OBJECT_KEY) != null) {
            setAdapterValues((List<ChatterPost>) getArguments().getSerializable(POST_OBJECT_KEY));
        }
    }

    ChatterAdapter.Callback onCardClickCallback = new ChatterAdapter.Callback() {
        @Override
        public void onItemClick(ChatterPost post) {
            mListener.onItemViewClicked(post);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    RestClient.AsyncRequestCallback onChatterDataReceived = new RestClient.AsyncRequestCallback() {
        @Override
        public void onSuccess(RestRequest request, RestResponse response) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.readerFor(ChatterFeed.class);
                ObjectReader jsonReader = mapper.readerFor(ChatterFeed.class);
                ChatterFeed feed = ((ChatterFeed) jsonReader.readValue(response.asString()));
                setAdapterValues(feed.getChatterchatterPosts());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Exception exception) {

        }
    };

    public interface OnFragmentInteractionListener {
        void onItemViewClicked(ChatterPost post);
    }

    /*@Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putSerializable(POST_OBJECT_KEY, (Serializable) chatterAdapter.getPosts());
    }*/

    private void setAdapterValues(List<ChatterPost> comments){

        entries.clear();
        for (int i = 0; i < comments.size(); i++ ){

            if (!comments.get(i).getElementType().equals("Bundle")){
                entries.add(comments.get(i));
//                chatterAdapter.notifyItemInserted(i);
            }
        }
        chatterAdapter.notifyDataSetChanged();
        getArguments().putSerializable(POST_OBJECT_KEY, (Serializable) chatterAdapter.getPosts());

       // progress.dismiss();
    }

}
