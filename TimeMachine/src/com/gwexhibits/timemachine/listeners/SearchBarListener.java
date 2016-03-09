package com.gwexhibits.timemachine.listeners;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.gwexhibits.timemachine.OrderDetailsActivity;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.fragments.StagePopUp;
import com.gwexhibits.timemachine.objects.OrderDetails;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.utils.Utils;
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
        Log.d("TAG", "Oppend");
    }

    @Override
    public void onSearchCleared() {
        Log.d("TAG", "Cleared");
    }

    @Override
    public void onSearchClosed() {
        Log.d("TAG", "Closed");
    }

    @Override
    public void onSearchTermChanged(String term) {

        Log.d("TAG", "Term is: " + term);

        QuerySpec querySpec = QuerySpec.buildLikeQuerySpec(OrderObject.ORDER_SUPE,
                OrderObject.SFID,
                '%' + term + '%',
                null,
                null,
                20);

        ArrayList<SearchResult> results = new ArrayList<SearchResult>();
        try {
            JSONArray array = smartStore.query(querySpec, 0);

            for(int i = 0; i < array.length(); i++ ){
                results.add(createOption(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            //TODO: Show error
            e.printStackTrace();
        }

        searchBox.addAllResults(results);
    }

    @Override
    public void onSearch(String result) {
        Log.d("TAG", "On search");
    }

    @Override
    public void onResultClick(SearchResult result) {

        try {
            String[] list = OrderObject.getPhasesForType(result.value.getString(OrderObject.ORDER_TYPE));

            if(list.length > 1){
                FragmentActivity activity = (FragmentActivity) context;

                DialogFragment phaseDialog = new StagePopUp();
                Bundle bundle = new Bundle();
                bundle.putString(OrderDetailsActivity.ORDER_KEY, result.value.toString());
                bundle.putStringArray(StagePopUp.LIST_OF_PHASES_KEY, list);
                phaseDialog.setArguments(bundle);
                phaseDialog.show(activity.getSupportFragmentManager(),
                        context.getString(R.string.stage_dialog_tag));
            }else{
                Intent showOrderDetails = new Intent(context, OrderDetailsActivity.class);
                showOrderDetails.putExtra(OrderDetailsActivity.ORDER_KEY, result.value.toString());
                showOrderDetails.putExtra(OrderDetailsActivity.PHASE_KEY, list[0]);
                context.startActivity(showOrderDetails);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "On result click");

    }

    private SearchResult createOption(JSONObject object) throws JSONException {

        String account = Utils.getStringValue(object, OrderObject.CLIENT_NAME);
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
