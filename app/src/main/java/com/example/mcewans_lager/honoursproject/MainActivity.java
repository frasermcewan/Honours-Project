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

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by mcewans_lager on 20/01/16.
 */
public class MainActivity extends FragmentActivity {

    private static String targetURL = "api.openweathermap.org/data/2.5/forecast?q=Glasgow&mode=xml&APPID=b33efea24270c8a29ff5678f3730ecb2";
//    private static String APIKey = "&APPID=b33efea24270c8a29ff5678f3730ecb2";
    URL Weather;

    public MainActivity() {
        try {
            Weather = new URL(targetURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getWeather();

    }


    public String getWeather() {


            try {
                SAXParserFactory spx = SAXParserFactory.newInstance();
                SAXParser sp = spx.newSAXParser();
                XMLReader xr = sp.getXMLReader();

                XMLHandler handle = new XMLHandler();
                xr.setContentHandler(handle);
                xr.parse(new InputSource(Weather.openStream()));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

        return null;
    }

    }




