package com.example.mcewans_lager.honoursproject;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by mcewans_lager on 25/02/16.
 */
public class wifiHolder extends BroadcastReceiver {
    private static final String TAG = "Receiver";
    public static final int REQUEST_CODE = 12345;


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent in = new Intent(context, WifiIntentService.class);
        context.startService(in);


    }








}