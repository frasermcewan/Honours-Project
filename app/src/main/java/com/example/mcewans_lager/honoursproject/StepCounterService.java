package com.example.mcewans_lager.honoursproject;

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
import android.widget.Toast;

/**
 * Created by mcewans_lager on 16/02/16.
 */
public class StepCounterService extends Service implements SensorEventListener {

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
        Log.i(TAG, "onStartCommandStepCounter: ");
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
        if (!sDetector && !sCounter) {
            onDestroy();
        } else {
            Intent i = new Intent(this, MainService.InfoReceiver.class);
            i.putExtra("Action","Step");
            i.putExtra("Status","true");

        }
    }


    @Override
    public void onDestroy() {
    Intent i = new Intent(this, MainService.InfoReceiver.class);
    i.putExtra("Action","Step");
    i.putExtra("Status","false");
        super.onDestroy();

    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        int steps = -1;
        int lastSteps = 0;

        if (values.length > 0) {
            steps = (int) values[0];
        }

        if(steps > (lastSteps + 50)) {
            Intent i = new Intent(this, MainService.InfoReceiver.class);
            i.putExtra("Action", "StepsTaken");
            i.putExtra("StepCount", steps);
        }
        lastSteps = steps;

    }





    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}