package com.gwexhibits.timemachine;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gwexhibits.timemachine.ChatterAdapter;
import com.gwexhibits.timemachine.ChatterCommentsFragment;
import com.gwexhibits.timemachine.ChatterFragment;
import com.gwexhibits.timemachine.ChatterMentionsAdapter;
import com.gwexhibits.timemachine.MainActivity;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.pojo.ChatterBody;
import com.gwexhibits.timemachine.objects.pojo.ChatterPost;
import com.gwexhibits.timemachine.objects.pojo.ChatterPostItem;
import com.gwexhibits.timemachine.objects.pojo.Views;
import com.gwexhibits.timemachine.ui.ChatterDelayAutoCompleteTextView;
import com.gwexhibits.timemachine.utils.ChatterManager;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ChatterChat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatterChat extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    public static final String CHATTER_URL = "chatter_url";
    public static final String FEED_ITEM = "FeedItem";
    public static final String COMMENT_ITEM = "CommentItem";
    public static final String FEED_TYPE = "feed_type";
    public static final int TO_ME_FEED_TYPE = 1;
    public static final int RECORDS_FEED_TYPE = 0;


    @Bind(R.id.message_text) ChatterDelayAutoCompleteTextView chatterText;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.message_send_progress) ProgressBar postProgressBar;

    public ChatterChat() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatterChat.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatterChat newInstance(String chatterUrl, int feedType) {
        ChatterChat fragment = new ChatterChat();
        Bundle args = new Bundle();
        args.putString(CHATTER_URL, chatterUrl);
        args.putInt(FEED_TYPE, feedType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getChildFragmentManager()
            .beginTransaction()
            .replace(R.id.fame_placeholder, ChatterFragment.newInstance(
                    getArguments().getString(CHATTER_URL),
                    getArguments().getInt(FEED_TYPE)), MainActivity.CHATTER_FEED_FRAGMENT)
            .addToBackStack(MainActivity.CHATTER_FEED_FRAGMENT)
            .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chatter_chat, container, false);
        ButterKnife.bind(this, view);

        chatterText.setThreshold(2);
        chatterText.setAdapter(new ChatterMentionsAdapter(getContext(), null));
        chatterText.setLoadingIndicator(progressBar);

        return view;
    }

    RestClient.AsyncRequestCallback onRequestExecuted = new RestClient.AsyncRequestCallback() {
        @Override
        public void onSuccess(RestRequest request, RestResponse response) {
            hideProgress();
            Toast.makeText(getContext(), "Message posted", Toast.LENGTH_LONG);
            ((ChatterShowList) getCurrentFragment()).refreshList();
            chatterText.clear();
        }

        @Override
        public void onError(Exception exception) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG);
        }
    };

    @OnClick(R.id.send_chatter_message)
    public void sendMessage(View view){
        showProgress();
        Utils.hideKeyboard(getContext(), getActivity().getCurrentFocus());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectWriter writer = mapper.writerWithView(Views.Send.class);

        Fragment fragment = getCurrentFragment();

        try {
            String jsonInString = writer.writeValueAsString(buildPost(fragment));
            if (((ChatterShowList) fragment).getPostType().equals(FEED_ITEM)) {
                ChatterManager.getInstance().postNewFeedItem(jsonInString, onRequestExecuted);
            } else {
                ChatterManager.getInstance().postNewComment(jsonInString, ((ChatterShowList) fragment).getRecordId(), onRequestExecuted);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ChatterPostItem buildPost(Fragment fragment){

        ChatterBody body = new ChatterBody();
        body.setSegments(chatterText.getSegments());
        ChatterPostItem post = new ChatterPostItem();
        if (((ChatterShowList) fragment).getPostType().equals(FEED_ITEM)) {
            post.setSubjectId(((ChatterShowList) fragment).getRecordId());
            post.setElementType(FEED_ITEM);
        }
        post.setChatterBody(body);

        return post;
    }

    public void replaceWithComments(ChatterPost post){
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fame_placeholder, ChatterCommentsFragment.newInstance(post), MainActivity.CHATTER_POST_FRAGMENT)
                .addToBackStack(MainActivity.CHATTER_POST_FRAGMENT)
                .commit();
    }

    public Fragment getCurrentFragment(){
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public void showProgress(){
        postProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress(){
        postProgressBar.setVisibility(View.GONE);
    }

}
