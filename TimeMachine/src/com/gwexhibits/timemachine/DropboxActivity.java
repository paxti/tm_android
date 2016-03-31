package com.gwexhibits.timemachine;

import android.support.v7.app.AppCompatActivity;

import com.dropbox.core.android.Auth;
import com.gwexhibits.timemachine.utils.DropboxClientFactory;
import com.gwexhibits.timemachine.utils.PreferencesManager;

/**
 * Created by psyfu on 3/22/2016.
 */
public abstract class DropboxActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        String accessToken = PreferencesManager.getInstance().getDropBoxToken();
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                PreferencesManager.getInstance().saveDropBoxToken(accessToken);
                initAndLoadData(accessToken);
            }
        } else {
            initAndLoadData(accessToken);
        }
    }

    private void initAndLoadData(String accessToken) {
        DropboxClientFactory.init(accessToken);
    }

    protected boolean hasToken() {
        return PreferencesManager.getInstance().isDropBoxTokenSet();
    }
}
