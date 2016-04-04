package com.gwexhibits.timemachine.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.pojo.ChatterCommentEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by psyfu on 4/7/2016.
 */
public class ChatterPostAdapter extends RecyclerView.Adapter<ChatterPostAdapter.ChatterCommentView> {

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
        holder.text1.setText(chatterComments.get(position).getUrl());
        holder.text2.setText(chatterComments.get(position).getActor().getFirstName());
    }

    @Override
    public int getItemCount() {
        return chatterComments.size();
    }

    public class ChatterCommentView extends RecyclerView.ViewHolder {

        @Bind(R.id.textView3) TextView text1;
        @Bind(R.id.textView4) TextView text2;
        @Bind(R.id.textView5) TextView text3;


        public ChatterCommentView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }
}
