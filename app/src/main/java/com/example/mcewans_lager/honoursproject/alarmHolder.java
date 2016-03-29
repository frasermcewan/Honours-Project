package com.example.mcewans_lager.honoursproject;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by FraserMcEwan on 25/02/16.
 *
 * This class works in tandem with the AlarmManager declared in the MainService, The alarmManager
 * contacts this receiver which in turns starts or polls services which then return the information
 * to the MainService
 *
 */
public class alarmHolder extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;


    @Override
    public void onReceive(Context context, Intent intent) {

        /**
         * This method is called when the AlarmManager contacts it, which then polls the services
         * mentioned below
         */


        Intent in = new Intent(context, WifiIntentService.class);
        context.startService(in);


        Intent gp = new Intent(context, LocationService.class);
        gp.putExtra("Action", "GPS");
        context.startService(gp);


    }


}