package com.example.mcewans_lager.honoursproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;


/**
 * Created by mcewans_lager on 20/01/16.
 */
public class NewMain extends FragmentActivity {

    private static String targetURL = "http://api.openweathermap.org/data/2.5/forecast?q=Glasgow&mode=xml&APPID=b33efea24270c8a29ff5678f3730ecb2";
    private static String secondURL = "http://api.openweathermap.org/data/2.5/forecast?q=Miami&mode=xml&APPID=b33efea24270c8a29ff5678f3730ecb2";
    private final String TAG = "Main";
    boolean done = false;
    private XmlPullParserFactory xmlFactory;
    private ArrayList<String> PrepType = new ArrayList<String>();
    private ArrayList<String> PrepVolume = new ArrayList<String>();
    private ArrayList<String> WindSpeed = new ArrayList<String>();
    private ArrayList<String> tempMax = new ArrayList<String>();
    private ArrayList<String> tempMin = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getWeather();

    }


    public void setPrepType(String p) { PrepType.add(p)  ;}

    public void setPrepVolume(String v) {PrepVolume.add(v);}

    public void setWindSpeed(String w) {WindSpeed.add(w);}

    public void setTempMax(String tMax) {tempMax.add(tMax);}

    public void setTempMin(String tMin) {tempMin.add(tMin);}

    public void getWeather() {
       handleXML handle = new handleXML(secondURL);
        handle.getXML();

//       DownloadXmlTask task = new DownloadXmlTask();
//        task.execute();
        

    }


    private class DownloadXmlTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {


            try {
                Log.i(TAG, "doInBackground: ");
                URL url = new URL(secondURL);
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


                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    done = false;
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                    } else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("precipitation")) {
                        if (parser.getAttributeCount() > 0) {
                            setPrepType(parser.getAttributeValue(2));
                            setPrepVolume(parser.getAttributeValue(1));
                        } else {

                        }
                    } else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("windSpeed")) {
                        if (parser.getAttributeCount() > 0) {
                            setWindSpeed(parser.getAttributeName(0));
                        }
                    } else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("temperature")) {
                      if   (parser.getAttributeCount() > 0) {
                          setTempMax(parser.getAttributeName(3));
                          setTempMin(parser.getAttributeValue(2));
                      }
                        Log.i(TAG, "Temp Min " + tempMin);
                    }

                    eventType = parser.next();
                }
                in.close();


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            
        }


    }



}




