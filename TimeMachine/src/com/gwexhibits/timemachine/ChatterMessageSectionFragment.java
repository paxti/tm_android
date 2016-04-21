package com.gwexhibits.timemachine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gwexhibits.timemachine.objects.pojo.ChatterBody;
import com.gwexhibits.timemachine.objects.pojo.ChatterPostItem;
import com.gwexhibits.timemachine.objects.pojo.Views;
import com.gwexhibits.timemachine.ui.ChatterDelayAutoCompleteTextView;
import com.gwexhibits.timemachine.utils.ChatterManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ChatterMessageSectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatterMessageSectionFragment extends Fragment {
    private static final String RECORD_ID = "chatter_record_id";
    private static final String RECORD_TYPE = "chatter_record_type";
    public static final String FEED_ITEM = "FeedItem";
    public static final String COMMENT_ITEM = "Comment";

    @Bind(R.id.message_text) ChatterDelayAutoCompleteTextView chatterText;
    @Bind(R.id.progress_bar) ProgressBar progressBar;

    private OnFragmentInteractionListener listener;
    private String recordId;
    private String recordType;

    public interface OnFragmentInteractionListener {
        void onChatterPostSentSuccess(RestResponse response);
        void onChatterPostSentError(Exception exception);
    }

    public ChatterMessageSectionFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recordId id of the chatter record.
     * @param recordType type of chatter record.
     * @return A new instance of fragment ChatterMessageSectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatterMessageSectionFragment newInstance(String recordId, String recordType) {
        ChatterMessageSectionFragment fragment = new ChatterMessageSectionFragment();
        Bundle args = new Bundle();
        args.putString(RECORD_ID, recordId);
        args.putString(RECORD_TYPE, recordType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recordId = getArguments().getString(RECORD_ID);
            recordType = getArguments().getString(RECORD_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        ButterKnife.bind(this, view);
        chatterText.setThreshold(2);
        chatterText.setAdapter(new ChatterMentionsAdapter(getContext(), recordId));
        chatterText.setLoadingIndicator(progressBar);
        return view;
    }

    public void clear(){
        chatterText.clear();
    }

    RestClient.AsyncRequestCallback callback = new RestClient.AsyncRequestCallback() {
        @Override
        public void onSuccess(RestRequest request, RestResponse response) {
            listener.onChatterPostSentSuccess(response);
        }

        @Override
        public void onError(Exception exception) {
            listener.onChatterPostSentError(exception);
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

    @OnClick(R.id.send_chatter_message)
    public void sendMessage(View view){

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectWriter writer = mapper.writerWithView(Views.Send.class);

        try {
            String jsonInString = writer.writeValueAsString(buildPost());
            if (recordType.equals(FEED_ITEM)) {
                ChatterManager.getInstance().postNewFeedItem(jsonInString, callback);
            } else {
                ChatterManager.getInstance().postNewComment(jsonInString, recordId, callback);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ChatterPostItem buildPost(){
        ChatterBody body = new ChatterBody();
        body.setSegments(chatterText.getSegments());
        ChatterPostItem post = new ChatterPostItem();
        if (recordType.equals(FEED_ITEM)) {
            post.setSubjectId(recordId);
            post.setElementType(recordType);
        }
        post.setChatterBody(body);

        return post;
    }

}
