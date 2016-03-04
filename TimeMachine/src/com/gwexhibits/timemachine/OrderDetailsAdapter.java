package com.gwexhibits.timemachine;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by psyfu on 3/3/2016.
 */
public class OrderDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Integer> elements;
    private Context context;

    public OrderDetailsAdapter(Context context) {
        this.context = context;
        elements = new ArrayList<Integer>();
        elements.add(0);
        elements.add(0);
        elements.add(0);
        elements.add(1);
        elements.add(0);
        elements.add(0);
        elements.add(0);
        elements.add(1);
        elements.add(0);
        elements.add(0);
        elements.add(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case 1:
                View headerView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_details_list_header, parent, false);
                return new ViewHolderHeader(headerView);
            case 2:
                View statusView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_status, parent, false);
                return new ViewHolderStatus(statusView);
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_details_list_item, parent, false);
                return new ViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (getItemViewType(position)) {
            case 1:
                ViewHolderHeader orderHeader = (ViewHolderHeader) holder;
                orderHeader.title.setText("SubText");
                break;
            case 2:
                ViewHolderStatus orderStatus = (ViewHolderStatus) holder;
                orderStatus.text.setText("Your current task is: ");
                break;
            default:
                ViewHolder orderHolder = (ViewHolder) holder;
                orderHolder.title.setText("Some Text");
                orderHolder.description.setText("Best description ever");
                orderHolder.details.setText("Something here as well");
                break;
        }
    }

    @Override
    public int getItemViewType(int position){
        return getElements().get(position);
    }

    @Override
    public int getItemCount() {
        return getElements().size();
    }

    public void addElement(Integer type, int position) {
        getElements().add(position, type);
        notifyItemInserted(position);
    }

    public void removeElement(Integer position) {
        getElements().remove(position);
        notifyItemRemoved(position);
    }

    public List<Integer> getElements(){
        return this.elements;
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

    public class ViewHolderStatus extends RecyclerView.ViewHolder {

        @Nullable @Bind(R.id.some_text) TextView text;

        public ViewHolderStatus(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.stop_task)
        public void stopTask(Button button) {
            SharedPreferences settings = context.getSharedPreferences(OrderDetails.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(OrderDetails.CURRENT_ORDER, "");
            editor.commit();
        }
    }
}


