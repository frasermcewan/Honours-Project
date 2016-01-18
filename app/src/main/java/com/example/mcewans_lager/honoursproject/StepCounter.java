package com.example.mcewans_lager.honoursproject;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

/**
 * Created by mcewans_lager on 18/01/16.
 */
public class StepCounter extends Activity implements SensorEventListener {



    private SensorManager mManager;

    private Sensor mStepCounter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounter = mManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

    }



    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

