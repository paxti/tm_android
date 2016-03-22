package com.gwexhibits.timemachine.objects;

import com.gwexhibits.timemachine.R;
import com.gwexhibits.timemachine.cards.OrderDetailsSections;
import com.gwexhibits.timemachine.objects.pojo.Configuration;
import com.gwexhibits.timemachine.objects.pojo.Order;
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

    private List<OrderDetailsSections> sections;

    public OrderDetails(Order order) {

        sections = new ArrayList<>();

        sections.add(buildGeneralDataSection("General information",
                R.drawable.ic_build_black_24dp,
                order));

        sections.add(buildInstructionDataSection("Other", order));

        if(order.getOpportunity().getConfiguration() != null) {
            sections.add(buildConfigurationDataSection("Configuration",
                    R.drawable.ic_gavel_black_24dp,
                    order.getOpportunity().getConfiguration()));
        }
    }

    public OrderDetailsSections buildGeneralDataSection(String title, int iconResource, Order order){

        OrderDetailsSections section = new OrderDetailsSections(
                SalesforceSDKManager.getInstance().getAppContext(),
                title,
                iconResource);

        section.addItem(new OrderDetailsItem("Order type", order.getOrderType()));
        section.addItem(new OrderDetailsItem("Order number" , order.getOrderNumber()));
        section.addItem(new OrderDetailsItem("SFID" , order.getSfid()));
        section.addItem(new OrderDetailsItem("Show name" , order.getShowName()));
        section.addItem(new OrderDetailsItem("Client name" , order.getAccount().getName()));
        section.addItem(new OrderDetailsItem("Shipping date" , order.getShippingDate()));

        return section;
    }

    public OrderDetailsSections buildConfigurationDataSection(String title, int iconResource,
                                                              Configuration configuration) {

        OrderDetailsSections section = new OrderDetailsSections(
                SalesforceSDKManager.getInstance().getAppContext(),
                title,
                iconResource);

            section.addItem(new OrderDetailsItem("Configuration name", configuration.getName()));
            section.addItem(new OrderDetailsItem("Time pre-stage", configuration.getPreStageTime()));
            section.addItem(new OrderDetailsItem("Time up", configuration.getUpTime()));
            section.addItem(new OrderDetailsItem("Time down", configuration.getDownTime()));
            section.addItem(new OrderDetailsItem("Time RI", configuration.getRiTime()));

        return section;
    }

    public OrderDetailsSections buildInstructionDataSection(String title, Order order){

        OrderDetailsSections section = new OrderDetailsSections(
                SalesforceSDKManager.getInstance().getAppContext(),
                title);

        section.addItem(new OrderDetailsItem("Instructions", order.getInstructions()));

        return section;
    }

    public List<OrderDetailsSections> getDetailsSection(){
        return sections;
    }

}
