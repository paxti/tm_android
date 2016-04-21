package com.gwexhibits.timemachine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gwexhibits.timemachine.ChatterCommentView;
import com.gwexhibits.timemachine.GalleryAdapter;
import com.gwexhibits.timemachine.objects.pojo.ChatterCommentEntity;
import com.gwexhibits.timemachine.objects.pojo.ChatterPost;
import com.gwexhibits.timemachine.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by psyfu on 4/20/2016.
 */
public class ChatterPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private List<ChatterCommentEntity> comments;
    private ChatterPost post;

    public ChatterPostsAdapter(Context context, List<ChatterCommentEntity> comments, ChatterPost chatterPost){

        this.context = context;
        this.comments = comments;
        this.post = chatterPost;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chatter_comments_header, parent, false);
            return new ChatterCommentsHeaderView(view);

        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chatter_comment, parent, false);
            return new ChatterCommentView(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ChatterCommentsHeaderView ){

            ((ChatterCommentsHeaderView) holder).title.setText(Html.fromHtml(post.getHeader().getTittle()));
            ((ChatterCommentsHeaderView) holder).content.setText(Html.fromHtml(post.getContent()));
            ((ChatterCommentsHeaderView) holder).date.setText(Utils.transformDateToHuman(post.getCreatedDate()));
            Picasso.with(context)
                    .load(post.getActor().getPhoto().getPhotoUrl())
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.ic_cancel_black_24dp)
                    .fit()
                    .into(((ChatterCommentsHeaderView) holder).icon);

        } else {

            ((ChatterCommentView) holder).commentTitle.setText(
                    Html.fromHtml(getItem(position).getActor().getDisplayName()));

            ((ChatterCommentView) holder).commentContent.setText(
                    Html.fromHtml(getItem(position).getChatterBody().getContent()));

            Picasso.with(context)
                    .load(getItem(position).getActor().getPhoto().getPhotoUrl())
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.ic_cancel_black_24dp)
                    .fit()
                    .into(((ChatterCommentView) holder).commentIcon);
        }

    }

    @Override
    public int getItemCount() {
        return comments.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private ChatterCommentEntity getItem(int position) {
        return comments.get(position - 1);
    }
}
