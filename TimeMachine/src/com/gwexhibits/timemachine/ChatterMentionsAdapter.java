package com.gwexhibits.timemachine;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gwexhibits.timemachine.objects.pojo.ChatterFeed;
import com.gwexhibits.timemachine.objects.pojo.ChatterMention;
import com.gwexhibits.timemachine.objects.pojo.ChatterMentionsList;
import com.gwexhibits.timemachine.utils.ChatterManager;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by psyfu on 4/14/2016.
 */
public class ChatterMentionsAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;

    private final Context context;
    private List<ChatterMention> mentions;
    private String feedContext;

    public ChatterMentionsAdapter(Context context, String feedContext) {
        this.context = context;
        this.feedContext = feedContext;
        this.mentions = new ArrayList<ChatterMention>();
    }

    @Override
    public int getCount() {
        return mentions.size();
    }

    @Override
    public ChatterMention getItem(int index) {
        return mentions.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.mentions_leayout, parent, false);
        }
        ChatterMention mention = getItem(position);
        ((TextView) convertView.findViewById(R.id.text1)).setText(mention.getName());
        ((TextView) convertView.findViewById(R.id.text2)).setText(mention.getDescription());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<ChatterMention> mentions = getMentions(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = mentions;
                    filterResults.count = mentions.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    mentions = (List<ChatterMention>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};

        return filter;
    }

    private List<ChatterMention> getMentions(String text){

        RestResponse response = null;
        ChatterMentionsList list = null;
        try {
            response = ChatterManager.getInstance().getMentions(feedContext, text);

            ObjectMapper mapper = new ObjectMapper();
            mapper.readerFor(ChatterFeed.class);
            ObjectReader jsonReader = mapper.readerFor(ChatterMentionsList.class);
            list = ((ChatterMentionsList) jsonReader.readValue(response.asString()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list.getMentions();
    }

}
