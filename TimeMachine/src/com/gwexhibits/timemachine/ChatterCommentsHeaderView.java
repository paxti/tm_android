package com.gwexhibits.timemachine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by psyfu on 4/20/2016.
 */
public class ChatterCommentsHeaderView extends RecyclerView.ViewHolder {

    @Bind(R.id.chatter_comments_header_title) TextView title;
    @Bind(R.id.chatter_comments_header_content) TextView content;
    @Bind(R.id.chatter_comments_header_date) TextView date;
    @Bind(R.id.chatter_comments_header_icon) ImageView icon;

    public ChatterCommentsHeaderView(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
