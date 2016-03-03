package com.example.mcewans_lager.honoursproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;


/**
 * Created by mcewans_lager on 20/01/16.
 */
public class NewMain extends FragmentActivity {

    private static String targetURL = "http://api.openweathermap.org/data/2.5/forecast?q=Glasgow&mode=xml&APPID=b33efea24270c8a29ff5678f3730ecb2";
    private static String secondURL = "http://api.openweathermap.org/data/2.5/forecast?q=Miami&mode=xml&APPID=b33efea24270c8a29ff5678f3730ecb2";
    private final String TAG = "Main";
    private XmlPullParserFactory xmlFactory;
    private ArrayList<String> PrepType = new ArrayList<String>();
    private ArrayList<String> PrepVolume = new ArrayList<String>();
    private ArrayList<String> WindSpeed = new ArrayList<String>();
    private ArrayList<String> tempMax = new ArrayList<String>();
    private ArrayList<String> tempMin = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getWeather();

    }


    public void setPrepType(String p) { PrepType.add(p)  ;}

    public void setPrepVolume(String v) {PrepVolume.add(v);}

    public void setWindSpeed(String w) {WindSpeed.add(w);}

    public void setTempMax(String tMax) {tempMax.add(tMax);}

    public void setTempMin(String tMin) {tempMin.add(tMin);}

    public void getWeather() {
       handleXML handle = new handleXML(secondURL);
        handle.getXML();


        while(!handle.done) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
        }

            if (handle.done) {
                Log.i(TAG, "getWeather: " + handle.getPrepType());
            }


    }




}




