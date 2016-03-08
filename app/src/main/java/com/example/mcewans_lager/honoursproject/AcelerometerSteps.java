package com.example.mcewans_lager.honoursproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by mcewans_lager on 08/03/16.
 */
public class AcelerometerSteps extends Service implements SensorEventListener {

    protected static final String TAG = "StepsTaken";
    private SensorManager sensorManager;
    private int steps;




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor acel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,acel,SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        
        Log.i(TAG, "onSensorChanged: " + z);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
