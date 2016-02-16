package com.example.mcewans_lager.honoursproject;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mcewans_lager on 16/02/16.
 */
public class WifiIntentService extends IntentService {

    private static final String TAG = "Test";


    public WifiIntentService(String name) {
        super("WifiIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        Log.i(TAG,"The service has begun");

    }
}
