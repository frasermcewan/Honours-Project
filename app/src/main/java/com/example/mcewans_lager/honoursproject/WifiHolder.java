package com.example.mcewans_lager.honoursproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

/**
 * Created by mcewans_lager on 25/02/16.
 */
public class WifiHolder extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in = new Intent(context, WifiIntentService.class);
        context.startService(in);
    }
}
