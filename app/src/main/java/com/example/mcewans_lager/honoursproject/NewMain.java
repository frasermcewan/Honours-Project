package com.example.mcewans_lager.honoursproject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


/**
 * Created by mcewans_lager on 20/01/16.
 */
public class NewMain extends FragmentActivity {

    private static String targetURL = "http://api.openweathermap.org/data/2.5/forecast?q=Glasgow&mode=xml&APPID=b33efea24270c8a29ff5678f3730ecb2";
    private static String secondURL = "http://api.openweathermap.org/data/2.5/forecast?q=Miami&mode=xml&APPID=b33efea24270c8a29ff5678f3730ecb2";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getWeather();

    }


    public void getWeather() {
        handleXML handle = new handleXML(secondURL);
        handle.getXML();




//        Log.i(TAG, "getWeather: " + handle.getPrepType());


    }



}




