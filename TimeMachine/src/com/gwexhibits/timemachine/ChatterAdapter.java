package com.gwexhibits.timemachine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gwexhibits.timemachine.objects.pojo.ChatterPost;
import com.gwexhibits.timemachine.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by psyfu on 4/5/2016.
 */
public class ChatterAdapter extends RecyclerView.Adapter<ChatterAdapter.ChatterCard>  {

    public interface Callback {
        void onItemClick(ChatterPost post);
    }

    private List<ChatterPost> posts = null;
    private Context context;
    private Callback callback;

    public ChatterAdapter(Context context, List<ChatterPost> posts, Callback callback){
        this.posts = posts;
        this.context = context;
        this.callback = callback;
    }

    @Override
    public ChatterCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatter_card, parent, false);

        return new ChatterCard(view);
    }

    @Override
    public void onBindViewHolder(ChatterCard holder, int position) {

        holder.title.setText(Html.fromHtml(posts.get(position).getHeader().getTittle()));
        holder.content.setText(Html.fromHtml(posts.get(position).getContent()));
        holder.createdDate.setText(Utils.transformDateToHuman(posts.get(position).getCreatedDate()));
        holder.commentsCounter.setText(posts.get(position).getCapabilities().getTotalInString());
        Picasso.with(context)
                .load(posts.get(position).getActor().getPhoto().getPhotoUrl())
                .placeholder(R.drawable.ic_build_black_24dp)
                .error(R.drawable.ic_cancel_black_24dp)
                .fit()
                .into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ChatterCard extends RecyclerView.ViewHolder {

        @Bind(R.id.chatter_card_actor_icon) ImageView icon;
        @Bind(R.id.chatter_card_title) TextView title;
        @Bind(R.id.chatter_card_content) TextView content;
        @Bind(R.id.chatter_card_date) TextView createdDate;
        @Bind(R.id.chatter_card_comments_counter) TextView commentsCounter;

        public ChatterCard(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onItemClick(posts.get(getAdapterPosition()));
                }
            });
        }
    }
}
