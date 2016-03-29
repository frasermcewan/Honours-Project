package com.example.mcewans_lager.honoursproject;

import android.app.NotificationManager;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;


public class GeofenceTransitionsIntentService extends IntentService {


    /**
     * This class uses samples provided by GooglePlayServices in order to tell the Main Service what transistion has happened
     * between Geofences and what fence was triggered. The source of the code used is cited in the report and below
     *
     * https://github.com/googlesamples/android-Geofencing/blob/master/Application/src/main/java/com/example/android/wearable/geofencing/GeofenceTransitionsIntentService.java
     */



    protected static final String TAG = "GeofenceTransitions";
    String geofenceTransitionDetails;
    String transistionString;


    public GeofenceTransitionsIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        /**
         * Called whenever a Geofence transistion happens, it then works out what hapepned and notifies the MainService
         */

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.i(TAG, "onHandleIntent: " + geofenceTransition);

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {


            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();


            geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );


            alertMainService();
            Log.i(TAG, geofenceTransitionDetails);
        }
    }

    private void alertMainService() {
        Intent intent = new Intent(this, MainService.class);
        intent.putExtra("Action", "Geofence");
        intent.putExtra("Details", geofenceTransitionDetails);
        intent.putExtra("Tran", returnTransistionString());
        startService(intent);
    }


    private String getGeofenceTransitionDetails(

            /**
             * This method was taken from GooglePlay Service example which are cited in the references in the report
             * This method allows the main service to figure out what Geofence was triggered, it then calls another method
             * to figure out what transition occoured
             *
             * Source Code found here
             * https://github.com/googlesamples/android-Geofencing/blob/master/Application/src/main/java/com/example/android/wearable/geofencing/GeofenceTransitionsIntentService.java
             */



            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);
        getTransistionString(geofenceTransitionString);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());

        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }


    private void getTransistionString(String in) {

        transistionString = in;

    }

    private String returnTransistionString() {
        return transistionString;
    }


    private String getTransitionString(int transitionType) {
        /**
         * Works out what transition happened for each Geofence that was triggered
         */

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Enter";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exit";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "Dwell";
            default:
                return "Unknown transistion";
        }
    }
}
