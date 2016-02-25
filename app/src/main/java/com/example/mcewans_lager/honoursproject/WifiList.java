package com.example.mcewans_lager.honoursproject;


import java.util.ArrayList;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;


/**
 * Created by mcewans_lager on 25/02/16.
 */
public class WifiList {
    Context mContext;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    static ArrayList<String> wList;


public WifiList(Context context) {
    this.mContext = context;
}

    public void startWifi () {
        mainWifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        mContext.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
    }

    public  static List<String> returnList () {
        return wList;

    }


    public void getList () {
       wList = (ArrayList<String>) receiverWifi.returnList();
    }

    public void register() {
        mContext.registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    public void deRegister() {
        mContext.unregisterReceiver(receiverWifi);
    }



}





class WifiReceiver extends BroadcastReceiver  {


    WifiManager mainWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    static ArrayList<String> wList;

    public void onReceive(Context c, Intent intent) {
        sb = new StringBuilder();
        String[] parts;
        wList = new ArrayList<>();

        wifiList = mainWifi.getScanResults();

        for (int q = 0; q<wifiList.size(); q++) {
            ScanResult result = wifiList.get(q);
            wList.add(result.SSID);
        }

        for (int i = 0; i < wifiList.size(); i++) {
            sb.append((wifiList.get(i)).toString());
            sb.append('\n');
        }

        showList();
    }


    public void showList () {
        for (int i = 0; i<wList.size(); i++) {
            Log.i(wList.get(i), "This is wifi number " + (i + 1));
        }



    }

   public List<String> returnList () {
       return wList;
   }

}