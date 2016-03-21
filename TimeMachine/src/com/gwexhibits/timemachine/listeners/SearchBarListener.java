package com.gwexhibits.timemachine.listeners;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.gwexhibits.timemachine.OrderDetailsActivity;
import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.fragments.StagePopUp;
import com.gwexhibits.timemachine.objects.OrderDetails;
import com.gwexhibits.timemachine.objects.pojo.Attribute;
import com.gwexhibits.timemachine.objects.pojo.Order;
import com.gwexhibits.timemachine.objects.pojo.Views;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.objects.sf.TimeObject;
import com.gwexhibits.timemachine.utils.Utils;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.smartstore.store.QuerySpec;
import com.salesforce.androidsdk.smartstore.store.SmartSqlHelper;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by psyfu on 2/26/2016.
 */
public class SearchBarListener implements SearchBox.SearchListener {

    private SearchBox searchBox;
    private UserAccount account;
    private SmartStore smartStore;
    private Context context;

    private ObjectMapper mapper;
    private final ObjectReader jsonReader;

    public SearchBarListener(SearchBox search, Context context){
        searchBox = search;
        account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        this.context = context;

        mapper = new ObjectMapper();
        mapper.writerWithView(Views.Full.class);
        jsonReader = mapper.reader(Order.class);
    }

    @Override
    public void onSearchOpened() {
        smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
        smartStore.registerSoup(TimeObject.TIME_SUPE, TimeObject.TIMES_INDEX_SPEC);
        smartStore.registerSoup(OrderObject.ORDER_SUPE, OrderObject.ORDERS_INDEX_SPEC);
    }

    @Override
    public void onSearchCleared() {
    }

    @Override
    public void onSearchClosed() {
    }

    @Override
    public void onSearchTermChanged(String term) {

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
                results.add(createOption((Order)jsonReader.readValue(array.getJSONObject(i).toString())));
            }
        } catch (JSONException e) {
            //TODO: Show error
            Log.e("Error", e.getMessage());
        }catch (SmartSqlHelper.SmartSqlException smartStoreException){
            Log.e("Error", smartStoreException.getMessage());
        }catch (IOException ioe){
            Log.e("Error", ioe.getMessage());
        }

        searchBox.addAllResults(results);
    }

    @Override
    public void onSearch(String result) {
    }

    @Override
    public void onResultClick(SearchResult result) {


        String[] list = OrderObject.getPhasesForType(((Order)result.value).getOrderType());

        if(list.length > 1){
            FragmentActivity activity = (FragmentActivity) context;

            DialogFragment phaseDialog = new StagePopUp();
            Bundle bundle = new Bundle();
            bundle.putSerializable(OrderDetailsActivity.ORDER_KEY, ((Order) result.value).getEntyId());
            bundle.putStringArray(StagePopUp.LIST_OF_PHASES_KEY, list);
            phaseDialog.setArguments(bundle);
            phaseDialog.show(activity.getSupportFragmentManager(),
                    context.getString(R.string.stage_dialog_tag));
        }else{
            Intent showOrderDetails = new Intent(context, OrderDetailsActivity.class);
            showOrderDetails.putExtra(OrderDetailsActivity.ORDER_KEY, ((Order) result.value).getEntyId());
            showOrderDetails.putExtra(OrderDetailsActivity.PHASE_KEY, list[0]);
            context.startActivity(showOrderDetails);
        }

        Log.d("TAG", "On result click");
    }

    private SearchResult createOption(Order order) throws JSONException {

        String title = order.getTitleForOptions();
        int icon;

        switch (order.getOrderType()){
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
                    order,
                    this.context.getResources().getDrawable(icon, context.getTheme()));
        } else {
            return new SearchResult(title, order, this.context.getDrawable(icon));
        }
    }
}
