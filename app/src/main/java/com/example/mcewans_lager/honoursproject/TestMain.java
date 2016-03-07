package com.example.mcewans_lager.honoursproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by mcewans_lager on 01/03/16.
 */
public class TestMain extends FragmentActivity {

    EditText buckysInput;
    TextView buckysText;
    DBHandler dbHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        buckysInput = (EditText) findViewById(R.id.buckysInput);
        buckysText = (TextView) findViewById(R.id.testText);
        dbHandler = new DBHandler(this, null, null, 1);
        printDatabase();
    }

    //Add a product to the database
    public void addButtonClicked(View view){
       Signatures signatures = new Signatures(buckysInput.getText().toString());
        dbHandler.addLocation(signatures);
        printDatabase();
    }

    //Delete items
    public void deleteButtonClicked(View view){
        String inputText = buckysInput.getText().toString();
        dbHandler.deleteLocation(inputText);
        printDatabase();
    }

    //Print the database
    public void printDatabase(){
        String dbString = dbHandler.databaseToString();
        buckysText.setText(dbString);
        buckysInput.setText("");
    }




}
