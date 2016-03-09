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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }


        unregisterReceiver(receiverWifi);


    }

    public void sendIntent() {
        Intent l = new Intent(this, MainService.class);
        l.putExtra("Action","Wifi");
        l.putExtra("list", new ArrayListWrapper(theList));
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
            clearDuplicates();
        }

        private void clearDuplicates() {
            Set<String> holderSet = new LinkedHashSet<>(theList);
            theList.clear();
            theList.addAll(holderSet);
            sendIntent();

        }

    }


}
