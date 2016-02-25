package com.example.mcewans_lager.honoursproject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by mcewans_lager on 16/02/16.
 */
public class WifiIntentService extends IntentService {

    private static final String TAG = "Test";




    public WifiIntentService() {
        super(TAG);
    }

    protected void onHandleIntent(Intent intent) {
        Log.i(TAG,"The service has begun");

    }
}
