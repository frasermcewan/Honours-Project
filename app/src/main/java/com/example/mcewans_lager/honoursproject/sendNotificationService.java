package com.example.mcewans_lager.honoursproject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Random;


public class sendNotificationService extends IntentService {

    protected static final String TAG = "NotificationService";
    int stepValue;
    String transistionSetting;
    String stepStatus;
    String message;
    String signitureName;
    String rainType;
    String rainVolume;
    String wind;
    String temp;
    String weather;

    long[] vibrate = {300, 300, 300, 300, 300, 300};

    long[] vibrate2 = {500, 500, 500, 500, 500, 500, 500, 500, 500};



    public sendNotificationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String ActionName = intent.getStringExtra("Action");

        if (ActionName.equals("GPSNot")) {
            transistionSetting = intent.getStringExtra("Details");
            sendGPSNotification(transistionSetting);
            Log.i(TAG, "notIntent: " + transistionSetting);

        } else if (ActionName.equals("Step")) {
            stepValue = intent.getIntExtra("Steps", 0);
            sendStepNotification(stepValue);
        } else if (ActionName.equals("SigCreated")) {
            signitureName = intent.getStringExtra("Name");
            sendCreateSignitureNotification(signitureName);
        } else if (ActionName.equals("Weather")) {
            rainType = intent.getStringExtra("Type");
            rainVolume = intent.getStringExtra("Volume");
            wind = intent.getStringExtra("Wind");
            temp = intent.getStringExtra("Temp");


            if(rainType.equals("0")){
                weather = "Good Weather";
                rainVolume = "No Rain";
                message = "Good time to be active";


            } else {
                weather = "Rain";
                message = "Bad weather, Still try to be Active";
            }

            sendWeatherNotification(weather, rainVolume, wind, temp, message, stepValue);

        }


    }

    private void sendGPSNotification(String notificationDetails) {

        String Mess = "";

        if (notificationDetails.equals("Exit: Home") || notificationDetails.equals("Exit: Work")) {
            Mess = "Great time to be active";
        } else if (notificationDetails.equals("Dwell: Home") || notificationDetails.equals("Dwell: Work")) {
            Mess = "Great time to be active";
        } else {
            Mess = "Location Update";
        }

        Intent notificationIntent = new Intent(getApplicationContext(), RouteFinding.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);


        stackBuilder.addParentStack(RouteFinding.class);


        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setVibrate(vibrate)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle(notificationDetails)
                .setContentText(Mess)
                .setContentIntent(notificationPendingIntent);


        builder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Random random = new Random();
        int m = random.nextInt();
        mNotificationManager.notify(m, builder.build());
    }


    private void sendStepNotification(int stepValue) {


        Intent notificationIntent = new Intent(getApplicationContext(), RouteFinding.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);


        stackBuilder.addParentStack(RouteFinding.class);


        stackBuilder.addNextIntent(notificationIntent);


        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


        builder.setSmallIcon(R.mipmap.ic_launcher)

                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.BLUE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(vibrate)
                .setContentTitle("Steps today")
                .setContentText("You have done " + stepValue + " Steps today")
                .setContentIntent(notificationPendingIntent);


        builder.setAutoCancel(true);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Random random = new Random();
        int m = random.nextInt();
        mNotificationManager.notify(m, builder.build());
    }


    public void sendCreateSignitureNotification(String signitureName) {

        Intent notificationIntent = new Intent(getApplicationContext(), RouteFinding.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(RouteFinding.class);


        stackBuilder.addNextIntent(notificationIntent);


        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


        builder.setSmallIcon(R.mipmap.ic_launcher)

                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.MAGENTA)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(vibrate)
                .setContentTitle(signitureName)
                .setContentText("Signiture has been generated at this location")
                .setContentIntent(notificationPendingIntent);


        builder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Random random = new Random();
        int m = random.nextInt();
        mNotificationManager.notify(m, builder.build());

    }

    public void sendWeatherNotification(String weather, String volume, String gale, String celsius, String Message, int stepV) {

        Intent notificationIntent = new Intent(getApplicationContext(), RouteFinding.class);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(RouteFinding.class);


        stackBuilder.addNextIntent(notificationIntent);


        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


        builder.setSmallIcon(R.mipmap.ic_launcher)

                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.YELLOW)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(vibrate)
                .setContentTitle(weather +" " + celsius)
                .setContentText(Message)
                .setContentIntent(notificationPendingIntent);


        builder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        Random random = new Random();
        int m = random.nextInt();
        mNotificationManager.notify(m, builder.build());



    }


}
