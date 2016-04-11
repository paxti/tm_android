package com.gwexhibits.timemachine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gwexhibits.timemachine.objects.pojo.ChatterPost;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by psyfu on 4/8/2016.
 */
public class ChatterCardView extends RecyclerView.ViewHolder {

    @Bind(R.id.chatter_card_actor_icon) ImageView icon;
    @Bind(R.id.chatter_card_title) TextView title;
    @Bind(R.id.chatter_card_content) TextView content;
    @Bind(R.id.chatter_card_date) TextView createdDate;
    @Bind(R.id.chatter_card_comments_counter) TextView commentsCounter;

    private ChatterPost card;
    private ChatterAdapter.Callback callback;

    public ChatterCardView(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemClick(card);
            }
        });
    }

    public void setData(ChatterPost card, ChatterAdapter.Callback callback){
        this.card = card;
        this.callback = callback;
    }
}