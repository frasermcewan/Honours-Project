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

/**
 * Created by mcewans_lager on 29/02/16.
 */
public class handleXML {
    private final String TAG = "Yo adrian, we did it!";
    private String PrepType;
    private String PrepVolume;
    private String WindSpeed;
    private String tempMax;
    private String tempMin;
    private String finalUrl;
    boolean finishParse = true;
    private XmlPullParserFactory xmlFactory;
    private static final String ns = null;


    public handleXML(String url) {
        this.finalUrl = url;
    }

    public void callAsync () {
        DownloadXmlTask dXML = new DownloadXmlTask();
        dXML.doInBackground();
    }

    public void setPrepType(String p) { this.PrepType = p;}

    public void setPrepVolume(String v) {this.PrepVolume = v;}

    public void setWindSpeed(String w) {this.WindSpeed = w;}

    public void setTempMax(String tMax) {this.tempMax = tMax;}

    public void setTempMin(String tMin) {this.tempMin = tMin;}

    public String getPrepType() {
        return PrepType;
    }

    public String getPrepVolume() {
        return PrepVolume;
    }

    public String getWindSpeed() {
        return WindSpeed;
    }

    public String getTempMax() {
        return tempMax;
    }

    public String getTempMin() {
        return tempMin;
    }




    public void XMLParse(XmlPullParser parser) {
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("precipitation")) {
                    if (!parser.getAttributeValue(1).equals(null)) {
                        PrepVolume = parser.getAttributeValue(1);
                        PrepType = parser.getAttributeValue(2);
                    }
                } else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("windSpeed")) {
                    WindSpeed = parser.getAttributeName(0);
                } else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("temperature")) {
                    tempMax = parser.getAttributeName(3);
                    tempMin = parser.getAttributeValue(2);
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