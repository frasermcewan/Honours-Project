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


//    private WifiManager.WifiLock lock;
//    public ArrayList<String> theList;
//
//    private WifiManager mainWifi;
//    private WifiReceiver receiverWifi;
    Semaphore s;
   boolean h = false;
//    private ArrayList<String> wList;

    private static final String TAG = "Main";

    protected void onCreate(Bundle savedInstanceState) {
//        theList = new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        startWifi();
        startAlarm();



    }



//
//    public void startWifi() {
//
//        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        receiverWifi = new WifiReceiver();
//        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        mainWifi.startScan();
//
//    }
//
//
//    public void acquireLock() {
//        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        mainWifi.setWifiEnabled(true);
//        lock = mainWifi.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "scanOnly");
//        lock.acquire();
//    }

    public void startAlarm() {



            Intent intent = new Intent(this, WifiHolder.class);


            final PendingIntent pIntent = PendingIntent.getBroadcast(this, WifiHolder.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    AlarmManager.INTERVAL_HALF_HOUR, pIntent);



    }

//    public void releaseLock() {
//        lock.release();
//    }

//    class WifiReceiver extends BroadcastReceiver {
//        ArrayList<String> holderList = new ArrayList<String>();
//        ;
//        List<ScanResult> wiList;
//        ScanResult result;
//
//        public void onReceive(Context c, Intent intent) {
////            holderList = new ArrayList<String>();
//
//            wiList = mainWifi.getScanResults();
//
//            for (int q = 0; q < wiList.size(); q++) {
//                ScanResult result = wiList.get(q);
//                holderList.add(result.SSID);
//                Log.i(TAG, "onReceive: ");
//            }
//
//            showList();
//            getListSize();
//            setLists();
////            addToList();
//        }
//
//        public void addToList() {
//            for (int q = 0; q < wiList.size(); q++) {
//                ScanResult result = wiList.get(q);
//                holderList.add(result.SSID);
//            }
//
//
//        }
//
//        public void showList() {
//            for (int i = 0; i < holderList.size(); i++) {
//                Log.i(holderList.get(i), "This is wifi number " + (i + 1));
//            }
//
//        }
//
//
//        public void getListSize() {
//            Log.i(TAG, "Items " + holderList.size());
//
//        }
//
//        public void setLists() {
//            theList.addAll(holderList);
//            startAlarm();
//        }
//
//        public ArrayList<String> getWList() {
//            return holderList;
//        }
//    }
}


