package com.example.mcewans_lager.honoursproject;

import android.app.Service;

import android.content.Intent;
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


/**
 * Created by mcewans_lager on 01/03/16.
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Boolean connected = false;
    protected static final String TAG = "GPSService";
    PowerManager.WakeLock wakeLock;
    private LocationRequest mLocationRequest;
    private double distanceInMeters = 0;
    double returnLat = 0.0;
    double returnLon = 0.0;
    int stepsTaken = 0;
    int sendSteps = 0;
    Location initialLocation;



    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mGoogleApiClient.connect();
        serviceCheck();


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


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

        String ActionName = intent.getStringExtra("Action");

        if (ActionName.equals("Normal")) {
            stepsTaken = 0;
            sendSteps = 50;
        } else if (ActionName.equals("GPS")) {
            getLocation();

        }

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
        sendConnection();
        connected = true;
        initialLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i(TAG,"Hello" + initialLocation);


    }


    public void getLocation() {
      
        if (connected) {
            returnLat = initialLocation.getLatitude();
            returnLon = initialLocation.getLongitude();
            sendGPS();
        } else {
            sendGPSError();
        }
       

    }

    private void sendGPSError() {
        Intent gps = new Intent(this, MainService.class);
        gps.putExtra("Action","GPS");
        gps.putExtra("Ready","No");
        startService(gps);
    }

    private void sendConnection() {
        Intent connect = new Intent(this,MainService.class);
        connect.putExtra("Action","Connect");
        startService(connect);
    }

    public void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates: ");
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
        int holderSteps = 0;



        double previousLon = initialLocation.getLongitude();
        double previousLat = initialLocation.getLatitude();
        double previousAlt = initialLocation.getAltitude();

        double currentLon = location.getLongitude();
        double currentLat = location.getLatitude();
        double currentAlt = location.getAltitude();
        float [] dist = new float[1];

        Location.distanceBetween(previousLon,previousLat,currentLon,currentLat,dist);

        distanceInMeters = Math.round( dist[0]);

        if (currentAlt > previousAlt) {
            holderSteps = holderSteps + (int) (distanceInMeters/0.80);

        } else {
            holderSteps = holderSteps + (int) (distanceInMeters / 0.75);

        }

        stepsTaken = stepsTaken + holderSteps;

        if (stepsTaken >= sendSteps) {
            sendSteps = sendSteps + 50;
            Log.i(TAG, "onLocationChanged: " + sendSteps);
            sendSteps();
        }
        Log.i(TAG, "onLocationChanged: " + stepsTaken);
        initialLocation = location;




    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    
    public void sendSteps() {
        Intent i = new Intent(this, MainService.class);
        i.putExtra("Action", "StepsTaken");
        i.putExtra("StepCount", stepsTaken);
        startService(i);

        
    }

    public void sendGPS() {
        Intent gps = new Intent(this, MainService.class);
        gps.putExtra("Action","GPS");
        gps.putExtra("Ready","Yes");
        gps.putExtra("Lat", returnLat);
        gps.putExtra("Lon", returnLon);
        startService(gps);
    }
    

}