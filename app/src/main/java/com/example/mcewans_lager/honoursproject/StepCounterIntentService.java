package com.example.mcewans_lager.honoursproject;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mcewans_lager on 16/02/16.
 */
public class StepCounterIntentService extends IntentService {

    protected static final String TAG = "StepsTaken";


    public StepCounterIntentService() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent: We made it");
    

    }
}
