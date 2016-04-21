package com.gwexhibits.timemachine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by psyfu on 4/8/2016.
 */
public class ChatterCommentView extends RecyclerView.ViewHolder {

    @Bind(R.id.chatter_comment_icon) ImageView commentIcon;
    @Bind(R.id.chatter_comment_title) TextView commentTitle;
    @Bind(R.id.chatter_comments_content) TextView commentContent;

    public ChatterCommentView(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
