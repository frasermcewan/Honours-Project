package com.example.mcewans_lager.honoursproject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by mcewans_lager on 25/02/16.
 */
public class MainClass extends Activity {


    private static final String TAG = "Main";
    static ArrayList<String> wiList = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startHourlyAlarm();


    }


    public void startHourlyAlarm() {


        Intent intent = new Intent(this, WifiHolder.class);


        final PendingIntent pIntent = PendingIntent.getBroadcast(this, WifiHolder.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                AlarmManager.INTERVAL_HOUR, pIntent);
        


    }


    public void startDailyAlarm() {

        Intent intent = new Intent(this, WifiHolder.class);


        final PendingIntent pIntent = PendingIntent.getBroadcast(this, WifiHolder.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                AlarmManager.INTERVAL_DAY, pIntent);

    }

    public void InstantScan() {

        Intent intent = new Intent(this, WifiHolder.class);
        startService(intent);

    }

  public static class InfoReceiver extends BroadcastReceiver {
        @Override



        public void onReceive(Context context, Intent intent) {

                String ActionName = intent.getStringExtra("Action");

            if(ActionName.equals("Wifi")) {
                Log.i(TAG, "onReceiveMain: In Action");
                Wrapper w = (Wrapper) intent.getSerializableExtra("list");
                wiList = w.getNames();
                Log.i(TAG, "onReceiveMain: " + wiList);
            } else if (ActionName.equals("Main")) {
                Log.i(TAG, "onReceive: Should be here");
            }

        }
    }


}


