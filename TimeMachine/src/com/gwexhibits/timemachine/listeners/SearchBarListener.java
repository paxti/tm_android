package com.gwexhibits.timemachine.listeners;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.gwexhibits.timemachine.OrderDetails;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.objects.OrderObject;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.smartstore.store.QuerySpec;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by psyfu on 2/26/2016.
 */
public class SearchBarListener implements SearchBox.SearchListener {

    private SearchBox searchBox;
    private UserAccount account;
    private SmartStore smartStore;
    private Context context;

    public SearchBarListener(SearchBox search, Context context){
        searchBox = search;
        account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
        this.context = context;
    }


    @Override
    public void onSearchOpened() {

    }

    @Override
    public void onSearchCleared() {

    }

    @Override
    public void onSearchClosed() {

    }

    @Override
    public void onSearchTermChanged(String term) {

        QuerySpec t = QuerySpec.buildLikeQuerySpec(OrderObject.ORDER_SUPE,
                OrderObject.SFID,
                '%' + term + '%',
                null,
                null,
                10);

        JSONArray result = null;
        ArrayList<SearchResult> results = new ArrayList<SearchResult>();
        try {
            result = smartStore.query(t, 0);
            for (int i=0; i< result.length(); i++) {
                results.add(createOption((JSONObject) result.get(i)));
            }
        } catch (JSONException e) {
            Log.e("jhkj", e.getMessage());
            e.printStackTrace();
        }

        searchBox.addAllResults(results);
    }

    @Override
    public void onSearch(String result) {

    }

    @Override
    public void onResultClick(SearchResult result) {
        Intent showOrderDetails = new Intent(context, OrderDetails.class);
        showOrderDetails.putExtra("order", result.value);
        context.startActivity(showOrderDetails);

    }

    private SearchResult createOption(JSONObject object) throws JSONException {

        String account = object.getJSONObject("Account").getString("Name");
        String sfid = object.getString("Opp_SFID__c");
        String show = android.text.Html.fromHtml(object.getString("Show_Name__c")).toString();
        String id = object.getString("_soupEntryId");

        String title = sfid + " " + account + "@" + show;
        int icon;

        switch (object.getString("Order_Type__c")){
            case "Workorder":
                title = "(WoW) " + title;
                icon = R.drawable.ic_work_black_24dp;
                break;

            case "SoS":
                title = "(SoS) " + title;
                icon = R.drawable.ic_build_black_24dp;
                break;

            case "Custom Fab Request":
                title = "(Fab) " + title;
                icon = R.drawable.ic_gavel_black_24dp;
                break;

            case "R&R":
                title = "(R&R) " + title;
                icon = R.drawable.ic_local_shipping_black_24dp;
                break;

            default:
                title = "(Unknown type) " + title;
                icon = R.drawable.ic_warning;
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new SearchResult(title,
                    id,
                    this.context.getResources().getDrawable(icon, context.getTheme()));
        } else {
            return new SearchResult(title, id, this.context.getDrawable(icon));
        }
    }
}
