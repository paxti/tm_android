package com.gwexhibits.timemachine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gwexhibits.timemachine.dummy.DummyContent.DummyItem;
import com.gwexhibits.timemachine.objects.pojo.Photo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<Photo> images = null;
    private OnGalleryItemLongClickListener listener;
    private Context context;

    public GalleryAdapter(Context context, List<Photo> items, OnGalleryItemLongClickListener listener) {
        this.images = items;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_local_image, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String path = images.get(position).getLocalPath();
        Picasso.with(context)
            .load(new File(path))
            .placeholder(R.drawable.ic_build_black_24dp)
            .error(R.drawable.ic_cancel_black_24dp)
            .fit()
            .into(holder.image);

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClick(images.get(position));
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public ImageView image;
        public Photo photo;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            image = (ImageView) view.findViewById(R.id.gallery_item_image);
        }
    }

    public interface OnGalleryItemLongClickListener {
        void onLongClick(Photo photo);
    }
}
