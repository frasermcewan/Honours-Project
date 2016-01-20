package com.example.mcewans_lager.honoursproject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by mcewans_lager on 20/01/16.
 */
public class MainActivity extends FragmentActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        createButtons();
    }

    protected void createButtons() {
        Button GPSbutton = (Button) findViewById(R.id.button1);
        Button WIFIbutton = (Button) findViewById(R.id.button2);
        Button Stepbutton = (Button) findViewById(R.id.button3);

        GPSbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RouteFinding.class));
            }
        });

        WIFIbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Wifi.class));
            }
        });

        Stepbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StepCounter.class));
            }
        });

    }
}
