package com.example.mcewans_lager.honoursproject;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mcewans_lager on 29/02/16.
 */
public class handleXML {
    private final String TAG = "Yo adrian, we did it!";
    private ArrayList<String> PrepType;
    private ArrayList<String> PrepVolume;
    private ArrayList<String> WindSpeed;
    private ArrayList<String> tempMax;
    private ArrayList<String> tempMin;
    private String finalUrl;
    boolean finishParse = true;
    private XmlPullParserFactory xmlFactory;


    public handleXML(String url) {
        this.finalUrl = url;
    }

    public void callAsync () {
        DownloadXmlTask dXML = new DownloadXmlTask();
        dXML.doInBackground();
    }

    public void setPrepType(String p) { PrepType.add(p)  ;}

    public void setPrepVolume(String v) {PrepVolume.add(v);}

    public void setWindSpeed(String w) {WindSpeed.add(w);}

    public void setTempMax(String tMax) {tempMax.add(tMax);}

    public void setTempMin(String tMin) {tempMin.add(tMin);}

    public ArrayList<String> getPrepType() {

        return PrepType;
    }

    public ArrayList<String> getPrepVolume() {

        return PrepVolume;
    }

    public ArrayList<String> getWindSpeed() {
        return WindSpeed;
    }

    public ArrayList<String> getTempMax() {
        return tempMax;
    }

    public ArrayList<String> getTempMin() {
        return tempMin;
    }




    public void XMLParse(XmlPullParser parser) {
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("precipitation")) {
                    if (parser.getAttributeCount() > 0) {
                        Log.i(TAG, "XMLParse: " + PrepVolume);
                        setPrepType(parser.getAttributeValue(2));
                        setPrepVolume(parser.getAttributeValue(1));
                    } else {

                    }
                } else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("windSpeed")) {
                    if (parser.getAttributeCount() > 0) {
                        setWindSpeed(parser.getAttributeName(0));
                    }
                } else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("temperature")) {

                    setTempMax(parser.getAttributeName(3));
                    setTempMin(parser.getAttributeValue(2));
                    Log.i(TAG, "Temp Min " + tempMin);
                }

                eventType = parser.next();
            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void getXML() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(finalUrl);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(10000 /* milliseconds */);
                    con.setConnectTimeout(15000 /* milliseconds */);
                    con.setRequestMethod("GET");
                    con.setDoInput(true);
                    con.connect();


                    InputStream in = con.getInputStream();
                    xmlFactory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = xmlFactory.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(in, null);
                    XMLParse(parser);
                    in.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {

                URL url = new URL(finalUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(10000 /* milliseconds */);
                con.setConnectTimeout(15000 /* milliseconds */);
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.connect();


                InputStream in = con.getInputStream();
                xmlFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = xmlFactory.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                XMLParse(parser);
                in.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }
    }