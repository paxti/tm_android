package com.gwexhibits.timemachine.utils;

import android.content.Context;


import com.gwexhibits.timemachine.services.DropboxService;
import com.salesforce.androidsdk.app.SalesforceSDKManager;

/**
 * Created by psyfu on 3/21/2016.
 */
public class DropBoxHelper {

    final static private String APP_KEY = "dy4k23ukfavt3dc";
    final static private String APP_SECRET = "mmh1uz7cue45804";

    private static DropBoxHelper ourInstance = new DropBoxHelper();

//    private DropboxAPI<AndroidAuthSession> dbAPI;

    public static DropBoxHelper getInstance() {
        return ourInstance;
    }

    private DropBoxHelper() {
//        AndroidAuthSession session = buildSession();
//        dbAPI = new DropboxAPI<AndroidAuthSession>(session);
    }

//    public DropboxAPI<AndroidAuthSession> getAPI(){
//        return dbAPI;
//    }

    /*private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    private void loadAuth(AndroidAuthSession session) {

        if(PreferencesManager.getInstance().isDropBoxTokenSet()){
            session.setOAuth2AccessToken(PreferencesManager.getInstance().getDropBoxToken());
        }else{
            AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
            session = new AndroidAuthSession(appKeys);
            dbAPI = new DropboxAPI<AndroidAuthSession>(session);
            dbAPI.getSession().startOAuth2Authentication(SalesforceSDKManager.getInstance().getAppContext());
        }
    }*/
}
