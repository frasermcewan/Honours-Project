package com.example.mcewans_lager.honoursproject;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mcewans_lager on 27/02/16.
 */
public class WifiIntentService extends IntentService {

    private static final String TAG = "New Intent Service";
    public ArrayList<String> theList = new ArrayList<String>();
    private WifiManager mainWifi;
    WifiReceiver receiverWifi;
    PowerManager.WakeLock wakeLock;
    Boolean list = false;


    public WifiIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "onHandleIntent: ");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();


        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        
        mainWifi.startScan();

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }


        unregisterReceiver(receiverWifi);


    }


    public void releaseLock() {
        Log.i(TAG, "releaseLock: ");
        wakeLock.release();

    }

    class WifiReceiver extends BroadcastReceiver {
        ArrayList<String> holderList = new ArrayList<String>();
        List<ScanResult> wiList;

        public void onReceive(Context c, Intent intent) {

            wiList = mainWifi.getScanResults();

            for (int q = 0; q < wiList.size(); q++) {
                ScanResult result = wiList.get(q);
                holderList.add(result.SSID);

            }

            showList();
            getListSize();
            setLists();

        }


        public void showList() {
            for (int i = 0; i < holderList.size(); i++) {
                Log.i(holderList.get(i), "This is wifi number " + (i + 1));
            }

        }


        public void getListSize() {
            Log.i(TAG, "Items " + holderList.size());

        }

        public void setLists() {
            theList.addAll(holderList);
            releaseLock();
        }

        public ArrayList<String> getWList() {
            return holderList;
        }
    }


}
