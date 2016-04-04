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
import com.gwexhibits.timemachine.objects.pojo.ChatterPost;
import com.gwexhibits.timemachine.objects.pojo.ChatterFeed;
import com.gwexhibits.timemachine.utils.ChatterManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class ChatterFragment extends Fragment {

    private static final String CHATTER_URL = "chatter_url";

    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.chatter_cards_list) RecyclerView recyclerView;

    private ChatterAdapter chatterAdapter = null;
    private List<ChatterPost> entries = new ArrayList<>();
    ProgressDialog progress;
    private String chatterUrl;
    private RestClient restClient;

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
        if (getArguments() != null) {
            chatterUrl = getArguments().getString(CHATTER_URL);
        }

        progress = ProgressDialog.show(getActivity(),
                getString(R.string.load_dialog_title),
                getString(R.string.load_dialog_text),
                true);
        if (chatterUrl == null){
            ChatterManager.getInstance().getFeed(onChatterDataReceived);
        } else {
            ChatterManager.getInstance().getFeed(chatterUrl, onChatterDataReceived);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chatter_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            chatterAdapter = new ChatterAdapter(getContext(), entries, onCardClickCallback);
            recyclerView.setAdapter(chatterAdapter);
            recyclerView.setHasFixedSize(false);
        }

        return view;
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
                ChatterFeed feed = (ChatterFeed) jsonReader.readValue(response.asString());
                entries.clear();

                for (ChatterPost post : feed.getChatterchatterPosts()){
                    if (!post.getElementType().equals("Bundle")){
                        entries.add(post);
                    }
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

    public interface OnFragmentInteractionListener {
        void onItemViewClicked(ChatterPost post);

    }


}
