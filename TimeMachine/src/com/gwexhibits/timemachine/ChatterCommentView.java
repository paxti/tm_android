package com.gwexhibits.timemachine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by psyfu on 4/8/2016.
 */
public class ChatterCommentView extends RecyclerView.ViewHolder {

    @Bind(R.id.textView3) TextView text1;
    @Bind(R.id.textView4) TextView text2;
    @Bind(R.id.textView5) TextView text3;


    public ChatterCommentView(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getText1() {
        return text1;
    }

    public TextView getText2() {
        return text2;
    }

    public TextView getText3() {
        return text3;
    }
}
