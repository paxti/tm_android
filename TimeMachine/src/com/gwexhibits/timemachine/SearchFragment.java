package com.gwexhibits.timemachine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dropbox.core.android.Auth;
import com.gwexhibits.timemachine.listeners.SearchBarListener;
import com.gwexhibits.timemachine.services.DropboxService;
import com.gwexhibits.timemachine.services.OrdersSyncService;
import com.gwexhibits.timemachine.utils.PreferencesManager;
import com.gwexhibits.timemachine.utils.Utils;
import com.quinny898.library.persistentsearch.SearchBox;
import com.salesforce.androidsdk.smartstore.ui.SmartStoreInspectorActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    @Bind(R.id.main_relative) RelativeLayout relativeLayout;
    @Bind(R.id.searchbox) SearchBox search;
    @Bind(R.id.version) TextView appVersion;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        search.setInputType(InputType.TYPE_CLASS_NUMBER);
        search.setSearchListener(new SearchBarListener(search, getActivity()));
        search.setMaxLength(10);
        search.setLogoText(getString(R.string.search_hint));

        appVersion.setText("v " + BuildConfig.VERSION_NAME);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

     private BroadcastReceiver syncMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.showSnackbar(intent, relativeLayout, Utils.SYNC_BROADCAST_MESSAGE_KEY);
        }
    };

    @OnClick(R.id.button)
    public void sayHi(Button button) {
        Intent mServiceIntent = new Intent(getActivity(), OrdersSyncService.class);
        getActivity().startService(mServiceIntent);
    }

    @OnClick(R.id.button2)
    public void button2Clicked(Button button) {
        final Intent i = new Intent(getActivity() , SmartStoreInspectorActivity.class);
        getActivity().startActivity(i);
    }

    @OnClick(R.id.button3)
    public void button3Clicked(Button button) {

        if (!PreferencesManager.getInstance().isDropBoxTokenSet()) {
            Auth.startOAuth2Authentication(getActivity(), getString(R.string.app_key));
        }
    }
}
