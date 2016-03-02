package com.example.mcewans_lager.honoursproject;

import android.app.AlarmManager;
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


public class MainService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Boolean connected;
    protected static final String TAG = "Service";
    PowerManager.WakeLock wakeLock;
    private LocationRequest mLocationRequest;
    static ArrayList<String> wiList = new ArrayList<String>();
    static boolean instantUpdate = false;
    static boolean dailyUpdate = false;
    static boolean stepCounter = false;
    private static int stepsTaken = 0;
    private int distanceInMeters= 0;
    Location initialLocation;





    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mGoogleApiClient.connect();
        serviceCheck();


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }



    public void startAlarm() {


        Intent intent = new Intent(this, wifiHolder.class);


        final PendingIntent pIntent = PendingIntent.getBroadcast(this, wifiHolder.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        if (!instantUpdate && !dailyUpdate) {
            Log.i(TAG, "startAlarm: Half Hour");
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntent);
        } else if (dailyUpdate) {
            Log.i(TAG, "startAlarm: Day");
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    AlarmManager.INTERVAL_DAY, pIntent);
        } else if (instantUpdate) {
            Log.i(TAG, "startAlarm: Instant");
            Intent l = new Intent(this, wifiHolder.class);
            sendBroadcast(l);
        }




    }





    public void serviceCheck() {
        int responseCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (responseCode == ConnectionResult.SUCCESS){
            connected = true;

        } else {
            connected = false;
        }
    }


    @Override
    public int onStartCommand (Intent intent, int flags, int startId){
        Intent startStepCounter = new Intent (this,StepCounterService.class);
        startService(startStepCounter);

        startAlarm();

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {

            startLocationUpdates();

         initialLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    }




    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void endLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }




    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (!stepCounter) {
            int holderSteps = 0;

            double previousLon = initialLocation.getLongitude();
            double previousLat = initialLocation.getLatitude();
            double previousAlt = initialLocation.getAltitude();

            double currentLon = location.getLongitude();
            double currentLat = location.getLatitude();
            double currentAlt = location.getAltitude();
            float[] dist = new float[1];

            Location.distanceBetween(previousLon, previousLat, currentLon, currentLat, dist);

            distanceInMeters = Math.round(dist[0]);

            if (currentAlt > previousAlt) {
                holderSteps = holderSteps + (int) (distanceInMeters / 0.80);

            } else {
                holderSteps = holderSteps + (int) (distanceInMeters / 0.75);
            }

            stepsTaken = stepsTaken + holderSteps;


            initialLocation = location;

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static class InfoReceiver extends BroadcastReceiver {
        @Override

        public void onReceive(Context context, Intent intent) {


            String ActionName = intent.getStringExtra("Action");

            if(ActionName.equals("Wifi")) {
                Log.i(TAG, "onReceiveMain: ");
                ArrayListWrapper w = (ArrayListWrapper) intent.getSerializableExtra("list");
                wiList = w.getNames();
                Log.i(TAG, "onReceiveMain: " + wiList);

//                if (instantUpdate) {
//                    //Write to database
//                }
//                instantUpdate = false;

            } else if (ActionName.equals("Main")) {
                instantUpdate = true;
            } else if (ActionName.equals("Step")){

                String stepStatus = intent.getStringExtra("Status");

                if(stepStatus.equals("true")) {
                    Log.i(TAG, "StepCounter: Ended and True ");
                    stepCounter = true;
                } else if (stepStatus.equals("false")) {
                    Log.i(TAG, "StepCounter: Ended and False ");
                    stepCounter = false;
                }


            } else if (ActionName.equals("StepsTaken")) {
                stepsTaken = intent.getIntExtra("StepCount", 0);
            }

            

        }
    }



}