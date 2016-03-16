package com.example.mcewans_lager.honoursproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mcewans_lager on 15/03/16.
 */
public class BootReceiver extends BroadcastReceiver {

    protected static final String TAG = "BootService";

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: + Boot Receiver" );

        Intent myIntent = new Intent(context, MainService.class);
        context.startService(myIntent);

    }
}
