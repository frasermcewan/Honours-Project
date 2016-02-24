package com.example.mcewans_lager.honoursproject;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



/**
 * Created by mcewans_lager on 17/02/16.
 */
public class XMLHandler extends DefaultHandler {


    XMLCollection xcol = new XMLCollection();
    boolean data = false;
    String PrepType;
    int PrepVolume;
    double WindSpeed;
    double tempMax;
    double tempMin;


public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("precipitation")) {
            PrepType = attributes.getValue("type");

            if (!PrepType.equals(null)) {
                PrepVolume = Integer.parseInt(attributes.getValue("value"));
                data = true;
            }
        }
        while (data == true) {
        if (localName.equals("windSpeed")) {
            WindSpeed = Double.parseDouble(attributes.getValue("mps"));

        }
        if (localName.equals("temperature")) {
            tempMin = Double.parseDouble(attributes.getValue("min"));
            tempMax = Double.parseDouble(attributes.getValue("max"));
        }


            xcol.setPrecip(PrepType);
            xcol.setVolume(PrepVolume);
            xcol.setWindspeed(WindSpeed);
            xcol.setTemp(tempMin,tempMax);
            data = false;

            }


    }
}


