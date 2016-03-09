package com.example.mcewans_lager.honoursproject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.Date;


public class MainService extends Service {


    protected static final String TAG = "Service";
    PowerManager.WakeLock wakeLock;
    ArrayList<String> wiList;
    AlarmManager alarm30;
    AlarmManager dayAlarm;
    PendingIntent pIntent;
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
    String transistionName;
    Bundle extras;
    boolean dailyStepWarning = false;
    DBhandler dbHandler;





    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        wiList = new ArrayList<String>();
        dbHandler = new DBhandler(this, null, null, 1);

        Intent startStepCounter = new Intent(this, StepCounterService.class);
        startService(startStepCounter);

        startAlarm30();
        getTime();
        dropTable();

    }


    public void dropTable () {
        dbHandler.methodAccess();
    }

    public void addToDatabase (String locationName, double lat, double lon, ArrayList<String> wifiList) {
        Signatures submitToDatabase = new Signatures();
        submitToDatabase.setLocationName(locationName);
        submitToDatabase.setLat(Double.toString(smoothDoubles(lat)));
        submitToDatabase.setLon(Double.toString(smoothDoubles(lon)));
        submitToDatabase.setWIFI(convertWifitoString(wifiList));
        dbHandler.addLocation(submitToDatabase);
        getDatabaseSize();
        signitureCreatedNotification(locationName);
    }


    public void addToDatabaseSmart (String locationName, String lat, String lon, String wifiList) {
        Signatures submitToDatabase = new Signatures();
        submitToDatabase.setLocationName(locationName);
        submitToDatabase.setLat(lat);
        submitToDatabase.setLon(lon);
        submitToDatabase.setWIFI(wifiList);
        dbHandler.addLocation(submitToDatabase);
        getDatabaseSize();
        signitureCreatedNotification(locationName);
    }


    public void deleteFromDatabase(String locationName) {
        dbHandler.deleteLocation(locationName);
    }

    public void getDatabaseSize() {
        ArrayList<Signatures> size = new ArrayList<>();
        size = dbHandler.returnAllLocation();
        Log.i(TAG, "getDatabaseSize: " + size.size());
    }

    public double smoothDoubles(double input) {
        Double storeDouble = new BigDecimal(input)
                .setScale(3,BigDecimal.ROUND_CEILING)
                .doubleValue();
        return storeDouble;
    }

    public void startAlarm30() {
        Intent intent = new Intent(this, wifiHolder.class);
        pIntent = PendingIntent.getBroadcast(this, wifiHolder.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
                Log.i(TAG, "onStartCommand: " + wiList);
                getWeather();
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
//                    getGPS();
                } else if (holder.equals("Yes")) {
                    currentLat = intent.getDoubleExtra("Lat", 0.0);
                    currentLon = intent.getDoubleExtra("Lon", 0.0);
                    Log.i(TAG, "onStart:  " + currentLat);
                    Log.i(TAG, "onStart:  " + currentLon);
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
//                getGPS();
            } else if (ActionName.equals("Geofence")) {
                geoTransistion = intent.getStringExtra("Details");
                transistionName = intent.getStringExtra("Tran");


                if (transistionName.equals("Dwell")) {
                    String rainType = currentPrepType.get(0);
                    String rainVolume = currentPrepVolume.get(0);
                    String wind = currentWindSpeed.get(0);
                    String lowTemp = currenttempMin.get(0);
                    WeatherNotification(rainType, rainVolume, wind, lowTemp);
                }

                startGPSNotification();
            } else if (ActionName.equals("Main")) {
                Log.i(TAG, "sentToMain: ");
                double homeLat = intent.getDoubleExtra("HomeLat", 0);
                double homeLon = intent.getDoubleExtra("HomeLon", 0);
                double workLat = intent.getDoubleExtra("WorkLat", 0);
                double workLon = intent.getDoubleExtra("WorkLon", 0);
                smoothDoubles(homeLat);
                smoothDoubles(homeLon);
                smoothDoubles(workLat);
                smoothDoubles(workLon);
                addGeofence("Home", homeLat, homeLon);
                addGeofence("Work", workLat, workLon);
                addToDatabase("Home", homeLat, homeLon, wiList);
                addToDatabase("Work", workLat, workLon, wiList);

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

        holderSig.setLat(Double.toString(smoothDoubles(currentLat)));
        holderSig.setLon(Double.toString(smoothDoubles(currentLon)));
        holderSig.setWIFI(convertWifitoString(wiList));
        holderSig.setTime(getTime());

        dataHolder.add(holderSig);
        checkForScanLimit();
    }


    public String convertWifitoString(ArrayList<String> in) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < in.size(); i++) {
            sb.append(in.get(i));
            sb.append(" ");
        }

        return sb.toString();
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
            alarm30.cancel(pIntent);
            startDailyAlarm();
       }


    }

    private void createHomeSigniture() {
        int counter = 0;

        for (int i = 0; i <homeList.size(); i++ ) {
            for (int j = i + 1; j<homeList.size(); i++) {
                if(homeList.get(i).getLat().equals(homeList.get(j).getLat())); {
                    counter++;
                }
                if (counter >= 3) {
                    addToDatabaseSmart("Home", homeList.get(i).getLat(), homeList.get(i).get_lon(), homeList.get(i).getWifi());
                    addGeofence("Home", Double.parseDouble(homeList.get(i).getLat()), Double.parseDouble(homeList.get(i).get_lon()));
                    break;
                }
            }
        }


    }

    private void createWorkSigniture() {
        int counter = 0;

        for (int i = 0; i <workList.size(); i++ ) {
            for (int j = i + 1; j<workList.size(); i++) {
                if(workList.get(i).getLat().equals(workList.get(j).getLat())); {
                    counter++;
                }
                if (counter >= 3) {
                    addToDatabaseSmart("Work",workList.get(i).getLat(),workList.get(i).get_lon(),workList.get(i).getWifi());
                    addGeofence("Work", Double.parseDouble(workList.get(i).getLat()), Double.parseDouble(workList.get(i).get_lon()));
                    break;
                }

            }
        }


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

    private void WeatherNotification(String weathertype, String volume, String wind,  String temp) {
        Intent startNot = new Intent(this, sendNotificationService.class);
        startNot.putExtra("Action", "Weather");
        startNot.putExtra("Type", weathertype);
        startNot.putExtra("Volume", volume);
        startNot.putExtra("Wind", wind);
        startNot.putExtra("Temp", temp);
        startService(startNot);

    }

    private void signitureCreatedNotification(String locationName) {
        Log.i(TAG, "signitureCreatedNotification: ");
        Intent startNot = new Intent(this, sendNotificationService.class);
        startNot.putExtra("Action", "SigCreated");
        startNot.putExtra("Name", locationName);
        startService(startNot);

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


    private void addGeofence(String inName, double lat, double lon) {
        Intent addGeoFences = new Intent(this, LocationService.class);
        addGeoFences.putExtra("Action", "AddFence");
        addGeoFences.putExtra("Name", inName);
        addGeoFences.putExtra("Lat", lat);
        addGeoFences.putExtra("Lon", lon);
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

    public Date checkDate() {
        return null;
    }


}