package com.gwexhibits.timemachine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gwexhibits.timemachine.ChatterCardView;
import com.gwexhibits.timemachine.ChatterCommentView;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.pojo.ChatterCommentEntity;
import com.squareup.picasso.Picasso;

import java.util.List;

import it.gmariotti.cardslib.library.recyclerview.internal.BaseRecyclerViewAdapter;

/**
 * Created by psyfu on 4/11/2016.
 */

public class ChatterCommentsAdapter {

}
/*public class ChatterCommentsAdapter extends easyRegularAdapter<ChatterCommentEntity, ChatterCommentView> {

    private Context context;

    public ChatterCommentsAdapter(Context context, List<ChatterCommentEntity> comments){
        super(comments);
        this.context = context;
    }

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.chatter_comment;
    }

    @Override
    protected ChatterCommentView newViewHolder(View view) {
        return new ChatterCommentView(view);
    }

    @Override
    protected void withBindHolder(ChatterCommentView holder, ChatterCommentEntity data, int position) {
        holder.commentTitle.setText(Html.fromHtml(data.getActor().getDisplayName()));
        holder.commentContent.setText(Html.fromHtml(data.getChatterBody().getContent()));
        Picasso.with(context)
                .load(data.getActor().getPhoto().getPhotoUrl())
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.ic_cancel_black_24dp)
                .fit()
                .into(holder.commentIcon);
    }

    public final void insertOne(ChatterCommentEntity e) {
        insertLast(e);
    }

    public final void removeLastOne() {
        removeLast();
    }
}*/
