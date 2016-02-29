package com.example.mcewans_lager.honoursproject;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



/**
 * Created by mcewans_lager on 17/02/16.
 */
public class XMLHandler extends DefaultHandler {

    protected static final String TAG = "MainThing";
    String PrepType;
    int PrepVolume;
    double WindSpeed;
    double tempMax;
    double tempMin;




public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    Log.i(TAG, "startElement: ");

        if (localName.equals("precipitation")) {
            PrepType = attributes.getValue("type");
            PrepVolume = Integer.parseInt(attributes.getValue("value"));

        }

        if (localName.equals("windSpeed")) {
            WindSpeed = Double.parseDouble(attributes.getValue("mps"));

        }
        if (localName.equals("temperature")) {
            tempMin = Double.parseDouble(attributes.getValue("min"));
            tempMax = Double.parseDouble(attributes.getValue("max"));
        }


//            xcol.setPrecip(PrepType);
//            xcol.setVolume(PrepVolume);
//            xcol.setWindspeed(WindSpeed);
//            xcol.setTempLow(tempMin);
//            xcol.setTempHigh(tempMax);


            }

    public double getWindspeed() {
        return WindSpeed;
    }

    public String getPrecip() {
        return PrepType;
    }

    public int getVol () {
        return PrepVolume;
    }

    public double getLo() {
        return tempMin;
    }

    public double getHi () {
        return tempMax;
    }



    }



