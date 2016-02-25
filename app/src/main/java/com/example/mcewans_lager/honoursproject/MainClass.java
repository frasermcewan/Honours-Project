package com.example.mcewans_lager.honoursproject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;

/**
 * Created by mcewans_lager on 25/02/16.
 */
public class MainClass extends Activity {


    private  WifiManager.WifiLock lock;
    private WifiManager mainWifi;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        acquireLock();
        startAlarm();
    }



    public void acquireLock() {
         mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mainWifi.setWifiEnabled(true);
        lock = mainWifi.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "scanOnly");
        lock.acquire();
    }

    public void startAlarm() {
        Intent intent = new Intent(this, WifiHolder.class);

        final PendingIntent pIntent = PendingIntent.getBroadcast(this, WifiHolder.REQUEST_CODE,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_HALF_HOUR, pIntent);
    }

    public void releaseLock() {
        lock.release();
    }

}
