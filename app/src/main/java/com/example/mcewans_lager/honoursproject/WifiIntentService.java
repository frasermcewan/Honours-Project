package com.example.mcewans_lager.honoursproject;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

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

    public WifiIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Log.i(TAG, "onHandleIntent: ");

        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }


        unregisterReceiver(receiverWifi);


    }

    public void sendIntent() {
        Log.i(TAG, "sendIntent: ");
        Intent l = new Intent(this, MainService.class);
        l.putExtra("Action","Wifi");
        l.putExtra("list", new ArrayListWrapper(theList));
//        sendBroadcast(l);
        startService(l);
        theList.clear();

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

            setLists();

        }


        public void setLists() {
            theList.addAll(holderList);
            sendIntent();
//            sendToReceiver();
        }

    }


}
