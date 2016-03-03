package com.gwexhibits.timemachine;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by psyfu on 3/3/2016.
 */
public class OrderDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;

    public OrderDetailsAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case 0:
                View view1 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_details_list_header, parent, false);
                return new ViewHolderHeader(view1);
            default:
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_details_list_item, parent, false);
                return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(position % 4 == 0){
            ViewHolderHeader orderHolder = (ViewHolderHeader) holder;
            orderHolder.title.setText("SubText");
        }else{
            ViewHolder orderHolder = (ViewHolder) holder;
            orderHolder.title.setText("Some Text");
            orderHolder.description.setText("Best description ever");
            orderHolder.details.setText("Something here as well");
            orderHolder.separator.setVisibility(View.GONE);

            orderHolder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("LISTENER", "My position is" + String.valueOf(position));
                }
            });


            if(position % 4 == 3){
                orderHolder.separator.setVisibility(View.GONE);
            }

            if(position % 4 != 1){
                orderHolder.icon.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemViewType(int position){

        return position % 4;
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable @Bind(R.id.title) TextView title;
        @Nullable @Bind(R.id.description) TextView description;
        @Nullable @Bind(R.id.details) TextView details;
        @Nullable @Bind(R.id.separator) View separator;
        @Nullable @Bind(R.id.icon) ImageView icon;
        @Nullable @Bind(R.id.action) ImageView action;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {

        @Nullable @Bind(R.id.title) TextView title;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}


