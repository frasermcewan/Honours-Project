package com.example.mcewans_lager.honoursproject;

/**
 * Created by mcewans_lager on 17/02/16.
 */
public class XMLCollection {
    double templ = 0.0;
    double temph = 0.0;
    int volume = 0;
    double windspeed = 0.0;
    String Precip = null;




   public void setTempLow(double Temps1) {
       templ = Temps1;
   }

    public void setTempHigh(double Temps2) {
        temph = Temps2;
    }

    public void setVolume(int Vol) {
        volume = Vol;
    }


   public void setPrecip(String p) {
       Precip = p;
   }

    public void setWindspeed(double w) {
        windspeed = w;
    }

    public double getWindspeed() {
        return windspeed;
    }

    public String getPrecip() {
        return Precip;
    }

    public int getVol () {
        return volume;
    }


}
