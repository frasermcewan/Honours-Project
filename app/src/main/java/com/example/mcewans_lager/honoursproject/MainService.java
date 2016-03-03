package com.example.mcewans_lager.honoursproject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;


public class MainService extends Service  {


    protected static final String TAG = "Service";
    PowerManager.WakeLock wakeLock;
    ArrayList<String> wiList = new ArrayList<String>();
    boolean instantUpdate = false;
    boolean dailyUpdate = false;
    int stepsTaken = 0;
    double currentLat = 0.0;
    double currentLon = 0.0;
    ArrayList<String> currentPrepType = new ArrayList<String>();
    ArrayList<String> currentPrepVolume = new ArrayList<String>();
    ArrayList<String> currentWindSpeed = new ArrayList<String>();
    ArrayList<String> currenttempMax = new ArrayList<String>();
    ArrayList<String> currenttempMin = new ArrayList<String>();
    private volatile boolean connected = false;






    @Override
    public void onCreate() {
        super.onCreate();
        
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        Intent startStepCounter = new Intent (this,StepCounterService.class);
        startService(startStepCounter);

        startAlarm();
        getWeather();
//        getGPS();


    }



    public void startAlarm() {


        Intent intent = new Intent(this, wifiHolder.class);


        final PendingIntent pIntent = PendingIntent.getBroadcast(this, wifiHolder.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


       if (!instantUpdate && !dailyUpdate) {
        Log.i(TAG, "startAlarm: Half Hour");
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, pIntent);
    }
             else if (dailyUpdate) {
            Log.i(TAG, "startAlarm: Day");
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    AlarmManager.INTERVAL_DAY, pIntent);
        } else if (instantUpdate) {
            Log.i(TAG, "startAlarm: Instant");
            Intent l = new Intent(this, wifiHolder.class);
            startService(l);
        }




    }


    @Override
    public int onStartCommand (Intent intent, int flags, int startId){

        Bundle extras = intent.getExtras();

        if(extras != null) {

            String ActionName = intent.getStringExtra("Action");

            if (ActionName.equals("Wifi")) {
                ArrayListWrapper w = (ArrayListWrapper) intent.getSerializableExtra("list");
                wiList = w.getItems();

            } else if(ActionName.equals("Step")){
                String stepStatus = intent.getStringExtra("Status");
                if(stepStatus.equals("true")) {
                } else if (stepStatus.equals("false")) {
                    Log.i(TAG, "onStartCommand: False StepCounter");
                    startGPSSteps();
                }
            } else if (ActionName.equals("StepsTaken")) {
                stepsTaken = intent.getIntExtra("StepCount",0);

            } else if (ActionName.equals("GPS")) {
                String holder = intent.getStringExtra("Ready");
                Log.i(TAG, "ErrorControl ");
                if(holder.equals("No")) {
                    Log.i(TAG, "ErrorControl ");
                 getGPS();
                } else if (holder.equals("Yes")) {
                    currentLat = intent.getDoubleExtra("Lat", 0.0);
                    currentLon = intent.getDoubleExtra("Lon", 0.0);
                    Log.i(TAG, "GPSLat: " + currentLat);
                    Log.i(TAG, "GPSLon: " + currentLon);
                }
            } else if (ActionName.equals("Weather")) {
                ArrayListWrapper w1 = (ArrayListWrapper) intent.getSerializableExtra("PrepType");
                currentPrepType = w1.getItems();
                ArrayListWrapper w2 = (ArrayListWrapper) intent.getSerializableExtra("PrepVolume");
                currentPrepVolume = w2.getItems();
                ArrayListWrapper w3 = (ArrayListWrapper) intent.getSerializableExtra("WindSpeed");
                currentWindSpeed = w3.getItems();
                ArrayListWrapper w4 = (ArrayListWrapper) intent.getSerializableExtra("tempMax");
                currenttempMax = w4.getItems();
                ArrayListWrapper w5 = (ArrayListWrapper) intent.getSerializableExtra("tempMin");
                currenttempMin = w5.getItems();
            } else if (ActionName.equals("Connect")) {
                connected = true;
                getGPS();
            }

        }
        return START_STICKY;
    }

    private void startGPSSteps() {
        Intent gpsStepCount = new Intent(this,LocationService.class);
        gpsStepCount.putExtra("Action", "Normal");
        startService(gpsStepCount);
    }

    private void getGPS() {
        if(connected) {
            Log.i(TAG, "getGPS: This was called");
            Intent getGPS = new Intent(this, LocationService.class);
            getGPS.putExtra("Action", "GPS");
            startService(getGPS);
        }
    }


    private void getWeather() {
        Intent getWeather = new Intent(this,weatherIntentService.class);
        getWeather.putExtra("Action","Location");
       getWeather.putExtra("Lat",currentLat);
        getWeather.putExtra("Lon",currentLon);
        startService(getWeather);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}