package com.example.mcewans_lager.honoursproject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mcewans_lager on 16/02/16.
 */
public class WifiIntentService extends IntentService {

    private static final String TAG = "Test";
    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    static ArrayList<String> wList;




    public WifiIntentService() {
        super(TAG);
    }

    protected void onHandleIntent(Intent intent) {
        WifiList wi = new WifiList(this);

        wi.startWifi();
        wi.getList();
        wi.returnList();

        Log.i(TAG, "The service has begun");

    }

    public void startWifi () {
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
        mainText.setText("\nStarting Scan...\n");
    }

}
