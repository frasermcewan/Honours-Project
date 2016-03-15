package com.example.mcewans_lager.honoursproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mcewans_lager on 15/03/16.
 */
public class BootReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, MainService.class);
        context.startService(myIntent);

    }
}
