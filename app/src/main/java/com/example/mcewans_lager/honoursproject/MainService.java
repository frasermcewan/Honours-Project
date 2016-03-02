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
    boolean mRequestingLocationUpdates = false;
    static ArrayList<String> wiList = new ArrayList<String>();
    static boolean instantUpdate = false;
    static boolean dailyUpdate = false;
    static boolean stepCounter = false;
    private int stepsTaken = 0;
    private int distanceInMeters= 0;
    Location initialLocation;





    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        Intent startStepCounter = new Intent (this,StepCounterService.class);
        startService(startStepCounter);


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
        startAlarm();


    }



    public void startAlarm() {


        Intent intent = new Intent(this, wifiHolder.class);


        final PendingIntent pIntent = PendingIntent.getBroadcast(this, wifiHolder.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        if (!instantUpdate && !dailyUpdate) {

            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    AlarmManager.INTERVAL_HALF_HOUR, pIntent);
        } else if (dailyUpdate) {
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    AlarmManager.INTERVAL_DAY, pIntent);
        } else if (instantUpdate) {
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
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
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
       if (!stepCounter){
           stepsTaken = 0;
           distanceInMeters = 0;
           float [] dist = new float[1];

           Location.distanceBetween(initialLocation.getLatitude(),initialLocation.getLongitude(),location.getLatitude(),location.getLongitude(),dist);

           distanceInMeters = Math.round( dist[0]);

            if (location.getAltitude() > initialLocation.getAltitude()) {
                stepsTaken += (int) (distanceInMeters/0.80);
            } else {
                stepsTaken += (int) (distanceInMeters/0.75);
            }

           initialLocation.setLatitude(location.getLatitude());
           initialLocation.setLongitude(location.getLongitude());
           initialLocation.setAltitude(location.getAltitude());

       }



    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class InfoReceiver extends BroadcastReceiver {
        @Override

        public void onReceive(Context context, Intent intent) {


            String ActionName = intent.getStringExtra("Action");

            if(ActionName.equals("Wifi")) {
                ArrayListWrapper w = (ArrayListWrapper) intent.getSerializableExtra("list");
                wiList = w.getNames();

                if (instantUpdate) {
                    //Write to database
                }
                instantUpdate = false;

            } else if (ActionName.equals("Main")) {
                instantUpdate = true;
            } else if (ActionName.equals("Step")){

                String stepStatus = intent.getStringExtra("Status");

                if(stepStatus.equals("true")) {
                    stepCounter = true;
                } else if (stepStatus.equals("false")) {
                    stepCounter = false;
                }


            } else if (ActionName.equals("StepsTaken")) {
                stepsTaken = intent.getIntExtra("StepCount", 0);
            }

            

        }
    }



}