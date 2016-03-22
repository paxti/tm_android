package com.gwexhibits.timemachine.utils;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.gwexhibits.timemachine.services.DropboxService;

/**
 * Created by psyfu on 3/21/2016.
 */
public class DropBoxHelper {
    private static DropBoxHelper ourInstance = new DropBoxHelper();

    private DropboxAPI<AndroidAuthSession> mDBApi;

    public static DropBoxHelper getInstance() {
        return ourInstance;
    }

    private DropBoxHelper() {
        AndroidAuthSession session = buildSession();
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(DropboxService.APP_KEY, DropboxService.APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    private void loadAuth(AndroidAuthSession session) {
        session.setOAuth2AccessToken(PreferencesManager.getInstance().getDropBoxToken());
    }

    public DropboxAPI<AndroidAuthSession> getAPI(){
        return mDBApi;
    }
}
