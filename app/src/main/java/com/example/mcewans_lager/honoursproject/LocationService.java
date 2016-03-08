package com.example.mcewans_lager.honoursproject;

import android.app.PendingIntent;
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
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mcewans_lager on 01/03/16.
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Boolean connected = false;
    protected static final String TAG = "GPSService";
    PowerManager.WakeLock wakeLock;
    private LocationRequest mLocationRequest;
    private String Name;
    private double distanceInMeters = 0;
    double recievedLat = 0.0;
    double recievedLon = 0.0;
    double returnLat = 0.0;
    double returnLon = 0.0;
    int stepsTaken = 0;
    int sendSteps = 0;
    Location initialLocation;
    private PendingIntent mGeofencePendingIntent;
    private List mGeofenceList;
    Bundle extras;


    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mGeofenceList = new ArrayList<Geofence>();
        mGoogleApiClient.connect();
        serviceCheck();


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }


    public void serviceCheck() {
        int responseCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (responseCode == ConnectionResult.SUCCESS) {
            connected = true;

        } else {
            connected = false;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getExtras() != null) {

            extras = intent.getExtras();
        }

        if (extras != null) {

            String ActionName = intent.getStringExtra("Action");

            if (ActionName.equals("Normal")) {
                stepsTaken = 0;
                sendSteps = 50;
            } else if (ActionName.equals("GPS")) {
                getLocation();

            } else if (ActionName.equals("AddFence")) {
                Log.i(TAG, "AddFence Reached: ");

                    Name = intent.getStringExtra("Name");
                    recievedLat = intent.getDoubleExtra("Lat", 0.0);
                    recievedLon = intent.getDoubleExtra("Lon", 0.0);
                    buildGeofences(Name, recievedLat, recievedLon);

            } else if (ActionName.equals("DeleteGeofences")) {

            }
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
        gps.putExtra("Action", "GPS");
        gps.putExtra("Ready", "No");
        startService(gps);
    }

    private void sendConnection() {
        Intent connect = new Intent(this, MainService.class);
        connect.putExtra("Action", "Connect");
        startService(connect);
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
        int holderSteps = 0;
        Log.i(TAG, "onLocationChanged: ");

        double previousLon = initialLocation.getLongitude();
        double previousLat = initialLocation.getLatitude();
        double previousAlt = initialLocation.getAltitude();

        double currentLon = location.getLongitude();
        double currentLat = location.getLatitude();
        double currentAlt = location.getAltitude();
        float[] dist = new float[1];

        Location.distanceBetween(previousLon, previousLat, currentLon, currentLat, dist);

        distanceInMeters = Math.round(dist[0]);

        if (distanceInMeters < 10.00) {

            if (currentAlt > previousAlt) {
                holderSteps = holderSteps + (int) (distanceInMeters / 0.75);

            } else {
                holderSteps = holderSteps + (int) (distanceInMeters / 0.80);


            }

            stepsTaken = stepsTaken + holderSteps;

        Log.i(TAG, "onLocationChanged: " + stepsTaken);

            if (stepsTaken >= sendSteps) {
                sendSteps = sendSteps + 50;
                sendSteps();
            }

            initialLocation = location;
        }

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void sendSteps() {
        Log.i(TAG, "sendSteps: ");
        Intent i = new Intent(this, MainService.class);
        i.putExtra("Action", "StepsTaken");
        i.putExtra("StepCount", stepsTaken);
        startService(i);


    }

    public void sendGPS() {
        Intent gps = new Intent(this, MainService.class);
        gps.putExtra("Action", "GPS");
        gps.putExtra("Ready", "Yes");
        gps.putExtra("Lat", returnLat);
        gps.putExtra("Lon", returnLon);
        startService(gps);
    }


    private void buildGeofences(String id, double latitude, double longitude) {
        
        mGeofenceList.add(new Geofence.Builder()

                .setRequestId(id)

                .setCircularRegion(
                        latitude,
                        longitude,
                        500
                )
                .setLoiteringDelay(30000)
                .setExpirationDuration(7200000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .build());
        registerGeoFences();
    }


    private PendingIntent getGeofencePendingIntent() {

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }


    public void registerGeoFences() {
        Log.i(TAG, "registerGeoFences: ");
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeofenceList,
                getGeofencePendingIntent());
        mGeofenceList.clear();

    }


    public void deRegisterGeoFences() {
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, getGeofencePendingIntent());


    }
}