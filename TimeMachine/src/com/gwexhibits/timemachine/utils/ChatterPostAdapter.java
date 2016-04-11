package com.gwexhibits.timemachine.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gwexhibits.timemachine.ChatterCommentView;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.pojo.ChatterCommentEntity;

import java.util.List;


/**
 * Created by psyfu on 4/7/2016.
 */
public class ChatterPostAdapter extends RecyclerView.Adapter<ChatterCommentView> {

    List<ChatterCommentEntity> chatterComments;
    private Context context;

    public ChatterPostAdapter(Context context, List<ChatterCommentEntity> comments){
        this.context = context;
        this.chatterComments = comments;
    }

    @Override
    public ChatterCommentView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatter_comment, parent, false);

        return new ChatterCommentView(view);
    }

    @Override
    public void onBindViewHolder(ChatterCommentView holder, int position) {
        holder.getText1().setText(chatterComments.get(position).getActor().getDisplayName());
        holder.getText2().setText(chatterComments.get(position).getChatterBody().getContent());
    }

    @Override
    public int getItemCount() {
        return chatterComments.size();
    }
}
