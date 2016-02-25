package com.example.mcewans_lager.honoursproject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class Wifi extends Activity {

    
    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    static ArrayList<String> wList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wefee);
        Intent intent = new Intent(this, WifiIntentService.class);
        startService(intent);
        startWifi();



    }



    public void startWifi () {
        mainText = (TextView) findViewById(R.id.mainText);
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
        mainText.setText("\nStarting Scan...\n");
    }


    public  static List<String> returnList () {
        return wList;

    }

    public void register() {
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    public void deRegister() {
        unregisterReceiver(receiverWifi);
    }
    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }


    class WifiReceiver extends BroadcastReceiver {


        public void onReceive(Context c, Intent intent) {
            sb = new StringBuilder();
            String[] parts;
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