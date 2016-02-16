package com.example.mcewans_lager.honoursproject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;


public class RouteFinding extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LatLng locale;
    private Button alertButton;
    private Button markerHomeButton;
    private Button markerAwayButton;
    private PendingIntent mGeofencePendingIntent;
    private List mGeofenceList;
    private LatLng touchLocation;
    private Marker homeMarker;
    private Marker workMarker;
    private Boolean Home = true;
    private Boolean Work = false;
    private int Time;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_finding);
        setUpMapIfNeeded();


        mGeofenceList = new ArrayList<Geofence>();

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

//        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeofenceList,
//                getGeofencePendingIntent());



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

                    mGeofenceList.add(new Geofence.Builder()

                            .setRequestId("Home GeoFence")

                            .setCircularRegion(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    50
                            )
                            .setExpirationDuration(40000)
                            .setLoiteringDelay(1000)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                            .build());
                            addGeoFences();

                } else if (Work == true) {

                    if (workMarker != null) {
                        workMarker.remove();

                    }

                    workMarker = mMap.addMarker(new MarkerOptions().position(touchLocation).title("Work Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    Work = false;


                    mGeofenceList.add(new Geofence.Builder()

                            .setRequestId("Work GeoFence")

                            .setCircularRegion(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    50
                            )
                            .setExpirationDuration(40000)
                            .setLoiteringDelay(5000)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                            .build());
                            addGeoFences();

                }


            }
        });

    }



    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

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


    public void addGeoFences() {
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeofenceList,
                getGeofencePendingIntent());

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

    public int getTime() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:");
        Time = Integer.parseInt(sdf.format(cal.getTime()));

        return Time;
    }


}




