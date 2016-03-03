package com.example.mcewans_lager.honoursproject;

import android.app.IntentService;
import android.content.Intent;


public class sendNotificationService extends IntentService {

    protected static final String TAG = "NotificationService";

    public sendNotificationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
