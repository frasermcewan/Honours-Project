package com.example.mcewans_lager.honoursproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by mcewans_lager on 01/03/16.
 */
public class TestMain extends FragmentActivity {



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Intent intent = new Intent(this,LocationService.class);
        startService(intent);
    }



}
