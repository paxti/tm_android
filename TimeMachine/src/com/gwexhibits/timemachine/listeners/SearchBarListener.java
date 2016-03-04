package com.gwexhibits.timemachine.listeners;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.ArrayMap;
import android.util.Log;

import com.gwexhibits.timemachine.OrderDetails;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.SearchActivity;
import com.gwexhibits.timemachine.fragments.StagePopUp;
import com.gwexhibits.timemachine.objects.OrderObject;
import com.gwexhibits.timemachine.objects.TimeObject;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

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
                20);

        ArrayList<SearchResult> results = new ArrayList<SearchResult>();
        try {
            JSONArray array = smartStore.query(t, 0);

            for(int i = 0; i < array.length(); i++ ){
                results.add(createOption(array.getJSONObject(i)));
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

        try {

            String[] list = OrderObject.getPhasesForType(result.value.getString(OrderObject.ORDER_TYPE));


            if(list.length > 1){
                FragmentActivity activity = (FragmentActivity) context;

                DialogFragment phaseDialog = new StagePopUp();
                Bundle bundle = new Bundle();
                bundle.putString("order", result.value.toString());
                bundle.putStringArray("options", list);
                phaseDialog.setArguments(bundle);
                phaseDialog.show(activity.getSupportFragmentManager(), "PhaseDialog");
            }else{
                Intent showOrderDetails = new Intent(context, OrderDetails.class);
                showOrderDetails.putExtra("order", result.value.toString());
                context.startActivity(showOrderDetails);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private SearchResult createOption(JSONObject object) throws JSONException {

        String account = object.getJSONObject("Account").getString("Name");
        String sfid = object.getString(OrderObject.SFID);
        String show = android.text.Html.fromHtml(object.getString(OrderObject.SHOW_NAME)).toString();
        String orderNumber = object.getString(OrderObject.ORDER_NUMBER).replaceFirst("^0+(?!$)", "");

        String title = sfid + " (*" + orderNumber + ") " + account + "@" + show;
        int icon;

        switch (object.getString(OrderObject.ORDER_TYPE)){
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
                    object,
                    this.context.getResources().getDrawable(icon, context.getTheme()));
        } else {
            return new SearchResult(title, object, this.context.getDrawable(icon));
        }
    }
}
