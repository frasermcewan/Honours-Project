package com.example.mcewans_lager.honoursproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mcewans_lager on 25/02/16.
 */
public class WifiHolder extends BroadcastReceiver {
    private static final String TAG = "Receiver";
    public static final int REQUEST_CODE = 12345;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: ");


          Intent in = new Intent(context, WifiIntentService.class);

          context.startService(in);

        }








    }
