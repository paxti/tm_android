package com.gwexhibits.timemachine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gwexhibits.timemachine.objects.pojo.Photo;
import com.gwexhibits.timemachine.utils.DbManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnGalleryItemInteractionListener}
 * interface.
 */
public class GalleryFragment extends Fragment implements GalleryAdapter.OnGalleryItemLongClickListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int columnCount = 2;
    private OnGalleryItemInteractionListener listener;
    private List<Photo> photos = new ArrayList<>();
    private GalleryAdapter galleryAdapter = null;
    private RecyclerView recyclerView = null;

    public GalleryFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GalleryFragment newInstance(int columnCount) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_image_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (columnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
            }
            setGalleryAdapter();
        }

        return view;
    }

    public void refreshView(){
        setGalleryAdapter();
    }

    private void setGalleryAdapter(){
        try {
            photos.clear();
            galleryAdapter = new GalleryAdapter(getContext(), photos, this);
            photos.addAll(DbManager.getInstance().getAllNotUploadedPhotos());
            recyclerView.setAdapter(galleryAdapter);
            recyclerView.setHasFixedSize(false);
            galleryAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGalleryItemInteractionListener) {
            listener = (OnGalleryItemInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onLongClick(Photo photo) {
        listener.onListFragmentInteraction(photo);
    }


    public interface OnGalleryItemInteractionListener {
        void onListFragmentInteraction(Photo photo);
    }
}
