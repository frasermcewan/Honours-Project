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
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
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
    boolean dailyStepWarning = false;
    DBhandler dbHandler;
    boolean sigCreatedHome = false;
    boolean sigCreatedWork = false;





    @Override
    public void onCreate() {
        super.onCreate();

        dataHolder.clear();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        wiList = new ArrayList<String>();
        dbHandler = new DBhandler(this, null, null, 1);

        Intent startStepCounter = new Intent(this, StepCounterService.class);
        startService(startStepCounter);

//        getGPS();
//        startAlarm30();
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
//        getDatabaseSize();
        signitureCreatedNotification(locationName);
    }


    public void deleteFromDatabase(String locationName) {
        dbHandler.deleteLocation(locationName);
    }

    public void getDatabaseSize() {
        ArrayList<Signatures> size = new ArrayList<>();
        size = dbHandler.returnAllLocation();

        if (size.size() < 2) {
            startAlarm30();
        }

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
        dayAlarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY, pIntent);
    }

//    public void runInForeground() {
//        Notification notification = new NotificationCompat.Builder(this)
//                .setContentTitle("Truiton Music Player")
//                .setTicker("Truiton Music Player")
//                .setContentText("My Music")
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setLargeIcon(
//                        Bitmap.createScaledBitmap(icon, 128, 128, false))
//                .setContentIntent(pendingIntent)
//                .setOngoing(true);
//        startForeground(20,
//                notification);
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(intent.hasExtra("Action")) {


            String ActionName = intent.getStringExtra("Action");
            if (ActionName.equals("Wifi")) {
                ArrayListWrapper w = (ArrayListWrapper) intent.getSerializableExtra("list");
                wiList = w.getItems();
                getWeather();
                setSigniture();
            } else if (ActionName.equals("Step")) {
                String stepStatus = intent.getStringExtra("Status");
                if (stepStatus.equals("true")) {
                } else if (stepStatus.equals("false")) {
                    startGPSSteps();
                }
            } else if (ActionName.equals("StepsTaken")) {
                stepsTaken = intent.getIntExtra("StepCount", 0);
                checkDailySteps();
            } else if (ActionName.equals("GPS")) {
                Log.i(TAG, "GPSMain: ");
                String holder = intent.getStringExtra("Ready");
                  if (holder.equals("Yes")) {
                    currentLat = intent.getDoubleExtra("Lat", 0.0);
                    currentLon = intent.getDoubleExtra("Lon", 0.0);
                    Log.i(TAG, "LAT  " + currentLat);
                    Log.i(TAG, "LON  " + currentLon);
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
                String Location = intent.getStringExtra("Location");
                if (Location.equals("Home")) {
                    double homeLat = intent.getDoubleExtra("HomeLat", 0);
                    double homeLon = intent.getDoubleExtra("HomeLon", 0);
                    homeLat = smoothDoubles(homeLat);
                    homeLon = smoothDoubles(homeLon);
                    addGeofence("Home", homeLat, homeLon);
                    addToDatabase("Home", homeLat, homeLon, wiList);
                } else if (Location.equals("Work")) {
                    double workLat = intent.getDoubleExtra("WorkLat", 0);
                    double workLon = intent.getDoubleExtra("WorkLon", 0);
                    addGeofence("Work", workLat, workLon);
                    workLat = smoothDoubles(workLat);
                    workLon = smoothDoubles(workLon);
                    addToDatabase("Work", workLat, workLon, wiList);
                }

            }

        }
        return START_STICKY;
    }

    private void checkDailySteps() {
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

        Log.i(TAG, "setSigniture: " + holderSig.getLat());
        Log.i(TAG, "setSigniture: " + holderSig.get_lon());
        Log.i(TAG, "setSigniture: " + holderSig.getTime());


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
                Log.i(TAG, "checkForScanLimit: " + getSig.getTime());
                if(getSig.getTime() < 9 || getSig.getTime() > 19) {
                    homeList.add(getSig);
                } else if (getSig.getTime() > 9 || getSig.getTime() < 19) {
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
        Log.i(TAG, "createHomeSigniture:  HERE");
        int counter = 0;


        for (int i = 0; i <homeList.size(); i++ ) {
            for (int j = i + 1; j<homeList.size(); j++) {
                if(homeList.get(i).getLat().equals(homeList.get(j).getLat())); {
                    counter++;
                }
                if (counter >= 3 && !sigCreatedHome) {
                    addToDatabaseSmart("Home", homeList.get(i).getLat(), homeList.get(i).get_lon(), homeList.get(i).getWifi());
                    addGeofence("Home", Double.parseDouble(homeList.get(i).getLat()), Double.parseDouble(homeList.get(i).get_lon()));
                    sigCreatedHome = true;
                    break;
                }
            }
        }


    }

    private void createWorkSigniture() {
        Log.i(TAG, "createWorkSigniture: THERE");
        int counter = 0;

        for (int i = 0; i <workList.size(); i++ ) {
            for (int j = i+ 1; j<workList.size(); j++) {
                if (workList.get(i).getLat().equals(workList.get(j).getLat())) ;
                if (!sigCreatedWork) {
                    addToDatabaseSmart("Work", workList.get(i).getLat(), workList.get(i).get_lon(), workList.get(i).getWifi());
                    addGeofence("Work", Double.parseDouble(workList.get(i).getLat()), Double.parseDouble(workList.get(i).get_lon()));
                    sigCreatedWork = true;
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
        startNot.putExtra("Steps", stepsTaken);
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
        getGPS();
    }

    private void getGPS() {
            Log.i(TAG, "getGPS: ");
            Intent getGPS = new Intent(this, LocationService.class);
            getGPS.putExtra("Action", "GPS");
            startService(getGPS);

    }

    private void getWeather() {
        Intent getWeather = new Intent(this, weatherIntentService.class);
        getWeather.putExtra("Action", "Location");
        getWeather.putExtra("Lat", currentLat);
        getWeather.putExtra("Lon", currentLon);
        startService(getWeather);
    }


    private void addGeofence(String inName, double lat, double lon) {
        Log.i(TAG, "addGeofence: " + inName);
        Log.i(TAG, "addGeofence: " + lat);
        Log.i(TAG, "addGeofence: " + lon);
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