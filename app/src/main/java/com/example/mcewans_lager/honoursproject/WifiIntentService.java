package com.example.mcewans_lager.honoursproject;

import android.app.IntentService;
import android.content.BroadcastReceiver;
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
        Log.i(TAG, "The service has begun");
        startWifi();

    }

    public void startWifi () {
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
    }

    public void register() {
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    public void deRegister() {
        unregisterReceiver(receiverWifi);
    }

    class WifiReceiver extends BroadcastReceiver {


        public void onReceive(Context c, Intent intent) {
            sb = new StringBuilder();
            wList = new ArrayList<>();

            wifiList = mainWifi.getScanResults();

            for (int q = 0; q < wifiList.size(); q++) {
                ScanResult result = wifiList.get(q);
                wList.add(result.SSID);
            }

            for (int i = 0; i < wifiList.size(); i++) {
                sb.append((wifiList.get(i)).toString());
                sb.append('\n');
                sb.append('\n');
            }
            mainText.setText(sb);
            showList();
        }


        public void showList() {
            for (int i = 0; i < wList.size(); i++) {
                Log.i(wList.get(i), "This is wifi number " + (i + 1));
            }

        }
    }

}
