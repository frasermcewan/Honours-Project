package com.example.mcewans_lager.honoursproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by FraserMcEwan on 15/03/16.
 *
 * This class starts the MainService on boot ensuring that the service always run
 */
public class BootReceiver extends BroadcastReceiver {

    protected static final String TAG = "BootService";

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: + Boot Receiver");

        Intent myIntent = new Intent(context, MainService.class);
        context.startService(myIntent);

    }
}
