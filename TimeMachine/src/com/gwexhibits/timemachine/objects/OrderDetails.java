package com.gwexhibits.timemachine.objects;

import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.cards.OrderDetailsSections;
import com.gwexhibits.timemachine.objects.sf.OrderObject;
import com.gwexhibits.timemachine.utils.Utils;
import com.salesforce.androidsdk.accounts.UserAccount;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by psyfu on 3/7/2016.
 */
public class OrderDetails {

    private static final Map<String, String> GENERAL_DATA_FIELDS = Collections.unmodifiableMap(
            new LinkedHashMap<String, String>() {{
                put(OrderObject.ORDER_TYPE, "Order type");
                put(OrderObject.ORDER_NUMBER, "Order number");
                put(OrderObject.SFID, "SFID");
                put(OrderObject.SHOW_NAME, "Show name");
                put(OrderObject.CLIENT_NAME, "Client name");
                put(OrderObject.SHIPPING_DATE, "Shipping date");
            }}
    );

    private static final Map<String, String> CONFIGURATION_FIELDS = Collections.unmodifiableMap(
            new LinkedHashMap<String, String>() {{
                put(OrderObject.CONFIGURATION_NAME, "Configuration name");
                put(OrderObject.CONFIGURATION_TIME_UP, "Time up");
                put(OrderObject.CONFIGURATION_TIME_DOWN, "Time down");
                put(OrderObject.CONFIGURATION_TIME_PRE_STAGE, "Time pre-stage");
                put(OrderObject.CONFIGURATION_TIME_RI, "Time RI");
            }}
    );

    private static final Map<String, String> INSTRUCTIONS = Collections.unmodifiableMap(
            new LinkedHashMap<String, String>() {{
                put(OrderObject.INSTRUCTIONS, "Instructions");
            }}
    );

    private UserAccount account;
    private SmartStore smartStore;
    private JSONObject dbObject;

    private List<OrderDetailsSections> sections;


    public OrderDetails(Long storeId) {
        account = SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentUser();
        smartStore = SmartSyncSDKManager.getInstance().getSmartStore(account);
        sections = new ArrayList<OrderDetailsSections>();
        try {
            dbObject = smartStore.retrieve(OrderObject.ORDER_SUPE, storeId).getJSONObject(0);
            sections.add(buildSection("General information", GENERAL_DATA_FIELDS, R.drawable.ic_build_black_24dp));
            sections.add(buildSection("Other", INSTRUCTIONS));
            sections.add(buildSection("Configuration", CONFIGURATION_FIELDS, R.drawable.ic_gavel_black_24dp));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private OrderDetailsSections buildSection(String title, Map<String, String> fields, int iconResource){
        OrderDetailsSections section = new OrderDetailsSections(
                SalesforceSDKManager.getInstance().getAppContext(),
                title,
                iconResource);

        return addItemsToSection(fields, section);
    }

    private OrderDetailsSections buildSection(String title, Map<String, String> fields){
        OrderDetailsSections section = new OrderDetailsSections(
                SalesforceSDKManager.getInstance().getAppContext(),
                title);

        return addItemsToSection(fields, section);
    }

    private OrderDetailsSections addItemsToSection(Map<String, String> fields, OrderDetailsSections section){

        for (String fieldKey: fields.keySet()){
            try {
                if( !Utils.getStringValue(dbObject, fieldKey).equals("null")){
                    section.addItem(new OrderDetailsItem(fields.get(fieldKey),
                        Utils.getStringValue(dbObject, fieldKey)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return section;
    }

    public List<OrderDetailsSections> getDetailsSection(){
        return sections;
    }

}
