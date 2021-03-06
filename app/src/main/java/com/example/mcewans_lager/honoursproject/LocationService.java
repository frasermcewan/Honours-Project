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
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by FraserMcEwan on 01/03/16.
 *
 * This class forms one of the main prototypes as it provides valuble location information to the MainService
 * This class allows the MainService to figure out where the user is, track their location and then add geofences in order to
 * properly track movement when they are exiting a location
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Boolean connected = false;
    protected static final String TAG = "GPSService";
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
    PendingIntent mGeofencePendingIntent;
    private List mGeofenceList;
    Bundle extras;


    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        super.onCreate();


        /**
         * Creates a googleAPIClient which allows us to use their Geofence service and request location
         * updates
         */

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<Geofence>();
        mGoogleApiClient.connect();
        serviceCheck();


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(30000);
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

        Bundle bundle = intent.getExtras();

        if (bundle != null) {


            String ActionName = intent.getStringExtra("Action");
            Log.i(TAG, "onStartCommand: " + ActionName);


            if (ActionName.equals("Normal")) {
                stepsTaken = 0;
                sendSteps = 50;
            } else if (ActionName.equals("GPS")) {
                Log.i(TAG, "onStartCommand: Requested GPS ");

            } else if (ActionName.equals("AddFence")) {
                Log.i(TAG, "AddFence Reached: ");

                Name = intent.getStringExtra("Name");
                recievedLat = intent.getDoubleExtra("Lat", 0.0);
                recievedLon = intent.getDoubleExtra("Lon", 0.0);
                buildGeofences(Name, recievedLat, recievedLon);
                registerGeoFences();

            } else if (ActionName.equals("DeleteGeofences")) {

            }
        }

        return START_STICKY;
        //  return START_REDELIVER_INTENT;
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
        getLocation();


    }


    public void getLocation() {
        Log.i(TAG, "getLocation: ");
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
                holderSteps = holderSteps + (int) (distanceInMeters / 1.25);

            } else {
                holderSteps = holderSteps + (int) (distanceInMeters / 1.35);


            }

            stepsTaken = stepsTaken + holderSteps;
            sendSteps();
            sendGPS();
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
        Log.i(TAG, "sendGPS: Sending to Main ");
        Intent gps = new Intent(this, MainService.class);
        Log.i(TAG, "sendGPSLat: " + returnLat);
        Log.i(TAG, "sendGPSLon: " + returnLon);
        gps.putExtra("Action", "GPS");
        gps.putExtra("Ready", "Yes");
        gps.putExtra("Lat", returnLat);
        gps.putExtra("Lon", returnLon);
        startService(gps);
    }


    /**
     *
     * The code for creating and registering Geofences is similar to the code that Android and Google Play
     * Services provide. The code has to be similar to the suggested guidelines otherwise it would not function
     * as there is only so many ways to build a Geofence, Create a pending intent for it and then register and track them
     *
     * Reference to the Source -> http://developer.android.com/training/location/geofencing.html
     */


    private void buildGeofences(String id, double latitude, double longitude) {
        mGeofenceList.add(new Geofence.Builder()

                .setRequestId(id)

                .setCircularRegion(
                        latitude,
                        longitude,
                        500
                )
                .setLoiteringDelay(30000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .build());
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.addGeofences(mGeofenceList);
        return builder.build();
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
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(),
                getGeofencePendingIntent());
    }


    public void deRegisterGeoFences() {
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, getGeofencePendingIntent());


    }
}