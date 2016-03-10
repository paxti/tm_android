package com.gwexhibits.timemachine.objects.sf;

import android.text.TextUtils;

import com.salesforce.androidsdk.smartstore.store.IndexSpec;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.manager.SyncManager;
import com.salesforce.androidsdk.smartsync.model.SalesforceObject;
import com.salesforce.androidsdk.smartsync.util.Constants;

import org.json.JSONObject;

/**
 * Created by psyfu on 2/24/2016.
 */
public class OrderObject extends SalesforceObject {

    public static final String ORDER_SUPE = "Orders";
    public static final String ORDER_SF_OBJECT = "Order";

    public static final String CONFIGURATION = "Related_Opportunity__r.Configuration__r.";

    public static final String CUSTOM_FAB = "Custom_Fab_Fulfillment_Approved__c";

    public static final String SFID = "Opp_SFID__c";
    public static final String ORDER_TYPE = "Order_Type__c";
    public static final String ORDER_NUMBER = "OrderNumber";
    public static final String SHOW_NAME = "Show_Name__c";
    public static final String CLIENT_NAME = "Account.Name";
    public static final String SHIPPING_DATE = "GOLDEN_Targeted_Shipping_Date__c";
    public static final String INSTRUCTIONS = "Special_Instructions__c";

    public static final String CONFIGURATION_NAME = CONFIGURATION + "Name";
    public static final String CONFIGURATION_TIME_PRE_STAGE = CONFIGURATION + "Estimated_Pre_Stage_Time__c";
    public static final String CONFIGURATION_TIME_UP = CONFIGURATION + "Estimated_I_D_Time_Up__c";
    public static final String CONFIGURATION_TIME_DOWN = CONFIGURATION + "Estimated_I_D_Time_Down__c";
    public static final String CONFIGURATION_TIME_RI = CONFIGURATION + "Estimated_RI_Time__c";

    public static IndexSpec[] ORDERS_INDEX_SPEC = {
            new IndexSpec(Constants.ID, SmartStore.Type.string),
            new IndexSpec(ORDER_TYPE, SmartStore.Type.string),
            new IndexSpec(ORDER_NUMBER, SmartStore.Type.string),
            new IndexSpec(SFID, SmartStore.Type.string),
            new IndexSpec(SHOW_NAME, SmartStore.Type.string),
            new IndexSpec(CLIENT_NAME, SmartStore.Type.string),
            new IndexSpec(SHIPPING_DATE, SmartStore.Type.string),
            new IndexSpec(INSTRUCTIONS, SmartStore.Type.string),
            new IndexSpec(CONFIGURATION_NAME, SmartStore.Type.string),
            new IndexSpec(CONFIGURATION_TIME_PRE_STAGE, SmartStore.Type.string),
            new IndexSpec(CONFIGURATION_TIME_UP, SmartStore.Type.string),
            new IndexSpec(CONFIGURATION_TIME_DOWN, SmartStore.Type.string),
            new IndexSpec(CONFIGURATION_TIME_RI, SmartStore.Type.string)
    };

    public static final String[] ORDER_FIELDS_SYNC_DOWN = {
            Constants.ID,
            ORDER_TYPE,
            ORDER_NUMBER,
            SFID,
            SHOW_NAME,
            CLIENT_NAME,
            CONFIGURATION_NAME,
            CONFIGURATION_TIME_PRE_STAGE,
            CONFIGURATION_TIME_UP,
            CONFIGURATION_TIME_DOWN,
            SHIPPING_DATE,
            CONFIGURATION_TIME_RI,
            INSTRUCTIONS,
            Constants.LAST_MODIFIED_DATE
    };

    public static final String ORDER_SUBMITTED_STATUS = "Order_Submitted__c";
    public static final String ORDER_STATUS = "Status";

    public  static final String[] STATUS_NOT_TO_SYNC = {
            "Draft",
            "Completed"
    };

    public static final String[] LIST_OF_ORDERS_TO_SYNC = {
            "Workorder",
            "SoS",
            "R&R",
            "Custom Fab Request"
    };

    public static final String[] LIST_OF_STAGES_WORKORDER = {
            "Pre-Stage",
            "I&D",
            "RI"
    };

    private boolean isLocallyModified;


    /**
     * Parameterized constructor.
     *
     * @param object Raw data for object.
     */
    public OrderObject(JSONObject object) {
        super(object);


        objectType = "OrderObject";
        objectId = object.optString(Constants.ID);
        name = object.optString(SFID) + " " +
                object.optString(CLIENT_NAME) + " @ " +
                object.optString(SHOW_NAME);

        isLocallyModified = object.optBoolean(SyncManager.LOCALLY_UPDATED) ||
                object.optBoolean(SyncManager.LOCALLY_CREATED) ||
                object.optBoolean(SyncManager.LOCALLY_DELETED);
    }

    /**
     * Returns whether the contact has been locally modified or not.
     *
     * @return True - if the contact has been locally modified, False - otherwise.
     */
    public boolean isLocallyModified() {
        return isLocallyModified;
    }

    private String sanitizeText(String text) {
        if (TextUtils.isEmpty(text) || text.equals(Constants.NULL_STRING)) {
            return Constants.EMPTY_STRING;
        }
        return text;
    }

    public static String buildWhereRequest(){
        return ORDER_TYPE + " IN ('" + TextUtils.join("','", LIST_OF_ORDERS_TO_SYNC) + "')"
                + " AND "
                + "(" + ORDER_SUBMITTED_STATUS + "=True OR " + CUSTOM_FAB + "=True)"
                + " AND "
                + ORDER_STATUS + " NOT IN ('" + TextUtils.join("','", STATUS_NOT_TO_SYNC) + "')";
    }

    public static String[] getPhasesForType(String type){

        switch (type){
            case "Workorder":
                return new String[]{"Pre-Stage", "RI", "I&D"};
            case "SoS":
                return new String[]{"Sos"};
            case "R&R":
                return new String[]{"Assessment", "Fulfilment"};
            case "Custom Fab Request":
                return new String[]{"Custom Fab Request"};
            default:
                return new String[]{"Wrong type"};
        }

    }
}
