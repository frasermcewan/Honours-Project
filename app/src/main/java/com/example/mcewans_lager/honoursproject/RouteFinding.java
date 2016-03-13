package com.example.mcewans_lager.honoursproject;


import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



public class RouteFinding extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    protected static final String TAG = "Main";


    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location holderLocation;
    private LatLng locale;
    private Button createButton;
    private Button markerHomeButton;
    private Button markerAwayButton;
    private LatLng touchLocation;
    private double homeLat = 0;
    private double homeLon = 0;
    private double workLat = 0;
    private double workLon = 0;
    private Marker homeMarker;
    private Marker workMarker;
    private Boolean Home = false;
    private Boolean Work = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_finding);
        Intent intent = new Intent(this, MainService.class);
        startService(intent);
        setUpMapIfNeeded();

        createButton = (Button) findViewById(R.id.CreateSigniture);
        markerHomeButton = (Button) findViewById(R.id.MarkerHomeButton);
        markerAwayButton = (Button) findViewById(R.id.MarkerWorkButton);


        createButton.setOnClickListener(this);
        markerHomeButton.setOnClickListener(this);
        markerAwayButton.setOnClickListener(this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mGoogleApiClient.connect();


    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected  void onDestroy() {
        super.onDestroy();
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

                    homeLat = touchLocation.latitude;
                    homeLon = touchLocation.longitude;

                    homeMarker = mMap.addMarker(new MarkerOptions().position(touchLocation).title("Home Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


                } else if (Work == true) {

                    if (workMarker != null) {
                        workMarker.remove();

                    }

                    workLat = touchLocation.latitude;
                    workLon = touchLocation.longitude;

                    workMarker = mMap.addMarker(new MarkerOptions().position(touchLocation).title("Work Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                }


            }
        });

    }







    @Override
    public void onConnected(Bundle bundle) {


        holderLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        if (holderLocation != null) {


            locale = new LatLng(holderLocation.getLatitude(), holderLocation.getLongitude());

            Marker addMarker;
            addMarker = mMap.addMarker(new MarkerOptions().position(locale).title("Actual Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locale, 10));

        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onClick(View savedInstance) {
        switch (savedInstance.getId()) {
            case R.id.CreateSigniture:
                Log.i(TAG, "onClick: ");
                if(Home && !Work) {
                    Intent intent = new Intent(this, MainService.class);
                    intent.putExtra("Action", "Main");
                    intent.putExtra("Location","Home");
                    intent.putExtra("HomeLat", homeLat);
                    intent.putExtra("HomeLon", homeLon);
                    intent.putExtra("WorkLat", workLat);
                    intent.putExtra("WorkLon", workLon);
                    startService(intent);
                 } else if (Work && !Home) {
                    Intent inte = new Intent(this, MainService.class);
                    inte.putExtra("Action", "Main");
                    inte.putExtra("Location","Work");
                    inte.putExtra("HomeLat", homeLat);
                    inte.putExtra("HomeLon", homeLon);
                    inte.putExtra("WorkLat", workLat);
                    inte.putExtra("WorkLon", workLon);
                    startService(inte);
                } else if(homeLat == 0 && workLat == 0) {
                    Toast.makeText(this, "Please choose one location", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.MarkerHomeButton:
                Home = true;
                Work = false;
                break;
            case R.id.MarkerWorkButton:
                Work = true;
                Home = false;
                break;
        }

    }



}