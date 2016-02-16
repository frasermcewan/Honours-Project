package com.example.mcewans_lager.honoursproject;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by mcewans_lager on 16/02/16.
 */
public class StepCounterIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public StepCounterIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
