package com.example.mcewans_lager.honoursproject;

import android.accounts.AccountAuthenticatorResponse;
import android.app.IntentService;
import android.content.Intent;
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
 * This class provides more contextual information through the use of Weather, it does this by downloading weather
 * information in XML format which it then parses and returns back to main
 */

public class weatherIntentService extends IntentService {

    protected static final String TAG = "WeatherService";
    private static String secondURL = "http://api.openweathermap.org/data/2.5/forecast?q=Glasgow&mode=xml&APPID=b33efea24270c8a29ff5678f3730ecb2";
    sortXML handle;
    ArrayList<String> PrepType = new ArrayList<String>();
    ArrayList<String> PrepVolume = new ArrayList<String>();
    ArrayList<String> WindSpeed = new ArrayList<String>();
    ArrayList<String> tempMax = new ArrayList<String>();
    ArrayList<String> tempMin = new ArrayList<String>();

    public weatherIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntentWeather: ");
        handle = new sortXML(secondURL);
        handle.getXML();

        while (!handle.done) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
        }

        if (handle.done) {
            callMainService();
        }


    }

    private void callMainService() {

        Intent l = new Intent(this, MainService.class);
        l.putExtra("Action", "Weather");
        l.putExtra("PrepType", new ArrayListWrapper(PrepType));
        l.putExtra("PrepVolume", new ArrayListWrapper(PrepVolume));
        l.putExtra("WindSpeed", new ArrayListWrapper(WindSpeed));
        l.putExtra("tempMax", new ArrayListWrapper(tempMax));
        l.putExtra("tempMin", new ArrayListWrapper(tempMin));
        startService(l);

    }

    class sortXML {
        private String finalUrl;
        boolean done = false;
        private XmlPullParserFactory xmlFactory;


        public sortXML(String url) {
            this.finalUrl = url;
        }


        public void setPrepType(String p) {
            PrepType.add(p);
        }

        public void setPrepVolume(String v) {
            PrepVolume.add(v);
        }

        public void setWindSpeed(String w) {
            WindSpeed.add(w);
        }

        public void setTempMax(String tMax) {
            tempMax.add(tMax);
        }

        public void setTempMin(String tMin) {
            tempMin.add(tMin);
        }

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
                            setPrepType(parser.getAttributeValue(2));
                            setPrepVolume(parser.getAttributeValue(1));

                        } else if (parser.getAttributeCount() == 0) {
                            setPrepType("0");
                            setPrepVolume("0");
                        }
                    } else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("windSpeed")) {
                        if (parser.getAttributeCount() > 0) {
                            setWindSpeed(parser.getAttributeValue(0));
                        } else if (parser.getAttributeCount() == 0) {
                            setWindSpeed("0");
                        }
                    } else if (eventType == XmlPullParser.START_TAG && parser.getName().equals("temperature")) {
                        if (parser.getAttributeCount() > 0) {
                            setTempMax(parser.getAttributeValue(3));
                            setTempMin(parser.getAttributeValue(2));
                        } else if (parser.getAttributeCount() == 0) {
                            setTempMax("0");
                            setTempMin("0");
                        }

                    }

                    eventType = parser.next();
                }
                done = true;
                Log.i(TAG, "XMLParse: ");

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * Initially i wanted this thread to behave as an AsyncTask but it proved too difficult to have an AsyncTask nested inside
         * a Intent Service as the Intent Service would always finish before the AsyncTask returned, as a result i create a new thread manually
         * and do my work within that.
         */

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


    }
}


