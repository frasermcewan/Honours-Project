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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainService extends Service {


    protected static final String TAG = "Service";
    PowerManager.WakeLock wakeLock;
    ArrayList<String> wiList = new ArrayList<String>();
    AlarmManager alarm30;
    AlarmManager dayAlarm;
    int stepsTaken = 0;
    double currentLat = 0.0;
    double currentLon = 0.0;
    ArrayList<Signatures> dataHolder = new ArrayList<>();
    ArrayList<String> currentPrepType = new ArrayList<String>();
    ArrayList<String> currentPrepVolume = new ArrayList<String>();
    ArrayList<String> currentWindSpeed = new ArrayList<String>();
    ArrayList<String> currenttempMax = new ArrayList<String>();
    ArrayList<String> currenttempMin = new ArrayList<String>();
    ArrayList<Signatures> homeList = new ArrayList<>();
    ArrayList<Signatures> workList = new ArrayList<>();
    private volatile boolean connected = false;
    String geoTransistion;
    Bundle extras;
    boolean dailyStepWarning = false;



    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        Intent startStepCounter = new Intent(this, StepCounterService.class);
        startService(startStepCounter);

        startAlarm30();
        getTime();
        getGPS();

    }


    public void startAlarm30() {
        Intent intent = new Intent(this, wifiHolder.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, wifiHolder.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm30 = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm30.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, pIntent);
    }

    public void startDailyAlarm() {
        Intent intent = new Intent(this, wifiHolder.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, wifiHolder.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        dayAlarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        dayAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, pIntent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            extras = intent.getExtras();
        }
        if (extras != null) {
            String ActionName = intent.getStringExtra("Action");
            if (ActionName.equals("Wifi")) {
                ArrayListWrapper w = (ArrayListWrapper) intent.getSerializableExtra("list");
                wiList = w.getItems();
                setSigniture();
            } else if (ActionName.equals("Step")) {
                String stepStatus = intent.getStringExtra("Status");
                if (stepStatus.equals("true")) {
                } else if (stepStatus.equals("false")) {
                    startGPSSteps();
                }
            } else if (ActionName.equals("StepsTaken")) {
                Log.i(TAG, "onStartCommand: Steps reached");
                stepsTaken = intent.getIntExtra("StepCount", 0);
                checkDailySteps();
            } else if (ActionName.equals("GPS")) {
                String holder = intent.getStringExtra("Ready");
                if (holder.equals("No")) {
                    getGPS();
                } else if (holder.equals("Yes")) {
                    currentLat = intent.getDoubleExtra("Lat", 0.0);
                    currentLon = intent.getDoubleExtra("Lon", 0.0);
                    Log.i(TAG, "onStartCommand:  " + currentLat);
                    Log.i(TAG, "onStartCommand:  " + currentLon);
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
            } else if (ActionName.equals("Geofence")) {
                geoTransistion = intent.getStringExtra("Details");
                startGPSNotification();
            }

        }
        return START_STICKY;
    }

    private void checkDailySteps() {
        Log.i(TAG, "checkDailySteps: ");
        if(!dailyStepWarning && getTime() > 12) {
            if (stepsTaken < 1000) {
                startLowStepsNotifcaion();
            }
        } dailyStepWarning = true;

    }

    private void setSigniture() {
        Signatures holderSig = new Signatures();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < wiList.size(); i++) {
            sb.append(wiList.get(i));
            sb.append(" ");
        }


        Double storeLat = new BigDecimal(currentLat)
                .setScale(3,BigDecimal.ROUND_CEILING)
                .doubleValue();

        Double storeLon = new BigDecimal(currentLon)
                .setScale(3,BigDecimal.ROUND_CEILING)
                .doubleValue();

        holderSig.setLat(Double.toString(storeLat));
        holderSig.setLon(Double.toString(storeLon));
        holderSig.setWIFI(sb.toString());
        holderSig.setTime(getTime());

        dataHolder.add(holderSig);
        checkForScanLimit();
    }

    private void checkForScanLimit() {
        Signatures getSig = new Signatures();

        if (dataHolder.size() > 48) {

            for (int i = 0; i < dataHolder.size(); i++) {
                getSig = dataHolder.get(i);

                if(getSig.getTime() < 9 && getSig.getTime() > 19) {
                    homeList.add(getSig);
                } else if (getSig.getTime() > 9 && getSig.getTime() < 19) {
                    workList.add(getSig);
                }

            }

            createHomeSigniture();
            createWorkSigniture();

       }


    }

    private void createHomeSigniture() {

    }

    private void createWorkSigniture() {

    }

    private void startGPSNotification() {
        Intent startNot = new Intent(this, sendNotificationService.class);
        startNot.putExtra("Action", "GPSNot");
        startNot.putExtra("Details", geoTransistion);
        startNot.putExtra("Steps", stepsTaken);
        startService(startNot);

    }

    private void startLowStepsNotifcaion() {
        Log.i(TAG, "startLowStepsNotifcaion: ");
        Intent startNot = new Intent(this, sendNotificationService.class);
        startNot.putExtra("Action", "Step");
        startNot.putExtra("Steps", stepsTaken);
        startService(startNot);

    }

    private void goodWeatherNotification() {
        getWeather();
        Intent startNot = new Intent(this, sendNotificationService.class);
        startNot.putExtra("Action", "Weather");
        startNot.putExtra("MaxTemp", currenttempMax.get(0));

    }

    private void signitureCreatedNotification() {
        Intent startNot = new Intent(this, sendNotificationService.class);
        startNot.putExtra("Action", "SigCreated");
        startNot.putExtra("Name", "NameValue");

    }


    private void startGPSSteps() {
        Intent gpsStepCount = new Intent(this, LocationService.class);
        gpsStepCount.putExtra("Action", "Normal");
        startService(gpsStepCount);
    }

    private void getGPS() {
        if (connected) {
            Log.i(TAG, "getGPS: ");
            Intent getGPS = new Intent(this, LocationService.class);
            getGPS.putExtra("Action", "GPS");
            startService(getGPS);
        }
    }


    private void getWeather() {
        Intent getWeather = new Intent(this, weatherIntentService.class);
        getWeather.putExtra("Action", "Location");
        getWeather.putExtra("Lat", currentLat);
        getWeather.putExtra("Lon", currentLon);
        startService(getWeather);
    }


    private void addGeofence() {
        double testLat = 55.846134;
        double testLon = -4.1506831;

        Intent addGeoFences = new Intent(this, LocationService.class);
        addGeoFences.putExtra("Action", "AddFence");
        addGeoFences.putExtra("Name", "Home");
        addGeoFences.putExtra("Lat", testLat);
        addGeoFences.putExtra("Lon", testLon);
        startService(addGeoFences);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public Double getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH.mm");
        Double dateVariable = Double.parseDouble(dateFormat.format(cal.getTime()));
        return dateVariable;
    }

}