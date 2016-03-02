package com.example.mcewans_lager.honoursproject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.google.android.gms.location.LocationSettingsRequest;

/**
 * Created by mcewans_lager on 01/03/16.
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Boolean connected;
    protected static final String TAG = "Service";
    PowerManager.WakeLock wakeLock;
    private LocationRequest mLocationRequest;
    boolean mRequestingLocationUpdates = false;
    private double distanceInMeters = 0;
    private int stepsTaken = 0;
    Location initialLocation;



    @Override
    public void onCreate() {
        super.onCreate();

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
        startLocationUpdates();
        initialLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i(TAG,"Hello" + initialLocation);


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
            holderSteps =  holderSteps + (int) (distanceInMeters/0.75);
        }

        stepsTaken = stepsTaken + holderSteps;


        initialLocation = location;
        Log.i(TAG, "onLocationChanged: " + stepsTaken);

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
