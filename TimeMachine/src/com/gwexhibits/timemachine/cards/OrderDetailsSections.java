package com.gwexhibits.timemachine.cards;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.OrderDetailsItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;

/**
 * Created by psyfu on 3/7/2016.
 */
public class OrderDetailsSections extends CardWithList {

    @Nullable @Bind(R.id.title) TextView cardTitle;
    @Nullable @Bind(R.id.details_entry_value) TextView details_description;
    @Nullable @Bind(R.id.action) ImageView actionIcon;
    @Nullable @Bind(R.id.icon) ImageView icon;

    private Context context;
    private List<CardWithList.ListObject> listItems;
    private String title;
    private CardHeader header;
    private int iconResourceId;

    public OrderDetailsSections(Context context, String title, int iconResourceId){
        super(context);
        listItems = new ArrayList<CardWithList.ListObject>();
        header = new CardHeader(context);
        header.setTitle(title);
        this.context = context;
        this.addCardHeader(header);
        this.iconResourceId = iconResourceId;
    }

    public OrderDetailsSections(Context context, String title){
        super(context);
        listItems = new ArrayList<CardWithList.ListObject>();
        header = new CardHeader(context);
        header.setTitle(title);
        this.context = context;
        this.addCardHeader(header);
        this.iconResourceId = -1;
    }

    public List<CardWithList.ListObject> getListItems() {
        return listItems;
    }

    public void setListItems(List<CardWithList.ListObject> listItems) {
        this.listItems = listItems;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addItem(CardWithList.ListObject item){
        listItems.add(item);
    }

    @Override
    protected CardHeader initCardHeader() {
        return null;
    }

    @Override
    protected void initCard() {

    }

    @Override
    protected List<ListObject> initChildren() {
        return listItems;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {
        ButterKnife.bind(this, convertView);

        cardTitle.setText(((OrderDetailsItem) object).getFieldName());
        details_description.setText(Html.fromHtml(((OrderDetailsItem) object).getFieldValue()));

        if(childPosition != 0){
            icon.setVisibility(View.INVISIBLE);
        }

/*        if(iconResourceId < 0) {
            icon.setVisibility(View.GONE);
            actionIcon.setVisibility(View.GONE);
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setImageDrawable(context.getDrawable(iconResourceId));
            }else{
                icon.setImageDrawable(context.getResources().getDrawable(iconResourceId));
            }
        }*/

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.order_details_section_item;
    }
}
