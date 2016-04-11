package com.gwexhibits.timemachine;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by psyfu on 4/7/2016.
 */
public class ChatterPostFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String CHATTER_POST = "chatter_post";

    @Bind(R.id.chatter_comments_list) RecyclerView recyclerView;
    @Bind(R.id.autoCompleteTextView) AutoCompleteTextView autoCompleteTextView;
    @Bind(R.id.edit_test_1) EditText editText;

    private ChatterPostAdapter chatterAdapter = null;
    private List<ChatterCommentEntity> comments = new ArrayList<>();

    private ChatterPostFragment.OnFragmentInteractionListener listener;
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

            if(chatterPost.getCapabilities().getCommentsPage().getPage().getNextPageUrl() != null){
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
        ButterKnife.bind(this, view);
        // Set the adapter
//        if (view instanceof RecyclerView) {
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        chatterAdapter = new ChatterPostAdapter(getContext(), comments);
        recyclerView.setAdapter(chatterAdapter);
        recyclerView.setHasFixedSize(false);
//        }

        String[] t = new String[]{"Belgium", "France", "Italy", "Germany", "Spain"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, t);;
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TEXT", "dsfsdfsdfsdf");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    @Override
    public void onRefresh() {

    }

    public interface OnFragmentInteractionListener {
        void onItemViewClicked(String postUrl);

    }

    @OnClick(R.id.button4)
    public void changeDate(Button button) {
        String data = editText.getText().toString();

        String s = "{\n" +
                "\t\"body\":{\n" +
                "\t\t\"messageSegments\":[\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"type\":\"Text\",\n" +
                "\t\t\t\t\"text\":\"" + editText.getText().toString() +  "\"\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t}\n" +
                "}";

        try {
            JSONObject object = new JSONObject(s);
            RestResponse r = ChatterManager.getInstance().postNewComment(object, chatterPost.getPostId());
            s = "";
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    RestClient.AsyncRequestCallback callback = new RestClient.AsyncRequestCallback() {
        @Override
        public void onSuccess(RestRequest request, RestResponse response) {
            int t =1;
            t = 2;
        }

        @Override
        public void onError(Exception exception) {
            int t =1;
            t = 2;
        }
    };

}
