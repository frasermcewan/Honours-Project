package com.example.mcewans_lager.honoursproject;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by mcewans_lager on 27/02/16.
 */
public class WifiIntentService extends Service {

    private static final String TAG = "New Intent Service";
    public ArrayList<String> theList = new ArrayList<String>();
    private WifiManager mainWifi;
    WifiReceiver receiverWifi;


    @Override
    public void onCreate() {

        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();


    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiverWifi);
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void sendIntent() {
        Log.i(TAG, "sendIntent: ");
        Intent l = new Intent(this, MainClass.InfoReceiver.class);
        l.putExtra("Action","Wifi");
        l.putExtra("list", new Wrapper(theList));
        sendBroadcast(l);
        theList.clear();
        onDestroy();

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
        }

    }



}
