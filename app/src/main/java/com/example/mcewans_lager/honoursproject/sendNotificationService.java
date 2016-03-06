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


public class sendNotificationService extends IntentService {

    protected static final String TAG = "NotificationService";
    int stepValue;
    String transistionSetting;
    String stepStatus;
    long[] vibrate = {300, 300, 300, 300, 300, 300};

    public sendNotificationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String ActionName = intent.getStringExtra("Action");

        if (ActionName.equals("GPSNot")) {
            transistionSetting = intent.getStringExtra("Details");
            stepValue = intent.getIntExtra("Steps", 0);
            sendGPSNotification(transistionSetting, stepValue);

        } else if (ActionName.equals("Step")) {


        } else if (ActionName.equals("Sig")) {
            stepStatus = intent.getStringExtra("Status");
            stepValue =  intent.getIntExtra("Steps", 0);
            sendCreateSignitureNotification(stepStatus, stepValue);
        }


    }

    private void sendGPSNotification(String notificationDetails, int stepValue) {
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
                .setContentText("Your total steps after this transistion " + stepValue)
                .setContentIntent(notificationPendingIntent);


        builder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }


    private void sendStepNotification(String notificationDetails) {


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
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(vibrate)
                .setContentTitle(notificationDetails)
                .setContentText("Notifcation")
                .setContentIntent(notificationPendingIntent);


        builder.setAutoCancel(true);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(0, builder.build());
    }


    public void sendCreateSignitureNotification(String notificationDetails, int stepValue) {

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
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(vibrate)
                .setContentTitle(notificationDetails)
                .setContentText("Notifcation")
                .setContentIntent(notificationPendingIntent);


        builder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, builder.build());

    }


}
