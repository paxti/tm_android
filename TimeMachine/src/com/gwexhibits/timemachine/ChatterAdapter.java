package com.gwexhibits.timemachine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gwexhibits.timemachine.objects.pojo.ChatterPost;
import com.gwexhibits.timemachine.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by psyfu on 4/5/2016.
 */
public class ChatterAdapter extends RecyclerView.Adapter<ChatterCardView>  {

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
    public ChatterCardView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatter_card, parent, false);

        return new ChatterCardView(view);
    }

    @Override
    public void onBindViewHolder(ChatterCardView holder, int position) {

        holder.setData(posts.get(position), callback);
        holder.title.setText(Html.fromHtml(posts.get(position).getHeader().getTittle()));
        holder.content.setText(Html.fromHtml(posts.get(position).getContent()));
        holder.createdDate.setText(Utils.transformDateToHuman(posts.get(position).getCreatedDate()));
        holder.commentsCounter.setText(posts.get(position).getCapabilities().getTotalInString());
        Picasso.with(context)
                .load(posts.get(position).getActor().getPhoto().getPhotoUrl())
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.ic_cancel_black_24dp)
                .fit()
                .into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public List<ChatterPost> getPosts(){
        return this.posts;
    }
}
