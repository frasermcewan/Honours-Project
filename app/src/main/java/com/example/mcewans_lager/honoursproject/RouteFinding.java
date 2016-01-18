package com.example.mcewans_lager.honoursproject;


import android.app.AlertDialog;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class RouteFinding extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LatLng locale;
    private Button alertButton;
    private Button markerHomeButton;
    private Button markerAwayButton;
    private ArrayList mGeofenceList;
    private Geofence mGeofence;
    private LatLng touchLocation;
    private Marker homeMarker;
    private Marker workMarker;
    private Marker standardMarker;
    private Boolean Home = true;
    private Boolean Work = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_finding);
        setUpMapIfNeeded();


        alertButton = (Button) findViewById(R.id.AlertButton);
        markerHomeButton = (Button) findViewById(R.id.MarkerHomeButton);
        markerAwayButton = (Button) findViewById(R.id.MarkerWorkButton);


        alertButton.setOnClickListener(this);
        markerHomeButton.setOnClickListener(this);
        markerAwayButton.setOnClickListener(this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

//        LocationServices.GeofencingApi.addGeofences(
//                mGoogleApiClient,
//                getGeofencingRequest(),
//                getGeofencePendingIntent()
//        ).setResultCallback(this);


    }

    protected void onStart(Bundle savedInstanceState) {
        super.onStart();
        //Small Changes
        mGoogleApiClient.connect();
    }

    protected void onPause(Bundle savedInstanceState) {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null)
                setUpMap();
        }
    }


    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(10, 10)).title("Test Location"));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                touchLocation = point;

                if (Home == true) {

                    if (homeMarker != null) {
                        homeMarker.remove();
                    }
                    homeMarker = mMap.addMarker(new MarkerOptions().position(touchLocation).title("Home Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    Home = false;
//                    new Geofence.Builder().build();

                } else if (Work == true) {

                    if (workMarker != null) {
                        workMarker.remove();

                    }

                    workMarker = mMap.addMarker(new MarkerOptions().position(touchLocation).title("Work Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    Work = false;
                }


            }
        });

    }

//    private GeofencingRequest getGeofencingRequest() {
//        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
//        builder.addGeofences(mGeofenceList);
//        return builder.build();
//    }


//    public void geoFences () {
//
//    //63, 512
//        mGeofenceList.add(new Geofence.Builder()
//                // Set the request ID of the geofence. This is a string to identify this
//                // geofence.
//                .setRequestId("Fence1")
//
//                .setCircularRegion(
//                        location.getLatitude(),
//                        location.getLongitude(),
//                        250
//                )
//                .setExpirationDuration(3000)
//                .setLoiteringDelay(60000)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
//                .build());
//    }


//    private PendingIntent getGeofencePendingIntent() {
//        // Reuse the PendingIntent if we already have it.
//        if (mGeofencePendingIntent != null) {
//            return mGeofencePendingIntent;
//        }
//        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
//        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
//        // calling addGeofences() and removeGeofences().
//        return PendingIntent.getService(this, 0, intent, PendingIntent.
//                FLAG_UPDATE_CURRENT);
//    }


    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            locale = new LatLng(location.getLatitude(), location.getLongitude());

            Marker addMarker;
            addMarker = mMap.addMarker(new MarkerOptions().position(locale).title("Actual Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locale, 10));

        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onClick(View savedInstance) {
        switch (savedInstance.getId()) {
            case R.id.AlertButton:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("Do this suggestion.");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder1.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                break;
            case R.id.MarkerHomeButton:
                Home = true;
                break;
            case R.id.MarkerWorkButton:
                Work = true;
                break;


        }

    }
}


    //@Override
//    protected void onHandleIntent(Intent intent) {
//        protected void onHandleIntent(Intent intent) {
//            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
//            if (geofencingEvent.hasError()) {
//                String errorMessage = GeofenceErrorMessages.getErrorString(this,
//                        geofencingEvent.getErrorCode());
//                return;
//            }
//
//            // Get the transition type.
//            int geofenceTransition = geofencingEvent.getGeofenceTransition();
//
//            // Test that the reported transition was of interest.
//            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
//                    geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
//
//                // Get the geofences that were triggered. A single event can trigger
//                // multiple geofences.
//                List triggeringGeofences = geofencingEvent.getTriggeringGeofences();
//
//                // Get the transition details as a String.
//                String geofenceTransitionDetails = getGeofenceTransitionDetails(
//                        this,
//                        geofenceTransition,
//                        triggeringGeofences
//                );
//
//                // Send notification and log the transition details.
//                sendNotification(geofenceTransitionDetails);
//                Log.i(TAG, geofenceTransitionDetails);
//            } else {
//                // Log the error.
//                Log.e(TAG, getString(R.string.geofence_transition_invalid_type,
//                        geofenceTransition));
//            }
//    }
//}
