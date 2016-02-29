package com.example.mcewans_lager.honoursproject;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by mcewans_lager on 16/02/16.
 */
public class StepCounterIntentService extends Service implements SensorEventListener {

    protected static final String TAG = "StepsTaken";
    private SensorManager sensorManager;
    private int steps;
    private boolean sCounter = false;
    private boolean sDetector = false;
    PowerManager.WakeLock wakeLock;




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(stepCounter != null) {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI);
            sCounter = true;
        } else {
//            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

        if (stepDetector != null && sCounter == false) {
            sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI);
            sDetector = true;
        } else {
            Toast.makeText(this, "StepDetector and counter not available!", Toast.LENGTH_LONG).show();
        }

        checkToEndService();
        return START_STICKY;
    }

    private void checkToEndService() {
        if (sDetector == false && sCounter == false) {
            onDestroy();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        int steps = -1;

        if (values.length > 0) {
            steps = (int) values[0];
        }

    }

    public int returnSteps () {
        return steps;
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
